package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.Converters;
import com.generalbytes.batm.server.extensions.ICanSendMany;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.CryptXException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.CryptXSendTransactionRequest;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.HeaderParam;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.ICryptXAPI.*;

public class CryptXWallet implements IWallet, ICanSendMany {

    private static final Logger log = LoggerFactory.getLogger(CryptXWallet.class);

    protected final ICryptXAPI api;
    protected String walletId;
    private String customFeePrice;
    private String customGasLimit;
    protected String url;
    protected static final Integer readTimeout = 90 * 1000;
    private int priority;
    protected String passphrase;

    public CryptXWallet(String scheme, String host, int port, String token, String walletId, String priority, String customFeePrice, String customGasLimit, String passphrase) {
        this.walletId = walletId;
        this.customFeePrice = customFeePrice;
        this.customGasLimit = customGasLimit;
        this.passphrase = passphrase;
        this.url = new HttpUrl.Builder().scheme(scheme).host(host).port(port).build().toString();

        if (priority == null) {
            this.priority = 2;
        } else if (PRIORITY_LOW.equalsIgnoreCase(priority.trim())) {
            this.priority = 24;
        } else if (PRIORITY_MEDIUM.equalsIgnoreCase(priority.trim())) {
            this.priority = 8;
        } else if (PRIORITY_HIGH.equalsIgnoreCase(priority.trim())) {
            this.priority = 2;
        } else {
            this.priority = 2;
        }

        ClientConfig config = new ClientConfig();
        config.setHttpReadTimeout(readTimeout);

        config.addDefaultParam(HeaderParam.class, "Authorization", "Bearer " + token);

        api = RestProxyFactory.createProxy(ICryptXAPI.class, this.url, config);
    }

    @Override
    public String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description) {
        List<CryptXSendTransactionRequest.AddressValuePair> addressList = transfers.stream()
                .map(transfer -> new CryptXSendTransactionRequest.AddressValuePair(transfer.getDestinationAddress(), toMinorUnit(cryptoCurrency, transfer.getAmount())))
                .collect(Collectors.toList());
        CryptXSendTransactionRequest sendTransactionRequest = new CryptXSendTransactionRequest(addressList, description, priority, customFeePrice, customGasLimit, passphrase);
        return sendCryptXTransaction(cryptoCurrency, sendTransactionRequest);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        CryptXSendTransactionRequest sendTransactionRequest = new CryptXSendTransactionRequest(destinationAddress, toMinorUnit(cryptoCurrency, amount), description, priority, customFeePrice, customGasLimit, passphrase);
        return sendCryptXTransaction(cryptoCurrency, sendTransactionRequest);
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.warn("{} not supported", cryptoCurrency);
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();
        try {
            String apiCryptocurrency = getAPICryptocurrency(cryptoCurrency);
            final Map<String, Object> wallet = api.getWallet(apiCryptocurrency, this.walletId, false);
            return (String) wallet.get("defaultAddress");
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoAddress error: {}", hse.getHttpBody());
        } catch (CryptXException e) {
            log.debug("getCryptoAddress error: {}", e.getErrorMessage());
        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> coins = new HashSet<>();
        coins.add(CryptoCurrency.BTC.getCode());
        coins.add(CryptoCurrency.LTC.getCode());
        coins.add(CryptoCurrency.BCH.getCode());
        coins.add(CryptoCurrency.ETH.getCode());
        coins.add(CryptoCurrency.USDT.getCode());
        coins.add(CryptoCurrency.BTBS.getCode());
        coins.add(CryptoCurrency.TBTC.getCode());
        coins.add(CryptoCurrency.TLTC.getCode());
        coins.add(CryptoCurrency.TBCH.getCode());
        coins.add(CryptoCurrency.TETH.getCode());
        return coins;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.warn("{} not supported", cryptoCurrency);
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();

        try {
            final Balance balance = api.getWalletBalance(cryptoCurrency, this.walletId, false);
            if (balance == null) {
                return null;
            }

            BigInteger spendableBalance = balance.getSpendableBalance();

            return toMajorUnit(cryptoCurrency, spendableBalance.toString());
        } catch (HttpStatusIOException hse) {
            log.debug("getCryptoBalance error: {}", hse.getHttpBody());
        } catch (CryptXException e) {
            log.debug("getCryptoBalance error: {}", e.getErrorMessage());
        } catch (Exception e) {
            log.error("getCryptoBalance error", e);
        }

        return null;
    }

    public String getAPICryptocurrency(String cryptoCurrency) {
        if (cryptoCurrency.equalsIgnoreCase(CryptoCurrency.USDT.getCode())) {
            return CryptoCurrency.ETH.getCode();
        }
        return cryptoCurrency;
    }

    private String toMinorUnit(String cryptoCurrency, BigDecimal amount) {
        try {
            switch (CryptoCurrency.valueOfCode(cryptoCurrency)) {
                case TBTC:
                case BTC:
                    return amount.multiply(Converters.BTC).toBigInteger().toString();
                case TLTC:
                case LTC:
                    return amount.multiply(Converters.LTC).toBigInteger().toString();
                case TBCH:
                case BCH:
                    return amount.multiply(Converters.BCH).toBigInteger().toString();
                case TETH:
                case ETH:
                    return amount.multiply(Converters.ETH).toBigInteger().toString();
                case BTBS:
                    return amount.multiply(Converters.BTBS).toBigInteger().toString();
                case USDT:
                    return amount.multiply(Converters.USDT).toBigInteger().toString();
                default:
                    return amount.toBigInteger().toString();
            }
        } catch (IllegalArgumentException e) {
            return amount.toBigInteger().toString();
        }
    }

    private String sendCryptXTransaction(String cryptoCurrency, CryptXSendTransactionRequest sendTransactionRequest) {
        try {
            Map<String, Object> response = api.sendTransaction(cryptoCurrency.toLowerCase(), this.walletId, sendTransactionRequest);
            return (String) response.get("txid");
        } catch (HttpStatusIOException hse) {
            log.debug("send coins error - HttpStatusIOException, error message: {}, HTTP code: {}, HTTP content: {}", hse.getMessage(), hse.getHttpStatusCode(), hse.getHttpBody());
        } catch (CryptXException e) {
            log.debug("send coins error message: {}", e.getErrorMessage());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    private BigDecimal toMajorUnit(String cryptoCurrency, String amount) {
        BigInteger bigIntegerAmount = new BigInteger(amount);
        switch (CryptoCurrency.valueOfCode(cryptoCurrency)) {
            case TBTC:
            case BTC:
            case TLTC:
            case LTC:
            case TBCH:
            case BCH:
                return new BigDecimal(bigIntegerAmount).movePointLeft(8);
            case TETH:
            case ETH:
            case BTBS:
                return new BigDecimal(bigIntegerAmount).movePointLeft(18);
            case USDT:
                return new BigDecimal(bigIntegerAmount).movePointLeft(6);
            default:
                throw new IllegalArgumentException("Unsupported crypto currency");
        }
    }

    public int getPriority() {
        return priority;
    }

    public String getCustomFeePrice() {
        return customFeePrice;
    }

    public String getCustomGasLimit() {
        return customGasLimit;
    }

    public String getPassphrase() {
        return passphrase;
    }
}
