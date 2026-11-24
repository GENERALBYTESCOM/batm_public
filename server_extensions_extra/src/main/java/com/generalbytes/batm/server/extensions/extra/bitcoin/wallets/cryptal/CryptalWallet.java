package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalApiException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalBalance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalPage;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalSendRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalSendResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CryptalWallet implements IWallet {

    private static final Logger log = LoggerFactory.getLogger(CryptalWallet.class);

    static final int PAGE_SIZE = 200;
    private static final int READ_TIMEOUT_MILLIS = 90_000;
    private static final int MAX_REFERENCE_LENGTH = 64;
    private static final int MAX_ADDRESS_NAME_LENGTH = 30;
    private static final String UNIQUE_ID_USED = "wallet.pro.transaction-unique-id-used";

    protected final ICryptalWalletProApi api;
    protected final String apiKey;
    protected final CryptalSignatureDigest signature;
    private final String addressName;
    private final Map<CryptalCoin, String> fixedAddresses = new ConcurrentHashMap<>();

    public CryptalWallet(CryptalWalletConfig config) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setHttpReadTimeout(READ_TIMEOUT_MILLIS);
        this.api = RestProxyFactory.createProxy(ICryptalWalletProApi.class, config.baseUrl(), clientConfig);
        this.apiKey = config.apiKey();
        this.signature = new CryptalSignatureDigest(config.privateKey());
        this.addressName = toAddressName(config.addressName());
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CryptalCoin.gbCodes();
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        Optional<CryptalCoin> coin = coin(cryptoCurrency);
        if (coin.isEmpty()) {
            return null;
        }
        try {
            CryptalBalance balance = api.getSendableBalance(apiKey, now(), signature, coin.get().getCurrency());
            return balance == null ? null : balance.total();
        } catch (Exception e) {
            logError("getCryptoBalance", cryptoCurrency, e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        Optional<CryptalCoin> coin = coin(cryptoCurrency);
        if (coin.isEmpty()) {
            return null;
        }
        String cached = fixedAddresses.get(coin.get());
        if (cached != null) {
            return cached;
        }
        try {
            String address = findAddressByName(coin.get(), addressName)
                .orElseGet(() -> createAddress(coin.get(), addressName));
            if (address != null) {
                fixedAddresses.put(coin.get(), address);
            }
            return address;
        } catch (Exception e) {
            logError("getCryptoAddress", cryptoCurrency, e);
            return null;
        }
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        Optional<CryptalCoin> coin = coin(cryptoCurrency);
        if (coin.isEmpty()) {
            return null;
        }
        CryptalSendRequest request = new CryptalSendRequest(
            coin.get().getCurrency(),
            coin.get().getNetwork(),
            destinationAddress,
            coin.get().toApiAmount(amount),
            false,
            toClientReference(description));
        try {
            CryptalSendResponse response = api.send(apiKey, now(), signature, request);
            if (response == null) {
                return null;
            }
            if (response.txHash() != null && !response.txHash().isBlank()) {
                return response.txHash();
            }
            return response.uuid();
        } catch (CryptalApiException e) {
            if (UNIQUE_ID_USED.equals(e.getMessageCode()) && request.uniqueId() != null) {
                return findEarlierPayout(request.uniqueId(), cryptoCurrency);
            }
            logError("sendCoins " + request.uniqueId(), cryptoCurrency, e);
            return null;
        } catch (Exception e) {
            logError("sendCoins " + request.uniqueId(), cryptoCurrency, e);
            return null;
        }
    }

    protected Optional<CryptalCoin> coin(String cryptoCurrency) {
        Optional<CryptalCoin> coin = CryptalCoin.of(cryptoCurrency == null ? getPreferredCryptoCurrency() : cryptoCurrency);
        if (coin.isEmpty()) {
            log.warn("{} is not supported by Cryptal Wallet Pro", cryptoCurrency);
        }
        return coin;
    }

    protected String createAddress(CryptalCoin coin, String name) {
        try {
            CryptalAddress address = api.createAddress(apiKey, now(), signature,
                new CryptalCreateAddressRequest(coin.getCurrency(), coin.getNetwork(), name));
            return address == null ? null : address.address();
        } catch (Exception e) {
            logError("createAddress", coin.name(), e);
            return null;
        }
    }

    private String findEarlierPayout(String uniqueId, String cryptoCurrency) {
        try {
            CryptalPage<CryptalTransaction> payouts = api.getTransactionsByUniqueId(apiKey, now(), signature, uniqueId, "SEND", 1);
            Optional<CryptalTransaction> payout = payouts == null || payouts.content() == null
                ? Optional.empty()
                : payouts.content().stream().filter(t -> uniqueId.equals(t.uniqueId())).findFirst();
            if (payout.isEmpty() || "FAILED".equals(payout.get().status())) {
                log.error("Cryptal Wallet Pro payout {} {} was sent before and is {}; not reported as paid", uniqueId,
                    cryptoCurrency, payout.map(CryptalTransaction::status).orElse("not found"));
                return null;
            }
            log.info("Cryptal Wallet Pro payout {} {} was sent before ({}); reporting that payout", uniqueId, cryptoCurrency,
                payout.get().status());
            String hash = payout.get().transactionId();
            return hash != null && !hash.isBlank() ? hash : payout.get().uuid();
        } catch (Exception e) {
            logError("findEarlierPayout " + uniqueId, cryptoCurrency, e);
            return null;
        }
    }

    private Optional<String> findAddressByName(CryptalCoin coin, String name) throws Exception {
        int page = 0;
        while (true) {
            CryptalPage<CryptalAddress> addresses = api.getAddresses(apiKey, now(), signature,
                coin.getCurrency(), coin.getNetwork(), name, page, PAGE_SIZE);
            if (addresses == null || addresses.content() == null || addresses.content().isEmpty()) {
                return Optional.empty();
            }
            Optional<String> match = addresses.content().stream()
                .filter(a -> name.equals(a.name()))
                .map(CryptalAddress::address)
                .findFirst();
            if (match.isPresent() || addresses.content().size() < PAGE_SIZE) {
                return match;
            }
            page++;
        }
    }

    static String toClientReference(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        String reference = description.trim().replaceAll("[^A-Za-z0-9._:-]+", "-");
        return reference.length() > MAX_REFERENCE_LENGTH ? reference.substring(0, MAX_REFERENCE_LENGTH) : reference;
    }

    static String toAddressName(String label) {
        String name = label == null ? "" : label.replaceAll("[^A-Za-z0-9 ]+", " ").replaceAll(" +", " ").trim();
        if (name.length() > MAX_ADDRESS_NAME_LENGTH) {
            name = name.substring(0, MAX_ADDRESS_NAME_LENGTH).trim();
        }
        return name.isEmpty() ? CryptalWalletConfig.DEFAULT_ADDRESS_NAME : name;
    }

    protected static long now() {
        return System.currentTimeMillis();
    }

    protected static void logError(String action, String cryptoCurrency, Exception e) {
        if (e instanceof CryptalApiException apiException) {
            log.error("Cryptal Wallet Pro {} {} failed: HTTP {} {}", action, cryptoCurrency, apiException.getHttpStatusCode(), apiException.getMessage());
        } else if (e instanceof HttpStatusIOException httpException) {
            log.error("Cryptal Wallet Pro {} {} failed: HTTP {} {}", action, cryptoCurrency, httpException.getHttpStatusCode(), httpException.getHttpBody());
        } else {
            log.error("Cryptal Wallet Pro {} {} failed", action, cryptoCurrency, e);
        }
    }
}
