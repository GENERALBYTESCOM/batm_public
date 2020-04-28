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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitflyer;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitflyer.dto.*;
import com.generalbytes.batm.server.extensions.extra.dash.sources.cddash.CompatSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SuppressWarnings({"Duplicates", "unused"})
public class BitFlyerExchange implements IRateSourceAdvanced, IExchangeAdvanced {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BitFlyerExchange");

    private static final HashMap<String, BigDecimal> rateAmounts = new HashMap<>();
    private static HashMap<String, Long> rateTimes = new HashMap<>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;
    public static final String BITFLYER_COM_BASE_URL = "https://api.bitflyer.com";
    public static final String BITFLYER_JP_BASE_URL = "https://api.bitflyer.jp";

    private static final Set<String> CRYPTO_CURRENCIES = new HashSet<>();
    private final Set<String> FIAT_CURRENCIES = new HashSet<>();
    private final Set<String> PRODUCT_CODES = new HashSet<>();

    private String preferredFiatCurrency;
    private String apiKey;
    private String secretKey;

    private IBitFlyerAPI api;

    private static volatile long lastCall = -1;
    public static final int CALL_PERIOD_MINIMUM = 2100; //Cannot be called more often than once in 2 seconds

    enum TradableLimitFor {BUY, SELL}

    static {
        CRYPTO_CURRENCIES.add(CryptoCurrency.BTC.getCode());
    }

    private void initConstants(String baseUrl) {
        if (baseUrl.equals(BITFLYER_COM_BASE_URL)) {
            FIAT_CURRENCIES.add(FiatCurrency.USD.getCode());
            PRODUCT_CODES.add(CryptoCurrency.BTC.getCode() + "_" + FiatCurrency.USD.getCode());
        }
        if (baseUrl.equals(BITFLYER_JP_BASE_URL)) {
            FIAT_CURRENCIES.add(FiatCurrency.JPY.getCode());
            PRODUCT_CODES.add(CryptoCurrency.BTC.getCode() + "_" + FiatCurrency.JPY.getCode());
        }
    }

    private String makeProductCode(String cryptoCurrency, String fiatCurrency) {
        return cryptoCurrency.toUpperCase() + "_" + fiatCurrency.toUpperCase();
    }

    private static String msgBaseLogInfo(String methodName) {
        return msgBaseLogInfo(methodName, null);
    }

    private static String msgBaseLogInfo(String methodName, String message) {
        StringBuilder sb = new StringBuilder();
        if (methodName != null) {
            sb.append(methodName);
            if (message != null) {
                sb.append(" - ");
                sb.append(message);
            }
        } else {
            sb.append(message);
        }
        return sb.toString();
    }

    private void waitForPossibleCall() {
        long now = System.currentTimeMillis();
        if (lastCall != -1) {
            long diff = now - lastCall;
            if (diff < CALL_PERIOD_MINIMUM) {
                try {
                    long sleeping = CALL_PERIOD_MINIMUM - diff;
                    Thread.sleep(sleeping);
                } catch (InterruptedException e) {
                    log.error(msgBaseLogInfo("waitForPossibleCall"), e);
                }
            }
        }
        lastCall = now;
    }

    public BitFlyerExchange(String preferredFiatCurrency, String baseUrl) {
        preferredFiatCurrency = preferredFiatCurrency.toUpperCase();
        initConstants(baseUrl);
        if (!FIAT_CURRENCIES.contains(preferredFiatCurrency)) {
            log.error(msgBaseLogInfo("constructor", "Fiat currency \"" + preferredFiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
            throw new IllegalStateException("preferredFiatCurrency = " + preferredFiatCurrency);
        }
        this.preferredFiatCurrency = preferredFiatCurrency;
        try {
            ClientConfig config = new ClientConfig();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
            api = RestProxyFactory.createProxy(IBitFlyerAPI.class, baseUrl, config);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error(msgBaseLogInfo("constructor", "Cannot create instance."), e);
        }
    }

    public BitFlyerExchange(String preferredFiatCurrency, String apiKey, String secretKey, String baseUrl) {
        this(preferredFiatCurrency, baseUrl);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error(msgBaseLogInfo("constructor", "Key is not set."));
        }
        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.error(msgBaseLogInfo("constructor", "Secret is not set."));
        }
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTO_CURRENCIES;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        log.debug(msgBaseLogInfo("getCryptoBalance"));
        cryptoCurrency = cryptoCurrency.toUpperCase();
        if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
            log.error(msgBaseLogInfo("getCryptoBalance", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
            return null;
        }
        waitForPossibleCall();
        String timestamp = new Date().toString();
        BFYAccountAssetBalanceResponse[] balances = api.getBalance(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp));
        if (balances != null) {
            for (BFYAccountAssetBalanceResponse balance : balances) {
                if (balance.currency_code.equals(cryptoCurrency)) {
                    return balance.amount;
                }
            }
        }
        log.error(msgBaseLogInfo("getCryptoBalance", "Balance doesn't exist for crypto currency \"" + cryptoCurrency + "\"."));
        return null;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        log.debug(msgBaseLogInfo("getFiatBalance"));
        fiatCurrency = fiatCurrency.toUpperCase();
        if (!FIAT_CURRENCIES.contains(fiatCurrency)) {
            log.error(msgBaseLogInfo("getFiatBalance", "Fiat currency \"" + fiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
            return null;
        }
        waitForPossibleCall();
        String timestamp = new Date().toString();
        BFYAccountAssetBalanceResponse[] balances = api.getBalance(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp));
        if (balances != null) {
            for (BFYAccountAssetBalanceResponse balance : balances) {
                if (balance.currency_code.equals(fiatCurrency)) {
                    return balance.amount;
                }
            }
        }
        log.error(msgBaseLogInfo("getFiatBalance", "Balance doesn't exist for fiat currency \"" + fiatCurrency + "\"."));
        return null;
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        String productCode = makeProductCode(cryptoCurrency, fiatCurrency);
        if (!PRODUCT_CODES.contains(productCode)) {
            log.debug(msgBaseLogInfo("getExchangeRateLastSync", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
            return null;
        }
        waitForPossibleCall();
        BFYExecutionHistoryResponse[] executionHistories = api.getExecutionHistory(productCode, 1);
        if (executionHistories != null && executionHistories.length == 1) {
            return executionHistories[0].price;
        }
        log.debug(msgBaseLogInfo("getExchangeRateLastSync", "Response has not one object."));
        return null;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        log.debug(msgBaseLogInfo("getExchangeRateLast"));
        String key = cryptoCurrency + "_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug(msgBaseLogInfo("getExchangeRateLast", "for rate: " + key + " = " + result));
                rateAmounts.put(key, result);
                rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            } else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                } else {
                    //do the job
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    log.debug(msgBaseLogInfo("getExchangeRateLast", "for rate: " + key + " = " + result));
                    rateAmounts.put(key, result);
                    rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }
    }

    public String getPermissions() {
        log.debug(msgBaseLogInfo("getPermissions"));
        waitForPossibleCall();
        String timestamp = new Date().toString();
        String[] permissions = api.getPermissions(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp));
        StringBuilder sb = new StringBuilder();
        for (String perm : permissions) {
            sb.append(perm).append(";");
        }
        return sb.toString();
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        log.debug(msgBaseLogInfo("getDepositAddress"));
        cryptoCurrency = cryptoCurrency.toUpperCase();
        if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
            log.error(msgBaseLogInfo("getDepositAddress", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
            return null;
        }
        waitForPossibleCall();
        String timestamp = new Date().toString();
        BFYDepositAddressResponse[] addresses = api.getAddresses(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp));
        if (addresses != null) {
            for (BFYDepositAddressResponse address : addresses) {
                if (address.currency_code.equals(cryptoCurrency)) {
                    if ("NORMAL".equals(address.type) && address.address != null && !address.address.trim().isEmpty()) {
                        return address.address;
                    }
                }
            }
        }
        log.error(msgBaseLogInfo("getDepositAddress", "Deposit address (type normal) doesn't exist for crypto currency \"" + cryptoCurrency + "\"."));
        return null;
    }

    class SellCoinsTask implements ITask {

        private long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; // 5 hours
        private long createTime;

        private BigDecimal amount;
        private String productCode;
        private String orderAId;
        private String result;
        private boolean finished;

        SellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, String description) {
            String productCode = makeProductCode(cryptoCurrency, fiatCurrency);
            if (!PRODUCT_CODES.contains(productCode)) {
                log.debug(msgBaseLogInfo("SellCoinsTask.constructor", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
                throw new IllegalStateException("Product code \"" + productCode + "\" is not supported");
            }
            this.productCode = productCode;
            this.amount = amount;
        }

        @Override
        public boolean onCreate() {
            try {
                log.debug(msgBaseLogInfo("SellCoinsTask.onCreate", "amount = " + amount + ", product code = " + productCode));
                BigDecimal tradableLimit = calculateTradableLimit(productCode, amount, TradableLimitFor.SELL);

                if (tradableLimit != null) {

                    BFYNewOrderRequest request = new BFYNewOrderRequest();
                    request.child_order_type = "LIMIT";
                    request.minute_to_expire = new BigDecimal(525600 * 1000); // 1000 years (no expire)
                    request.price = tradableLimit;
                    request.product_code = productCode;
                    request.side = "SELL";
                    request.size = amount;
                    request.time_in_force = "GTC"; // Good-Til-Canceled will continue to work until the order fills or is canceled

                    waitForPossibleCall();
                    String timestamp = new Date().toString();
                    BFYNewOrderResponse response = api.sendOrder(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp), request);

                    if (response.error_message != null) {
                        log.error(msgBaseLogInfo("SellCoinsTask.onCreate", response.error_message));
                        return false;
                    }

                    createTime = System.currentTimeMillis();
                    orderAId = response.child_order_acceptance_id;
                    log.debug(msgBaseLogInfo("SellCoinsTask.onCreate", "response.child_order_acceptance_id = " + orderAId));

                    try {
                        Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                    } catch (InterruptedException e) {
                        log.error(msgBaseLogInfo("SellCoinsTask.onCreate"), e);
                    }
                }

            } catch (Exception e) {
                log.error(msgBaseLogInfo("SellCoinsTask.onCreate"), e);
            }
            return (orderAId != null);
        }

        @Override
        public boolean onDoStep() {
            if (orderAId == null) {
                log.debug(msgBaseLogInfo("SellCoinsTask.onDoStep", "Giving up on waiting for trade to complete. Because it did not happen."));
                finished = true;
                result = "Skipped";
                return false;
            }
            // get open orders
            boolean orderProcessed = false;
            long checkTillTime = createTime + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug(msgBaseLogInfo("SellCoinsTask.onDoStep", "Giving up on waiting for trade " + orderAId + " to complete."));
                finished = true;
                return false;
            }

            boolean orderFound = false;
            try {
                String timestamp = new Date().toString();
                BFYListOrdersResponse[] response = api.getActiveOrders(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp), productCode, Integer.MAX_VALUE);
                log.debug(msgBaseLogInfo("SellCoinsTask.onDoStep", "Open orders (" + response.length + "):"));
                for (BFYListOrdersResponse order : response) {
                    log.debug(msgBaseLogInfo("SellCoinsTask.onDoStep", "openOrder.child_order_acceptance_id = " + order.child_order_acceptance_id));
                    if (orderAId.equals(order.child_order_acceptance_id)) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (Exception e) {
                log.error(msgBaseLogInfo("SellCoinsTask.onDoStep"), e);
            }

            if (orderFound) {
                log.debug(msgBaseLogInfo("SellCoinsTask.onDoStep", "Waiting for order to be processed."));
            } else {
                orderProcessed = true;
            }

            if (orderProcessed) {
                result = orderAId;
                finished = true;
            }

            return result != null;
        }

        @Override
        public void onFinish() {
            log.debug(msgBaseLogInfo("SellCoinsTask.onFinish", "Sell task finished."));
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
            return 5 * 1000; //it doesn't make sense to run step sooner than after 5 seconds
        }
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, String description) {
        log.debug(msgBaseLogInfo("createSellCoinsTask"));
        cryptoCurrency = cryptoCurrency.toUpperCase();
        fiatCurrency = fiatCurrency.toUpperCase();

        if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
            log.error(msgBaseLogInfo("createSellCoinsTask", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
            return null;
        }
        if (!FIAT_CURRENCIES.contains(fiatCurrency)) {
            log.error(msgBaseLogInfo("createSellCoinsTask", "Fiat currency \"" + fiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
            return null;
        }
        return new SellCoinsTask(amount, cryptoCurrency, fiatCurrency, description);
    }

    class PurchaseCoinsTask implements ITask {

        private long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; // 5 hours
        private long createTime;

        private BigDecimal amount;
        private String productCode;
        private String description;
        private String orderAId;
        private String result;
        private boolean finished;

        PurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, String description) {
            String productCode = makeProductCode(cryptoCurrency, fiatCurrency);
            if (!PRODUCT_CODES.contains(productCode)) {
                log.debug(msgBaseLogInfo("PurchaseCoinsTask.constructor", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
                throw new IllegalStateException("Product code \"" + productCode + "\" is not supported");
            }
            this.productCode = productCode;
            this.amount = amount;
        }

        @Override
        public boolean onCreate() {
            try {
                log.debug(msgBaseLogInfo("PurchaseCoinsTask.onCreate", "amount = " + amount + ", product code = " + productCode));
                BigDecimal tradableLimit = calculateTradableLimit(productCode, amount, TradableLimitFor.BUY);

                if (tradableLimit != null) {

                    BFYNewOrderRequest request = new BFYNewOrderRequest();
                    request.child_order_type = "LIMIT";
                    request.minute_to_expire = new BigDecimal(525600 * 1000); // 1000 years (no expire)
                    request.price = tradableLimit;
                    request.product_code = productCode;
                    request.side = "BUY";
                    request.size = amount;
                    request.time_in_force = "GTC"; // Good-Til-Canceled will continue to work until the order fills or is canceled

                    waitForPossibleCall();
                    String timestamp = new Date().toString();
                    BFYNewOrderResponse response = api.sendOrder(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp), request);

                    if (response.error_message != null) {
                        log.error(msgBaseLogInfo("PurchaseCoinsTask.onCreate", response.error_message));
                        return false;
                    }

                    createTime = System.currentTimeMillis();
                    orderAId = response.child_order_acceptance_id;
                    log.debug(msgBaseLogInfo("PurchaseCoinsTask.onCreate", "response.child_order_acceptance_id = " + orderAId));

                    try {
                        Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                    } catch (InterruptedException e) {
                        log.error(msgBaseLogInfo("PurchaseCoinsTask.onCreate"), e);
                    }
                }

            } catch (Exception e) {
                log.error(msgBaseLogInfo("PurchaseCoinsTask.onCreate"), e);
            }
            return (orderAId != null);
        }

        @Override
        public boolean onDoStep() {
            if (orderAId == null) {
                log.debug(msgBaseLogInfo("PurchaseCoinsTask.onDoStep", "Giving up on waiting for trade to complete. Because it did not happen."));
                finished = true;
                result = "Skipped";
                return false;
            }
            // get open orders
            boolean orderProcessed = false;
            long checkTillTime = createTime + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug(msgBaseLogInfo("PurchaseCoinsTask.onDoStep", "Giving up on waiting for trade " + orderAId + " to complete."));
                finished = true;
                return false;
            }

            boolean orderFound = false;
            try {
                String timestamp = new Date().toString();
                BFYListOrdersResponse[] response = api.getActiveOrders(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp), productCode, Integer.MAX_VALUE);
                log.debug(msgBaseLogInfo("PurchaseCoinsTask.onDoStep", "Open orders (" + response.length + "):"));
                for (BFYListOrdersResponse order : response) {
                    log.debug(msgBaseLogInfo("PurchaseCoinsTask.onDoStep", "openOrder.child_order_acceptance_id = " + order.child_order_acceptance_id));
                    if (orderAId.equals(order.child_order_acceptance_id)) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (Exception e) {
                log.error(msgBaseLogInfo("PurchaseCoinsTask.onDoStep"), e);
            }

            if (orderFound) {
                log.debug(msgBaseLogInfo("PurchaseCoinsTask.onDoStep", "Waiting for order to be processed."));
            } else {
                orderProcessed = true;
            }

            if (orderProcessed) {
                result = orderAId;
                finished = true;
            }

            return result != null;
        }

        @Override
        public void onFinish() {
            log.debug(msgBaseLogInfo("PurchaseCoinsTask.onFinish", "Purchase task finished."));
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
            return 5 * 1000; //it doesn't make sense to run step sooner than after 5 seconds
        }
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrency, String description) {
        log.debug(msgBaseLogInfo("createPurchaseCoinsTask"));
        cryptoCurrency = cryptoCurrency.toUpperCase();
        fiatCurrency = fiatCurrency.toUpperCase();

        if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
            log.error(msgBaseLogInfo("createPurchaseCoinsTask", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
            return null;
        }
        if (!FIAT_CURRENCIES.contains(fiatCurrency)) {
            log.error(msgBaseLogInfo("createPurchaseCoinsTask", "Fiat currency \"" + fiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
            return null;
        }
        return new PurchaseCoinsTask(amount, cryptoCurrency, fiatCurrency, description);
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrency, String description) {
        try {
            cryptoCurrency = cryptoCurrency.toUpperCase();
            fiatCurrency = fiatCurrency.toUpperCase();

            if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
                log.error(msgBaseLogInfo("sellCoins", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
                return null;
            }
            if (!FIAT_CURRENCIES.contains(fiatCurrency)) {
                log.error(msgBaseLogInfo("sellCoins", "Fiat currency \"" + fiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
                return null;
            }

            String productCode = makeProductCode(cryptoCurrency, fiatCurrency);
            if (!PRODUCT_CODES.contains(productCode)) {
                log.debug(msgBaseLogInfo("sellCoins", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
                return null;
            }

            log.debug(msgBaseLogInfo("sellCoins", "amount = " + cryptoAmount + ", product code = " + productCode));

            BigDecimal tradableLimit = calculateTradableLimit(productCode, cryptoAmount, TradableLimitFor.SELL);

            if (tradableLimit != null) {

                BFYNewOrderRequest request = new BFYNewOrderRequest();
                request.child_order_type = "LIMIT";
                request.minute_to_expire = new BigDecimal(525600 * 1000); // 1000 years (no expire)
                request.price = tradableLimit;
                request.product_code = productCode;
                request.side = "SELL";
                request.size = cryptoAmount;
                request.time_in_force = "GTC"; // Good-Til-Canceled will continue to work until the order fills or is canceled

                waitForPossibleCall();
                String timestamp = new Date().toString();
                BFYNewOrderResponse response = api.sendOrder(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp), request);

                if (response.error_message != null) {
                    log.error(msgBaseLogInfo("sellCoins", response.error_message));
                    return null;
                }

                log.debug(msgBaseLogInfo("sellCoins", "response.child_order_acceptance_id = " + response.child_order_acceptance_id));

                if (response.child_order_acceptance_id == null) {
                    return null;
                }

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    log.error(msgBaseLogInfo("sellCoins (1)"), e);
                }

                // get open orders
                boolean orderProcessed = false;
                int numberOfChecks = 0;
                while (!orderProcessed && numberOfChecks < 10) {
                    boolean orderFound = false;
                    String timestamp2 = new Date().toString();
                    BFYListOrdersResponse[] response2 = api.getActiveOrders(apiKey, timestamp2, BitFlyerDigest.createInstance(secretKey, timestamp2), productCode, Integer.MAX_VALUE);
                    log.debug(msgBaseLogInfo("sellCoins", "Open orders (" + response2.length + "):"));
                    for (BFYListOrdersResponse order : response2) {
                        log.debug(msgBaseLogInfo("sellCoins", "openOrder.child_order_acceptance_id = " + order.child_order_acceptance_id));
                        if (response.child_order_acceptance_id.equals(order.child_order_acceptance_id)) {
                            orderFound = true;
                            break;
                        }
                    }
                    if (orderFound) {
                        log.debug(msgBaseLogInfo("sellCoins", "Waiting for order to be processed."));
                        try {
                            Thread.sleep(3000); //don't get your ip address banned
                        } catch (InterruptedException e) {
                            log.error("sellCoins (2)", e);
                        }
                    } else {
                        orderProcessed = true;
                    }
                    numberOfChecks++;
                }
                if (orderProcessed) {
                    return response.child_order_acceptance_id;
                }
            }

        } catch (Exception e) {
            log.error(msgBaseLogInfo("sellCoins (3)"), e);
        }
        return null;
    }

    @Override
    public String purchaseCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrency, String description) {
        try {
            cryptoCurrency = cryptoCurrency.toUpperCase();
            fiatCurrency = fiatCurrency.toUpperCase();

            if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
                log.error(msgBaseLogInfo("purchaseCoins", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
                return null;
            }
            if (!FIAT_CURRENCIES.contains(fiatCurrency)) {
                log.error(msgBaseLogInfo("purchaseCoins", "Fiat currency \"" + fiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
                return null;
            }

            String productCode = makeProductCode(cryptoCurrency, fiatCurrency);
            if (!PRODUCT_CODES.contains(productCode)) {
                log.debug(msgBaseLogInfo("purchaseCoins", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
                return null;
            }

            log.debug(msgBaseLogInfo("purchaseCoins", "amount = " + cryptoAmount + ", product code = " + productCode));

            BigDecimal tradableLimit = calculateTradableLimit(productCode, cryptoAmount, TradableLimitFor.BUY);

            if (tradableLimit != null) {

                BFYNewOrderRequest request = new BFYNewOrderRequest();
                request.child_order_type = "LIMIT";
                request.minute_to_expire = new BigDecimal(525600 * 1000); // 1000 years (no expire)
                request.price = tradableLimit;
                request.product_code = productCode;
                request.side = "BUY";
                request.size = cryptoAmount;
                request.time_in_force = "GTC"; // Good-Til-Canceled will continue to work until the order fills or is canceled

                waitForPossibleCall();
                String timestamp = new Date().toString();
                BFYNewOrderResponse response = api.sendOrder(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp), request);

                if (response.error_message != null) {
                    log.error(msgBaseLogInfo("purchaseCoins (1)", response.error_message));
                    return null;
                }

                log.debug(msgBaseLogInfo("purchaseCoins", "response.child_order_acceptance_id = " + response.child_order_acceptance_id));

                if (response.child_order_acceptance_id == null) {
                    return null;
                }

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    log.error(msgBaseLogInfo("purchaseCoins (2)"), e);
                }

                // get open orders
                boolean orderProcessed = false;
                int numberOfChecks = 0;
                while (!orderProcessed && numberOfChecks < 10) {
                    boolean orderFound = false;
                    String timestamp2 = new Date().toString();
                    BFYListOrdersResponse[] response2 = api.getActiveOrders(apiKey, timestamp2, BitFlyerDigest.createInstance(secretKey, timestamp2), productCode, Integer.MAX_VALUE);
                    log.debug(msgBaseLogInfo("purchaseCoins", "Open orders (" + response2.length + "):"));
                    for (BFYListOrdersResponse order : response2) {
                        log.debug(msgBaseLogInfo("purchaseCoins", "openOrder.child_order_acceptance_id = " + order.child_order_acceptance_id));
                        if (response.child_order_acceptance_id.equals(order.child_order_acceptance_id)) {
                            orderFound = true;
                            break;
                        }
                    }
                    if (orderFound) {
                        log.debug(msgBaseLogInfo("purchaseCoins", "Waiting for order to be processed."));
                        try {
                            Thread.sleep(3000); //don't get your ip address banned
                        } catch (InterruptedException e) {
                            log.error(msgBaseLogInfo("purchaseCoins (3)"), e);
                        }
                    } else {
                        orderProcessed = true;
                    }
                    numberOfChecks++;
                }
                if (orderProcessed) {
                    return response.child_order_acceptance_id;
                }
            }

        } catch (Exception e) {
            log.error(msgBaseLogInfo("purchaseCoins (4)"), e);
        }
        return null;
    }

    private BigDecimal calculateTradableLimit(String productCode, BigDecimal targetAmount, TradableLimitFor tradableLimitFor) {
        if (!PRODUCT_CODES.contains(productCode)) {
            log.debug(msgBaseLogInfo("calculateTradableLimit", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
            throw new IllegalStateException("Product code \"" + productCode + "\" is not supported");
        }

        waitForPossibleCall();
        BFYOrderBookResponse orderBook = api.getOrderBook(productCode);

        List<BFYOrderBookRecord> items = null;
        if (tradableLimitFor == TradableLimitFor.SELL) {
            items = orderBook.bids;
            items.sort((lhs, rhs) -> rhs.price.compareTo(lhs.price));

        } else if (tradableLimitFor == TradableLimitFor.BUY) {
            items = orderBook.asks;
            items.sort(Comparator.comparing(lhs -> lhs.price));
        }

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal tradableAmount = null;
        BigDecimal tradableLimit = null;

        String type = null;
        if (tradableLimitFor == TradableLimitFor.SELL) {
            type = "bids";
        } else if (tradableLimitFor == TradableLimitFor.BUY) {
            type = "asks";
        }

        log.debug(msgBaseLogInfo("calculateTradableLimit", "Order book (" + type + "):"));

        for (BFYOrderBookRecord item : items) {
            log.trace(msgBaseLogInfo("calculateTradableLimit", "size = " + item.size + ", price = " + item.price));
            total = total.add(item.size);
            if (targetAmount.compareTo(total) <= 0) {
                tradableAmount = item.size;
                tradableLimit = item.price;
                break;
            }
        }

        log.trace(msgBaseLogInfo("calculateTradableLimit", "tradableAmount = " + tradableAmount));
        log.trace(msgBaseLogInfo("calculateTradableLimit", "tradableLimit = " + tradableLimit));
        log.trace(msgBaseLogInfo("calculateTradableLimit", "targetAmount = " + targetAmount));

        return tradableLimit;
    }

    @Override
    public synchronized String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        cryptoCurrency = cryptoCurrency.toUpperCase();

        if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
            log.error(msgBaseLogInfo("sendCoins", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
            return null;
        }

        log.debug(msgBaseLogInfo("sendCoins"));

        BFYSendCoinsRequest request = new BFYSendCoinsRequest();
        request.address = destinationAddress;
        request.amount = amount;
        request.currency_code = cryptoCurrency;
        request.additional_fee = BigDecimal.ZERO;

        waitForPossibleCall();
        String timestamp = new Date().toString();

        BFYSendCoinsResponse response = api.sendCoins(apiKey, timestamp, BitFlyerDigest.createInstance(secretKey, timestamp), request);

        if (response.error_message != null) {
            log.error(msgBaseLogInfo("sendCoins", response.error_message));
        }
        return response.message_id;
    }

    private BigDecimal getMeasureCryptoAmount() {
        return new BigDecimal(5);
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateBuyPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount());
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(), 2, BigDecimal.ROUND_UP).stripTrailingZeros();
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateSellPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount());
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(), 2, BigDecimal.ROUND_DOWN).stripTrailingZeros();
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            cryptoCurrency = cryptoCurrency.toUpperCase();
            fiatCurrency = fiatCurrency.toUpperCase();

            if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
                log.error(msgBaseLogInfo("calculateBuyPrice", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
                return null;
            }
            if (!FIAT_CURRENCIES.contains(fiatCurrency)) {
                log.error(msgBaseLogInfo("calculateBuyPrice", "Fiat currency \"" + fiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
                return null;
            }

            String productCode = makeProductCode(cryptoCurrency, fiatCurrency);
            if (!PRODUCT_CODES.contains(productCode)) {
                log.debug(msgBaseLogInfo("calculateBuyPrice", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
                return null;
            }

            log.trace(msgBaseLogInfo("calculateBuyPrice", "cryptoCurrency = " + cryptoCurrency + ", fiatCurrency = " + fiatCurrency + ", cryptoAmount = " + cryptoAmount));

            BigDecimal tradableLimit = calculateTradableLimit(productCode, cryptoAmount, TradableLimitFor.BUY);

            if (tradableLimit != null) {
                log.debug(msgBaseLogInfo("calculateBuyPrice", "Called BitFlyer exchange for BUY rate: " + cryptoCurrency + "_" + fiatCurrency + " = " + tradableLimit));
                return tradableLimit.multiply(cryptoAmount).stripTrailingZeros();
            }

        } catch (Throwable t) {
            log.debug(msgBaseLogInfo("calculateBuyPrice"), t);
        }
        log.error(msgBaseLogInfo("calculateBuyPrice", "Buy price doesn't exist for crypto currency \"" + cryptoCurrency + "\" and fiat currency \"" + fiatCurrency + "\"."));
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            cryptoCurrency = cryptoCurrency.toUpperCase();
            fiatCurrency = fiatCurrency.toUpperCase();

            if (!CRYPTO_CURRENCIES.contains(cryptoCurrency)) {
                log.error(msgBaseLogInfo("calculateSellPrice", "Crypto currency \"" + cryptoCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(CRYPTO_CURRENCIES.toArray())));
                return null;
            }
            if (!FIAT_CURRENCIES.contains(fiatCurrency)) {
                log.error(msgBaseLogInfo("calculateSellPrice", "Fiat currency \"" + fiatCurrency + "\" is not supported. BitFlayer supports only " + Arrays.toString(FIAT_CURRENCIES.toArray())));
                return null;
            }

            String productCode = makeProductCode(cryptoCurrency, fiatCurrency);
            if (!PRODUCT_CODES.contains(productCode)) {
                log.debug(msgBaseLogInfo("calculateSellPrice", "Product code \"" + productCode + "\" is not supported. BitFlayer supports only " + Arrays.toString(PRODUCT_CODES.toArray())));
                return null;
            }

            log.trace(msgBaseLogInfo("calculateSellPrice", "cryptoCurrency = " + cryptoCurrency + ", fiatCurrency = " + fiatCurrency + ", cryptoAmount = " + cryptoAmount));

            BigDecimal tradableLimit = calculateTradableLimit(productCode, cryptoAmount, TradableLimitFor.SELL);

            if (tradableLimit != null) {
                log.debug(msgBaseLogInfo("calculateSellPrice", "Called BitFlyer exchange for SELL rate: " + cryptoCurrency + "_" + fiatCurrency + " = " + tradableLimit));
                return tradableLimit.multiply(cryptoAmount).stripTrailingZeros();
            }

        } catch (Throwable t) {
            log.debug(msgBaseLogInfo("calculateSellPrice"), t);
        }
        log.error(msgBaseLogInfo("calculateSellPrice", "Sell price doesn't exist for crypto currency \"" + cryptoCurrency + "\" and fiat currency \"" + fiatCurrency + "\"."));
        return null;
    }

//    public static void main(String[] args) {
//
//        BitFlyerExchange exchangePublic = new BitFlyerExchange(FiatCurrency.JPY.getCode());
//        BitFlyerExchange exchangePrivate = new BitFlyerExchange(FiatCurrency.JPY.getCode(), "2YBNqYdWkFyC5HBtp3PLRR", "xxxxxxxxxxxxxxxxxxxxxxx");
//
//        log.info("getExchangeRateLast (" + CryptoCurrency.BTC.getCode() + " in " + FiatCurrency.JPY.getCode() + ") = " + exchangePublic.getExchangeRateLast(CryptoCurrency.BTC.getCode(), FiatCurrency.JPY.getCode()));
//
//        log.info("getExchangeRateForBuy (" + CryptoCurrency.BTC.getCode() + " in " + FiatCurrency.JPY.getCode() + ") = " + ValueFormatUtils.formatCrypto(exchangePublic.getExchangeRateForBuy(CryptoCurrency.BTC.getCode(), FiatCurrency.JPY.getCode())));
//        log.info("getExchangeRateForSell (" + CryptoCurrency.BTC.getCode() + " in " + FiatCurrency.JPY.getCode() + ") = " + exchangePublic.getExchangeRateForSell(CryptoCurrency.BTC.getCode(), FiatCurrency.JPY.getCode()));
//
//        ITask taskSell = exchangePrivate.createSellCoinsTask(new BigDecimal("0.00787"), CryptoCurrency.BTC.getCode(), FiatCurrency.JPY.getCode(), null);
//        taskSell.onCreate();
//        taskSell.onDoStep();
//
//        ITask taskBuy = exchangePrivate.createPurchaseCoinsTask(new BigDecimal("0.001"), CryptoCurrency.BTC.getCode(), FiatCurrency.JPY.getCode(), null);
//        taskBuy.onCreate();
//        taskBuy.onDoStep();
//
//        log.info("sendCoins (" + CryptoCurrency.BTC.getCode() + ") = " + exchangePrivate.sendCoins("15kLSmf8Ad1CD54hkSDxKK1XToZFydBFou", new BigDecimal("0.001"), CryptoCurrency.BTC.getCode(), null));
//
//        log.info("sellCoins (" + CryptoCurrency.BTC.getCode() + " in " + FiatCurrency.JPY.getCode() + ") = " + exchangePrivate.sellCoins(new BigDecimal("0.01516175"), CryptoCurrency.BTC.getCode(), FiatCurrency.JPY.getCode(), null));
//
//        log.info("purchaseCoins (" + CryptoCurrency.BTC.getCode() + " in " + FiatCurrency.JPY.getCode() + ") = " + exchangePrivate.purchaseCoins(new BigDecimal("0.001"), CryptoCurrency.BTC.getCode(), FiatCurrency.JPY.getCode(), null));
//
//        log.info("getDepositAddress (" + CryptoCurrency.BTC.getCode() + ") = " + exchangePrivate.getDepositAddress(CryptoCurrency.BTC.getCode()));
//        log.info("getPermissions = " + exchangePrivate.getPermissions());
//
//        log.info("getFiatBalance (" + FiatCurrency.JPY.getCode() + ") = " + exchangePrivate.getFiatBalance(FiatCurrency.JPY.getCode()));
//        log.info("getCryptoBalance (" + CryptoCurrency.BTC.getCode() + ") = " + exchangePrivate.getCryptoBalance(CryptoCurrency.BTC.getCode()));
//
//    }
}
