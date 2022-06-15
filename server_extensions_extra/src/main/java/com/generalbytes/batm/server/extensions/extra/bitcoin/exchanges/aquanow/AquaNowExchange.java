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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto.*;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
import com.google.common.collect.ImmutableSet;
import org.knowm.xchange.utils.nonce.CurrentTimeIncrementalNonceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class AquaNowExchange implements IExchangeAdvanced, IRateSourceAdvanced {
    private static final Logger log = LoggerFactory.getLogger("batm.master.exchange.AquaNowExchange");

    private static final Set<String> fiatCurrencies = ImmutableSet.of(
        FiatCurrency.CAD.getCode());

    private static final Set<String> cryptoCurrencies = ImmutableSet.of(
        CryptoCurrency.BTC.getCode(),
        CryptoCurrency.ETH.getCode());

    private String apiKey;
    private String apiSecret;

    private String preferredFiatCurrency;
    private IAquaNowAPI apiMarket;
    private IAquaNowAPI apiTrade;

    @SuppressWarnings("WeakerAccess")
    public AquaNowExchange(){
        try {
            ClientConfig config = new ClientConfig();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
            // change: https://api.aquanow.io https://market.aquanow.io
            apiMarket =  RestProxyFactory.createProxy(IAquaNowAPI.class, "https://market-staging.aquanow.io", config);
            apiTrade = RestProxyFactory.createProxy(IAquaNowAPI.class, "https://api-dev.aquanow.io", config);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("constructor - Cannot create instance.", e);
        }
    }

    public AquaNowExchange(String preferredFiatCurrency) {
        this();
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    public AquaNowExchange(String apiKey, String apiSecret,String preferredFiatCurrency) {
        this();
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    private String getMarketSymbol(String cryptoCurrency, String fiatCurrency) {
        return cryptoCurrency + "-" + fiatCurrency;
    }

    private boolean isCryptoCurrencySupported(String currency) {
        if (currency == null || !getCryptoCurrencies().contains(currency)) {
            log.debug("doesn't support cryptocurrency '{}'", currency);
            return false;
        }
        return true;
    }

    private boolean isFiatCurrencySupported(String currency) {
        if (currency == null || !getFiatCurrencies().contains(currency)) {
            log.debug("doesn't support fiat currency '{}'", currency);
            return false;
        }
        return true;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return BigDecimal.ZERO;
        }
        return getBalance(cryptoCurrency);
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        if (!isFiatCurrencySupported(fiatCurrency)) {
            return BigDecimal.ZERO;
        }
        return getBalance(fiatCurrency);
    }

    private BigDecimal getBalance(String currency) {
        try {
            log.debug("getBalance");
            CurrentTimeIncrementalNonceFactory xNonce = new CurrentTimeIncrementalNonceFactory(TimeUnit.MILLISECONDS);
            String time = String.valueOf(xNonce.createValue());
            if (currency != null) {
                UserBalanceResponse userBalanceResponse = apiTrade.getUserBalance(apiKey, time, new AquaNowDigest(apiSecret),currency);
                if (userBalanceResponse.message == null) {
                    return userBalanceResponse.totalBalance;
                } else {
                    log.error("getBalance (1) - " + ("No account. getBalance = " + currency));
                }
            }
        } catch (Throwable e) {
            log.error("getBalance (2)", e);
        }
        return null;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        try {
            CurrentTimeIncrementalNonceFactory xNonce = new CurrentTimeIncrementalNonceFactory(TimeUnit.MILLISECONDS);
            String time = String.valueOf(xNonce.createValue());
            RateLimiter.waitForPossibleCall(getClass());
            GetAddressRequest getAddressRequest = new GetAddressRequest(cryptoCurrency);
            GetAddressResponse getAddressResponse = apiTrade.getUserAddress(apiKey, time, new AquaNowDigest(apiSecret),getAddressRequest);
            if (getAddressResponse.message == null) {
                return getAddressResponse.address;
            }else{
                log.error("getDepositAddress (1) - " + cryptoCurrency);
            }
        } catch (Throwable e) {
            log.error("getDepositAddress(2)", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            if (!isCryptoCurrencySupported(cryptoCurrency)) {
                return null;
            }
            CurrentTimeIncrementalNonceFactory xNonce = new CurrentTimeIncrementalNonceFactory(TimeUnit.MILLISECONDS);
            String time = String.valueOf(xNonce.createValue());
            SendCoinRequest sendCoinsRequest = new SendCoinRequest(amount,cryptoCurrency, destinationAddress);
            RateLimiter.waitForPossibleCall(getClass());
            SendCoinResponse sendCoinResponse = apiTrade.sendCoins(apiKey, time, new AquaNowDigest(apiSecret),sendCoinsRequest);
            if (sendCoinResponse.message == null) {
                return sendCoinResponse.txId;
            } else {
                log.error("sendCoins - " + sendCoinResponse.message);
            }
        } catch (Throwable e) {
            log.error("sendCoins", e);
        }
        return null;
    }

    private BigDecimal getExchangeRate(String cryptoCurrency, String fiatCurrency, String priceType) {
        try {
            log.debug("getExchangeRate - " + priceType);
            String currencyPair = getMarketSymbol(cryptoCurrency, fiatCurrency);
            RateLimiter.waitForPossibleCall(getClass());
            BestPriceResponse response = apiMarket.getbestprice(currencyPair);
            if(priceType.equals("buy")){
                if (response.bestAsk != null)  return new BigDecimal(String.valueOf(response.bestAsk));
            } else if(priceType.equals("sell")){
                if (response.bestBid != null)   return new BigDecimal(String.valueOf(response.bestBid));
            }
        } catch (Throwable e) {
            log.error("getExchangeRate", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        try {
            return getExchangeRate(cryptoCurrency, fiatCurrency, "buy");
        }catch (Throwable e) {
            log.error("getExchangeRateLast", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        try {
            return getExchangeRate(cryptoCurrency, fiatCurrency, "buy");
        }catch (Throwable e) {
            log.error("getExchangeRateForBuy", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        try {
            return getExchangeRate(cryptoCurrency, fiatCurrency, "sell");
        }catch (Throwable e) {
            log.error("getExchangeRateForBuy", e);
        }
        return null;
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return createOrderTask(amount, cryptoCurrency, fiatCurrencyToUse, "buy");
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return createOrderTask(amount, cryptoCurrency, fiatCurrencyToUse, "sell");
    }

    private ITask createOrderTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String orderSide) {
        if (!isCryptoCurrencySupported(cryptoCurrency) || !isFiatCurrencySupported(fiatCurrencyToUse)) {
            return null;
        }
        String symbol = getMarketSymbol(cryptoCurrency, fiatCurrencyToUse);
        return new OrderTask(amount, symbol, orderSide);
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        if (!isCryptoCurrencySupported(cryptoCurrency) || !isFiatCurrencySupported(fiatCurrency)) {
            return null;
        }
        BigDecimal rate = getExchangeRate(cryptoCurrency, fiatCurrency, "buy");
        return (rate != null) ? rate.multiply(cryptoAmount).stripTrailingZeros() : null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        if (!isCryptoCurrencySupported(cryptoCurrency) || !isFiatCurrencySupported(fiatCurrency)) {
            return null;
        }
        BigDecimal rate = getExchangeRate(cryptoCurrency, fiatCurrency, "sell");
        return (rate != null) ? rate.multiply(cryptoAmount).stripTrailingZeros() : null;
    }


    class OrderTask implements ITask {
        private static final long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; //5 hours

        private final long checkTillTime;
        private final BigDecimal cryptoAmount;
        private final String symbol;
        private final String orderSide;

        private String orderId;
        private String result;
        private boolean finished;

        OrderTask(BigDecimal cryptoAmount, String symbol, String orderSide) {
            this.cryptoAmount = cryptoAmount;
            this.symbol = symbol;
            this.orderSide = orderSide;
            this.checkTillTime = System.currentTimeMillis() + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
        }

        @Override
        public boolean onCreate() {
            try{
                log.info("Calling exchange ({} {})", orderSide, symbol);
                orderId = call("task submitLimitOrder", () -> {
                    TradeCoinResponse tradeResponse = null;
                    CurrentTimeIncrementalNonceFactory xNonce = new CurrentTimeIncrementalNonceFactory(TimeUnit.MILLISECONDS);
                    String time = String.valueOf(xNonce.createValue());
                    if(orderSide.equals("buy")){
                        BuyCoinRequest buyCoinRequest = new BuyCoinRequest(cryptoAmount, symbol, orderSide);
                        RateLimiter.waitForPossibleCall(getClass());
                        tradeResponse = apiTrade.buyCoins(apiKey, time, new AquaNowDigest(apiSecret),buyCoinRequest);
                        log.info("Submitting order: {}", buyCoinRequest);
                    }
                    else if(orderSide.equals("sell")){
                        SellCoinRequest sellCoinRequest = new SellCoinRequest(cryptoAmount, symbol, orderSide);
                        RateLimiter.waitForPossibleCall(getClass());
                        tradeResponse = apiTrade.sellCoins(apiKey, time, new AquaNowDigest(apiSecret),sellCoinRequest);
                        log.info("Submitting order: {}", sellCoinRequest);
                    }
                    return tradeResponse.payload.orderId;
                });
            } catch (Exception e) {
                log.error("PurchaseOrSellCoinsTask.onCreate", e);
            }
            return (orderId != null);
        }

        @Override
        public boolean onDoStep() {
            try {
                if (orderId == null || orderId.equals("0")) {
                    log.debug("Giving up on waiting for trade to complete. Because it did not happen");
                    finished = true;
                    result = "Skipped";
                    return false;
                }
                if (System.currentTimeMillis() > checkTillTime) {
                    log.debug("Giving up on waiting for trade {} to complete", orderId);
                    finished = true;
                    return false;
                }

                CurrentTimeIncrementalNonceFactory xNonce = new CurrentTimeIncrementalNonceFactory(TimeUnit.MILLISECONDS);
                String time = String.valueOf(xNonce.createValue());
                OrderStatusResponse orderStatusResponse = call("task getOrder", () -> apiTrade.getOrderStatus(apiKey, time, new AquaNowDigest(apiSecret), orderId));

                if (orderStatusResponse != null && orderStatusResponse.data.tradeStatus.equals("ERROR")) {
                    log.debug("trade finish but trade result = error");
                    finished = true;
                    return false;
                }
                if (orderStatusResponse != null && orderStatusResponse.data.tradeStatus.equals("COMPLETE")) {
                    result = orderId;
                    finished = true;
                }
            }catch (Exception e) {
                log.error("PurchaseOrSellCoinsTask.onDoStep", e);
            }


            return result != null;
        }

        @Override
        public boolean isFinished() {
            return finished;
        }

        @Override
        public String getResult() {
            return result;
        }

        @Override
        public boolean isFailed() {
            return finished && result == null;
        }

        @Override
        public void onFinish() {
            log.debug("task finished");
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 5 * 1000; //it doesn't make sense to run step sooner than after 5 seconds
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return fiatCurrencies;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    /**
     * Execute an action and yield its result or {@code null} if there is any error.
     */
    private <T> T call(String label, Callable<T> action) {
        try {
            T result = action.call();
            log.info("{} result: {}", label, result);
            return result;
        } catch (HttpStatusIOException e) {
            log.error("{} error; HTTP status: {}, body: {}", label, e.getHttpStatusCode(), e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("{} error", label, e);
        }
        return null;
    }
}
