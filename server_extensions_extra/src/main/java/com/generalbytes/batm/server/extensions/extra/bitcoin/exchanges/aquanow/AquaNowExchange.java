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
import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.concurrent.Callable;

public class AquaNowExchange implements IExchangeAdvanced, IRateSourceAdvanced {
    private static final Logger log = LoggerFactory.getLogger("batm.master.exchange.aquaExchange");

    private static final Set<String> fiatCurrencies = ImmutableSet.of(
        FiatCurrency.CAD.getCode());

    private static final Set<String> cryptoCurrencies = ImmutableSet.of(
        CryptoCurrency.BCH.getCode(),
        CryptoCurrency.BTC.getCode(),
        CryptoCurrency.DAI.getCode(),
        CryptoCurrency.ETH.getCode(),
        CryptoCurrency.LTC.getCode(),
        CryptoCurrency.XRP.getCode());

    // Supported markets (2021-10-06; see IaquaAPI.getMarkets):
    // [DAI-CAD, ETH-DAI, BTC-AAVE, ETH-AAVE, ETH-CAD, BTC-LINK, ETH-LINK, BCH-CAD, AAVE-CAD, BCH-BTC, BTC-DAI, LINK-CAD, BTC-CAD, XLM-CAD, LTC-CAD, XRP-CAD, EOS-BTC, XLM-BTC, XRP-BTC, ETH-BTC, LTC-BTC, EOS-CAD]

    private final String preferredFiatCurrency;
    private final IAquaNowAPI api;

    public AquaNowExchange(String preferredFiatCurrency) throws GeneralSecurityException {
        this.api = IAquaNowAPI.create();
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
        //change
        return new BigDecimal(0);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }
        //change
        return "a";
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }

        log.info("withdrawing {} {} to {}", amount, cryptoCurrency, destinationAddress);
        //change
        return "a";
    }

    private BigDecimal getExchangeRate(String cryptoCurrency, String fiatCurrency, String priceType) {
        try {
            log.debug("getExchangeRate - " + priceType);
            String currencyPair = getMarketSymbol(cryptoCurrency, fiatCurrency);
            RateLimiter.waitForPossibleCall(getClass());
            BestPriceResponse response = api.getbestprice(currencyPair);
            if(priceType.equals("buy")){
                if (response.bestBid != null)  return new BigDecimal(String.valueOf(response.bestBid));
            } else if(priceType.equals("sell")){
                if (response.bestAsk != null)   return new BigDecimal(String.valueOf(response.bestAsk));
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
        return createOrderTask(amount, cryptoCurrency, fiatCurrencyToUse, OrderSide.BUY);
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return createOrderTask(amount, cryptoCurrency, fiatCurrencyToUse, OrderSide.SELL);
    }

    private ITask createOrderTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, OrderSide orderSide) {
        if (!isCryptoCurrencySupported(cryptoCurrency) || !isFiatCurrencySupported(fiatCurrencyToUse)) {
            return null;
        }
        return new OrderTask(amount, cryptoCurrency, fiatCurrencyToUse, orderSide);
    }

    private BigDecimal getRateSourceCryptoVolume(String cryptoCurrency) {
        if (CryptoCurrency.BTC.getCode().equals(cryptoCurrency)) {
            return BigDecimal.ONE;
        }
        return BigDecimal.TEN;
    }


    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return calculatePrice(cryptoCurrency, fiatCurrency, cryptoAmount, OrderSide.BUY);
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return calculatePrice(cryptoCurrency, fiatCurrency, cryptoAmount, OrderSide.SELL);
    }

    private BigDecimal calculatePrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount, OrderSide orderSide) {
       //change
        return new BigDecimal(0);
    }

    class OrderTask implements ITask {
        private static final long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; //5 hours

        private final long checkTillTime;
        private final BigDecimal cryptoAmount;
        private final String cryptoCurrency;
        private final String fiatCurrencyToUse;
        private final OrderSide orderSide;

        private String orderId;
        private String result;
        private boolean finished;

        OrderTask(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, OrderSide orderSide) {
            this.cryptoAmount = cryptoAmount;
            this.cryptoCurrency = cryptoCurrency;
            this.fiatCurrencyToUse = fiatCurrencyToUse;
            this.orderSide = orderSide;
            this.checkTillTime = System.currentTimeMillis() + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
        }

        @Override
        public boolean onCreate() {
            //change
            return false;
        }

        @Override
        public boolean onDoStep() {
           //change
            return false;
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
