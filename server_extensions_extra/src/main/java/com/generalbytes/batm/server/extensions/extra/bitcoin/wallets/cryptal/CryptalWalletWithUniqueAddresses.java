package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalPage;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalTransaction;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CryptalWalletWithUniqueAddresses extends CryptalWallet implements IGeneratesNewDepositCryptoAddress, IQueryableWallet {

    private static final int CONFIRMED = 999;
    private static final int MAX_ISSUED_ADDRESSES = 10_000;

    private final Map<String, Set<String>> depositsBeforeIssue = Collections.synchronizedMap(
        new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Set<String>> eldest) {
                return size() > MAX_ISSUED_ADDRESSES;
            }
        });

    public CryptalWalletWithUniqueAddresses(CryptalWalletConfig config) {
        super(config);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        Optional<CryptalCoin> coin = coin(cryptoCurrency);
        if (coin.isEmpty()) {
            return null;
        }
        String address = createAddress(coin.get(), toAddressName(label));
        if (address == null) {
            return null;
        }
        try {
            Set<String> earlier = deposits(coin.get(), address, null).stream()
                .map(CryptalTransaction::uuid)
                .collect(Collectors.toSet());
            depositsBeforeIssue.put(key(coin.get(), address), earlier);
            return address;
        } catch (Exception e) {
            logError("generateNewDepositCryptoAddress " + address, cryptoCurrency, e);
            return null;
        }
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        Optional<CryptalCoin> coin = coin(cryptoCurrency);
        if (coin.isEmpty()) {
            return ReceivedAmount.ZERO;
        }
        try {
            Set<String> earlier = depositsBeforeIssue.getOrDefault(key(coin.get(), address), Set.of());
            BigDecimal total = BigDecimal.ZERO;
            List<String> hashes = new ArrayList<>();
            for (CryptalTransaction deposit : deposits(coin.get(), address, "SUCCESS")) {
                if (deposit.amount() == null || (deposit.uuid() != null && earlier.contains(deposit.uuid()))) {
                    continue;
                }
                total = total.add(deposit.amount());
                if (deposit.transactionId() != null) {
                    hashes.add(deposit.transactionId());
                }
            }
            ReceivedAmount receivedAmount = new ReceivedAmount(total, total.signum() > 0 ? CONFIRMED : 0);
            receivedAmount.setTransactionHashes(hashes);
            return receivedAmount;
        } catch (Exception e) {
            logError("getReceivedAmount", cryptoCurrency, e);
            return ReceivedAmount.ZERO;
        }
    }

    private List<CryptalTransaction> deposits(CryptalCoin coin, String address, String status) throws Exception {
        List<CryptalTransaction> deposits = new ArrayList<>();
        int page = 0;
        while (true) {
            CryptalPage<CryptalTransaction> result = api.getTransactions(apiKey, now(), signature,
                address, coin.getCurrency(), "RECEIVE", status, page, PAGE_SIZE);
            if (result == null || result.content() == null) {
                return deposits;
            }
            deposits.addAll(result.content());
            if (result.content().size() < PAGE_SIZE) {
                return deposits;
            }
            page++;
        }
    }

    private static String key(CryptalCoin coin, String address) {
        return coin.getCurrency() + ":" + address;
    }
}
