/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsResponse;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class CoinbaseExchange implements IRateSourceAdvanced, IExchangeAdvanced {

    private static final String CB_VERSION = "2017-05-19";
    private static final String CREDIT_CARD = "credit_card";

    private static final Logger log = LoggerFactory.getLogger("batm.master.CoinbaseExchange");

    private static final Set<String> FIAT_CURRENCIES = new HashSet<>();
    private static final Set<String> CRYPTO_CURRENCIES = new HashSet<>();

    private ICoinbaseAPILegacy api;

    private String apiKey;
    private String secretKey;
    private String accountName;
    private String preferedFiatCurrency;
    private String paymentMethodName;

    static {
        initConstants();
    }

    private static void initConstants() {
        FIAT_CURRENCIES.add(FiatCurrency.AUD.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.CAD.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.CNY.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.CZK.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.EUR.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.GBP.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.SGD.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.USD.getCode());

        CRYPTO_CURRENCIES.add(CryptoCurrency.ADA.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.BAT.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.BCH.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.BTC.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.ETC.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.ETH.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.LTC.getCode());
        CRYPTO_CURRENCIES.add(CryptoCurrency.XRP.getCode());
    }

    @SuppressWarnings("WeakerAccess")
    public CoinbaseExchange() {
        try {
            ClientConfig config = new ClientConfig();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
            api = RestProxyFactory.createProxy(ICoinbaseAPILegacy.class, "https://api.coinbase.com", config);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("constructor - Cannot create instance.", e);
        }
    }

    public CoinbaseExchange(String apiKey, String secretKey, String accountName, String preferedFiatCurrency, String paymentMethodName) {
        this();
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.accountName = accountName;
        this.preferedFiatCurrency = preferedFiatCurrency;
        this.paymentMethodName = paymentMethodName;
    }

    @Override
    public String getPreferredFiatCurrency() {
        if (preferedFiatCurrency != null && !preferedFiatCurrency.isEmpty()) {
            return preferedFiatCurrency;
        }
        return FiatCurrency.USD.getCode();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTO_CURRENCIES;
    }

    @Override
    public synchronized Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        try {
            log.debug("getCryptoBalance");
            cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
            if (cryptoCurrency != null) {
                CBAccount account = getAccount(accountName, cryptoCurrency);
                if (account != null) {
                    return new BigDecimal(account.balance.amount);
                } else {
                    log.error("getCryptoBalance (1) - No account.");
                }
            }
        } catch (Throwable e) {
            log.error("getCryptoBalance(2)", e);
        }
        return null;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        try {
            log.debug("getFiatBalance");
            fiatCurrency = checkFiatCurrency(fiatCurrency);
            if (fiatCurrency != null) {
                CBAccount account = getAccount(accountName, fiatCurrency);
                if (account != null) {
                    return new BigDecimal(account.balance.amount);
                } else {
                    log.error("fiatCurrency (1) - " + ("No account. fiatCurrency = " + fiatCurrency));
                }
            }
        } catch (Throwable e) {
            log.error("getFiatBalance (2)", e);
        }
        return null;
    }

    @Deprecated
    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRate(cryptoCurrency, fiatCurrency, "spot");
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRate(cryptoCurrency, fiatCurrency, "buy");
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRate(cryptoCurrency, fiatCurrency, "sell");
    }


    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        BigDecimal rate = getExchangeRate(cryptoCurrency, fiatCurrency, "buy");
        return (rate != null) ? rate.multiply(cryptoAmount).stripTrailingZeros() : null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        BigDecimal rate = getExchangeRate(cryptoCurrency, fiatCurrency, "sell");
        return (rate != null) ? rate.multiply(cryptoAmount).stripTrailingZeros() : null;
    }

    private BigDecimal getExchangeRate(String cryptoCurrency, String fiatCurrency, String priceType) {
        try {
            log.debug("getExchangeRate - " + priceType);
            String currencyPair = getCurrencyPair(cryptoCurrency, fiatCurrency);
            if (currencyPair != null) {
                RateLimiter.waitForPossibleCall(getClass());
                CBPriceResponse priceResponse = api.getPrice(CB_VERSION, currencyPair, priceType);
                if (priceResponse.errors == null) {
                    return new BigDecimal(priceResponse.data.amount);
                } else {
                    log.error("getExchangeRate - " + priceResponse.getErrorMessages());
                }
            }
        } catch (Throwable e) {
            log.error("getExchangeRate", e);
        }
        return null;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        try {
            String coinBaseTime = getTime();
            RateLimiter.waitForPossibleCall(getClass());
            CBNewAddressResponse newAddressResponse = api.getNewAddress(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), coinBaseTime, getAccountId(accountName, cryptoCurrency));
            if (newAddressResponse.errors == null) {
                return newAddressResponse.data.address;
            } else {
                log.error("getDepositAddress - " + newAddressResponse.getErrorMessages());
            }
        } catch (Throwable e) {
            log.error("getDepositAddress", e);
        }
        return null;
    }

    private String getMethodIdForCurrency(String currency, String payamentMethodName) {
        try {
            String coinBaseTime = getTime();
            RateLimiter.waitForPossibleCall(getClass());
            CBPaymentMethodsResponse paymentMethodsResponse = api.listPaymentMethods(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), coinBaseTime);
            log.debug("getMethodIdForCurrency - Payment methods: {}", paymentMethodsResponse);
            if (paymentMethodsResponse.errors == null) {
                for (CBPaymentMethodsResponse.CBPaymentMethod d : paymentMethodsResponse.data) {
                    if (currency.equalsIgnoreCase(d.currency)
                        && (payamentMethodName == null || payamentMethodName.equals(d.name))
                        && !CREDIT_CARD.equals(d.type)) {
                        log.debug("getMethodIdForCurrency - Selected payment method: {}", d);
                        return d.id;
                    }
                }
            } else {
                log.error("getMethodIdForCurrency - " + paymentMethodsResponse.getErrorMessages());
            }
        } catch (Throwable e) {
            log.error("getMethodIdForCurrency", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            if (CryptoCurrency.USDT.getCode().equals(cryptoCurrency) || CryptoCurrency.ADA.getCode().equals(cryptoCurrency)) {
                amount = amount.setScale(6, RoundingMode.FLOOR);
            }
            String coinBaseTime = getTime();
            CBSendCoinsRequest sendCoinsRequest = new CBSendCoinsRequest();
            sendCoinsRequest.to = destinationAddress;
            sendCoinsRequest.amount = amount.toPlainString();
            sendCoinsRequest.currency = cryptoCurrency.toUpperCase();
            sendCoinsRequest.description = description;
            RateLimiter.waitForPossibleCall(getClass());
            log.info("sending {} {} to {}", amount, cryptoCurrency, destinationAddress);
            CBSendCoinsResponse sendCoinsResponse = api.sendCoins(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), coinBaseTime, getAccountId(accountName, cryptoCurrency), sendCoinsRequest);
            if (sendCoinsResponse.errors == null) {
                return sendCoinsResponse.data.id;
            } else {
                log.error("sendCoins - " + sendCoinsResponse.getErrorMessages());
            }
        } catch (Throwable e) {
            log.error("sendCoins", e);
        }
        return null;
    }

    private String getBuyStatus(String buyId, String cryptoCurrency) {
        try {
            String coinBaseTime = getTime();
            RateLimiter.waitForPossibleCall(getClass());
            CBOrderResponse orderResponse = api.getBuyOrder(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), coinBaseTime, getAccountId(accountName, cryptoCurrency), buyId);
            if (orderResponse.errors == null) {
                return orderResponse.data.status;
            } else {
                log.error("getBuyStatus - " + orderResponse.getErrorMessages());
            }
        } catch (Throwable e) {
            log.error("getBuyStatus", e);
        }
        return null;
    }

    private String getSellStatus(String sellId, String cryptoCurrency) {
        try {
            String coinBaseTime = getTime();
            RateLimiter.waitForPossibleCall(getClass());
            CBOrderResponse orderResponse = api.getSellOrder(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), coinBaseTime, getAccountId(accountName, cryptoCurrency), sellId);
            if (orderResponse.errors == null) {
                return orderResponse.data.status;
            } else {
                log.error("getSellStatus - " + orderResponse.getErrorMessages());
            }
        } catch (Throwable e) {
            log.error("getSellStatus", e);
        }
        return null;
    }

    @Deprecated
    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        log.error("purchaseCoins - Deprecated. Use method IExchangeAdvanced.createPurchaseCoinsTask.");
        return null;
    }

    @Deprecated
    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        log.error("sellCoins - Deprecated. Use method IExchangeAdvanced.createSellCoinsTask.");
        return null;
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, String description) {
        log.debug("createPurchaseCoinsTask");
        cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
        fiatCurrency = checkFiatCurrency(fiatCurrency);
        if (cryptoCurrency != null && fiatCurrency != null) {
            return new PurchaseCoinsTask(amount, cryptoCurrency, fiatCurrency, description);
        }
        log.error("createPurchaseCoinsTask");
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, String description) {
        log.debug("createSellCoinsTask");
        cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
        fiatCurrency = checkFiatCurrency(fiatCurrency);
        if (cryptoCurrency != null && fiatCurrency != null) {
            return new SellCoinsTask(amount, cryptoCurrency, fiatCurrency, description);
        }
        log.error("createSellCoinsTask");
        return null;
    }

    class PurchaseCoinsTask implements ITask {

        private long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; // 5 hours
        private long createTime;

        private BigDecimal amount;
        private String cryptoCurrency;
        private String fiatCurrency;
        private String orderAId;
        private String result;
        private boolean finished;

        PurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, @SuppressWarnings("unused") String description) {
            this.amount = amount;
            this.fiatCurrency = fiatCurrency;
            this.cryptoCurrency = cryptoCurrency;
        }

        @Override
        public boolean onCreate() {
            try {
                String methodId = getMethodIdForCurrency(fiatCurrency, paymentMethodName);
                if (methodId == null) {
                    log.error("Payment method for currency " + fiatCurrency + " and name='" + paymentMethodName + "' is not available.");
                } else {
                    CBOrderRequest orderRequest = new CBOrderRequest();
                    orderRequest.total = amount.toPlainString();
                    orderRequest.currency = cryptoCurrency;
                    orderRequest.agree_btc_amount_varies = true;
                    orderRequest.commit = true;
                    orderRequest.quote = false;
                    orderRequest.payment_method = methodId;

                    String coinBaseTime = getTime();
                    RateLimiter.waitForPossibleCall(getClass());
                    CBOrderResponse orderResponse = api.buyCoins(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), coinBaseTime, getAccountId(accountName, cryptoCurrency), orderRequest);

                    if (orderResponse.errors == null) {
                        orderAId = orderResponse.data.id;
                        createTime = System.currentTimeMillis();
                        log.debug("PurchaseCoinsTask.onCreate - " + ("orderAId = " + orderAId));
                    } else {
                        log.error("PurchaseCoinsTask.onCreate - " + orderResponse.getErrorMessages());
                    }
                }
            } catch (Exception e) {
                log.error("PurchaseCoinsTask.onCreate", e);
            }
            return (orderAId != null);
        }

        @Override
        public boolean onDoStep() {
            if (orderAId == null) {
                log.debug("PurchaseCoinsTask.onDoStep - Giving up on waiting for trade to complete. Because it did not happen.");
                finished = true;
                result = "Skipped";
                return false;
            }

            long checkTillTime = createTime + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug("PurchaseCoinsTask.onDoStep - " + ("Giving up on waiting for trade " + orderAId + " to complete."));
                finished = true;
                return false;
            }

            String status = getBuyStatus(orderAId, cryptoCurrency);
            if ("completed".equals(status)) {
                result = orderAId;
                finished = true;
            }

            return result != null;
        }

        @Override
        public void onFinish() {
            log.debug("PurchaseCoinsTask.onFinish - Purchase task finished.");
        }

        @Override
        public boolean isFinished() {
            return finished;
        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public boolean isFailed() {
            return finished && result == null;
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 5 * 1000;
        }
    }

    class SellCoinsTask implements ITask {

        private long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; // 5 hours
        private long createTime;

        private BigDecimal amount;
        private String cryptoCurrency;
        private String fiatCurrency;
        private String orderAId;
        private String result;
        private boolean finished;

        SellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, @SuppressWarnings("unused") String description) {
            this.amount = amount;
            this.fiatCurrency = fiatCurrency;
            this.cryptoCurrency = cryptoCurrency;
        }

        @Override
        public boolean onCreate() {
            try {
                String methodId = getMethodIdForCurrency(fiatCurrency, paymentMethodName);
                if (methodId == null) {
                    log.error("Payment method for currency " + fiatCurrency + " is not available.");
                } else {
                    CBOrderRequest orderRequest = new CBOrderRequest();
                    orderRequest.total = amount.toPlainString();
                    orderRequest.currency = cryptoCurrency;
                    orderRequest.agree_btc_amount_varies = true;
                    orderRequest.commit = true;
                    orderRequest.quote = false;
                    orderRequest.payment_method = methodId;

                    String coinBaseTime = getTime();
                    RateLimiter.waitForPossibleCall(getClass());
                    CBOrderResponse orderResponse = api.sellCoins(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), coinBaseTime, getAccountId(accountName, cryptoCurrency), orderRequest);

                    if (orderResponse.errors == null) {
                        orderAId = orderResponse.data.id;
                        createTime = System.currentTimeMillis();
                        log.debug("SellCoinsTask.onCreate - " + ("orderAId = " + orderAId));
                    } else {
                        log.error("SellCoinsTask.onCreate - " + orderResponse.getErrorMessages());
                    }
                }
            } catch (Exception e) {
                log.error("SellCoinsTask.onCreate", e);
            }
            return (orderAId != null);
        }

        @Override
        public boolean onDoStep() {
            if (orderAId == null) {
                log.debug("SellCoinsTask.onDoStep - Giving up on waiting for trade to complete. Because it did not happen.");
                finished = true;
                result = "Skipped";
                return false;
            }

            long checkTillTime = createTime + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug("SellCoinsTask.onDoStep - " + ("Giving up on waiting for trade " + orderAId + " to complete."));
                finished = true;
                return false;
            }

            String status = getSellStatus(orderAId, cryptoCurrency);
            if ("completed".equals(status)) {
                result = orderAId;
                finished = true;
            }

            return result != null;
        }

        @Override
        public void onFinish() {
            log.debug("SellCoinsTask.onFinish - Sell task finished.");
        }

        @Override
        public boolean isFinished() {
            return finished;
        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public boolean isFailed() {
            return finished && result == null;
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 5 * 1000;
        }
    }

    private CBAccount getAccount(String accountName, String currency) throws IOException, TimeoutException {
        if (currency == null) {
            log.error("getAccount (1) - currency is null");
            return null;
        }
        final List<CBAccount> accounts = getAccounts();

        if (accountName != null) {
            for (CBAccount cbAccount : accounts) {
                if (accountName.equalsIgnoreCase(cbAccount.name)) {
                    if (currency.equalsIgnoreCase(cbAccount.currency.code)) {
                        return cbAccount;
                    }
                }
            }
        } else {
            for (CBAccount cbAccount : accounts) {
                if (cbAccount.primary) {
                    if (currency.equalsIgnoreCase(cbAccount.currency.code)) {
                        return cbAccount;
                    }
                }
            }
        }
        for (CBAccount cbAccount : accounts) {
            if (currency.equalsIgnoreCase(cbAccount.currency.code)) {
                return cbAccount;
            }
        }
        log.warn("no account found, accountName: {}, currency: {}", accountName, currency);
        return null;
    }


    /**
     * @return all accounts from the API using pagination
     */
    private List<CBAccount> getAccounts() throws IOException, TimeoutException {
        LinkedList<CBAccount> accounts = new LinkedList<>();
        String startingAfter = null; // start pagination from the beginning
        do {
            RateLimiter.waitForPossibleCall(getClass());
            log.debug("Getting accounts, startingAfter: {}", startingAfter);
            CBAccountsResponse accountsResponse = api.getAccounts(CB_VERSION, apiKey, CoinbaseDigest.createInstance(secretKey), getTime(), startingAfter);
            if (accountsResponse.errors != null) {
                throw new IllegalStateException(accountsResponse.getErrorMessages());
            }
            if (accountsResponse.warnings != null) {
                log.warn("getAccounts warning: {}", accountsResponse.getWarningMessages());
            }
            startingAfter = null;
            if (accountsResponse.data != null && accountsResponse.data.length > 0) {
                accounts.addAll(Arrays.asList(accountsResponse.data));
                if (accountsResponse.pagination != null && accountsResponse.pagination.next_uri != null) {
                    startingAfter = accounts.getLast().id;
                }
            }
        } while (startingAfter != null);
        return accounts;
    }

    private String getAccountId(String accountName, String currency) throws IOException, TimeoutException {
        CBAccount account = getAccount(accountName, currency);
        return (account != null) ? account.id : null;
    }

    private String checkCryptoCurrency(String cryptoCurrency) {
        if (cryptoCurrency == null || !CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
            log.error("checkCryptoCurrency - Crypto currency \"" + cryptoCurrency + "\" is not supported. Coinbase supports " + Arrays.toString(CRYPTO_CURRENCIES.toArray()) + ".");
            return null;
        }
        return cryptoCurrency.toUpperCase();
    }

    private String checkFiatCurrency(String fiatCurrency) {
        Set<String> fiatCurrencies = getFiatCurrencies();
        if (fiatCurrency == null || (fiatCurrencies != null && !fiatCurrencies.contains(fiatCurrency))) {
            String supports = (fiatCurrencies != null) ? Arrays.toString(fiatCurrencies.toArray()) : "N/A";
            log.error("checkFiatCurrency - Fiat currency \"" + fiatCurrency + "\" is not supported. Coinbase supports " + supports + ".");
            return null;
        }
        return fiatCurrency.toUpperCase();
    }

    private String getCurrencyPair(String cryptoCurrency, String fiatCurrency) {
        cryptoCurrency = checkCryptoCurrency(cryptoCurrency);
        fiatCurrency = checkFiatCurrency(fiatCurrency);
        if (cryptoCurrency != null && fiatCurrency != null) {
            return cryptoCurrency.toUpperCase() + "-" + fiatCurrency.toUpperCase();
        }
        return null;
    }

    private String getTime() {
        try {
            log.debug("getTime");
            RateLimiter.waitForPossibleCall(getClass());
            return String.valueOf(api.getTime(CB_VERSION).data.epoch);

        } catch (Exception e) {
            log.error("getTime", e);
            return "-1";
        }
    }

    @Override
    public String toString() {
        return String.format("accountName = %s, preferedFiatCurrency = %s, apiKey = %s",
            accountName, preferedFiatCurrency, apiKey);
    }

}
