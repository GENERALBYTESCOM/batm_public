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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.OrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.OrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.OrderSide;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.OrderType;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.QuoteRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.QuoteResponse;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.concurrent.Callable;

public class BitbuyExchange implements IExchangeAdvanced, IRateSourceAdvanced {
    private static final Logger log = LoggerFactory.getLogger("batm.master.exchange.BitbuyExchange");

    private static final Set<String> fiatCurrencies = ImmutableSet.of(
            FiatCurrency.CAD.getCode(),
            CryptoCurrency.DAI.getCode()); // stable coin as fiat

    private static final Set<String> cryptoCurrencies = ImmutableSet.of(
            CryptoCurrency.BCH.getCode(),
            CryptoCurrency.BTC.getCode(),
            CryptoCurrency.BetVerse.getCode(),
            CryptoCurrency.BetVerse_ICO.getCode(),
            CryptoCurrency.DAI.getCode(),
            CryptoCurrency.ETH.getCode(),
            CryptoCurrency.LTC.getCode(),
            CryptoCurrency.XRP.getCode());

    // Supported markets (2021-10-06; see IBitbuyAPI.getMarkets):
    // [DAI-CAD, ETH-DAI, BTC-AAVE, ETH-AAVE, ETH-CAD, BTC-LINK, ETH-LINK, BCH-CAD,
    // AAVE-CAD, BCH-BTC, BTC-DAI, LINK-CAD, BTC-CAD, XLM-CAD, LTC-CAD, XRP-CAD,
    // EOS-BTC, XLM-BTC, XRP-BTC, ETH-BTC, LTC-BTC, EOS-CAD]

    private final String preferredFiatCurrency;
    private final IBitbuyAPI api;

    public BitbuyExchange(String apiKey, String apiSecret, String preferredFiatCurrency)
            throws GeneralSecurityException {
        this.api = IBitbuyAPI.create(apiKey, apiSecret);
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
        return call(currency + " balance", () -> api.getWallets().stream()
                .filter(w -> currency.equals(w.symbol))
                .findAny()
                .map(w -> w.availableBalance)
                .orElseThrow(() -> new Exception(currency + " wallet not found")));
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }

        return call("deposit address", () -> api.getDepositAddress(cryptoCurrency).address);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }

        log.info("withdrawing {} {} to {}", amount, cryptoCurrency, destinationAddress);
        return call("send coins", () -> api.withdraw(cryptoCurrency, destinationAddress, amount).transactionReference);
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
            String description) {
        return createOrderTask(amount, cryptoCurrency, fiatCurrencyToUse, OrderSide.BUY);
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
            String description) {
        return createOrderTask(amount, cryptoCurrency, fiatCurrencyToUse, OrderSide.SELL);
    }

    private ITask createOrderTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse,
            OrderSide orderSide) {
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
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        BigDecimal rateSourceCryptoVolume = getRateSourceCryptoVolume(cryptoCurrency);
        BigDecimal result = calculateBuyPrice(cryptoCurrency, fiatCurrency, rateSourceCryptoVolume);
        if (result != null) {
            return result.divide(rateSourceCryptoVolume, 2, BigDecimal.ROUND_UP);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BigDecimal rateSourceCryptoVolume = getRateSourceCryptoVolume(cryptoCurrency);
        BigDecimal result = calculateSellPrice(cryptoCurrency, fiatCurrency, rateSourceCryptoVolume);
        if (result != null) {
            return result.divide(rateSourceCryptoVolume, 2, BigDecimal.ROUND_DOWN);
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return calculatePrice(cryptoCurrency, fiatCurrency, cryptoAmount, OrderSide.BUY);
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return calculatePrice(cryptoCurrency, fiatCurrency, cryptoAmount, OrderSide.SELL);
    }

    private BigDecimal calculatePrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount,
            OrderSide orderSide) {
        if (!isCryptoCurrencySupported(cryptoCurrency) || !isFiatCurrencySupported(fiatCurrency)) {
            return null;
        }
        return call("calculate " + orderSide + " price",
                () -> api.quoteOrder(new QuoteRequest(cryptoAmount, getMarketSymbol(cryptoCurrency, fiatCurrency),
                        orderSide, OrderType.LIMIT)).fillPrice.multiply(cryptoAmount));
    }

    class OrderTask implements ITask {
        private static final long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; // 5 hours

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
            log.info("Calling exchange ({} {} {})", orderSide, cryptoAmount, cryptoCurrency);
            orderId = call("task submitLimitOrder", () -> {
                QuoteRequest quoteRequest = new QuoteRequest(cryptoAmount,
                        getMarketSymbol(cryptoCurrency, fiatCurrencyToUse), orderSide, OrderType.LIMIT);
                QuoteResponse quote = api.quoteOrder(quoteRequest);

                OrderRequest orderRequest = new OrderRequest(cryptoAmount, quote.fillPrice,
                        getMarketSymbol(cryptoCurrency, fiatCurrencyToUse), orderSide, OrderType.LIMIT);
                log.info("Submitting order: {}", orderRequest);
                OrderResponse order = api.submitOrder(orderRequest);
                return order.id;
            });
            return (orderId != null);
        }

        @Override
        public boolean onDoStep() {
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

            OrderResponse order = call("task getOrder",
                    () -> api.getOrder(getMarketSymbol(cryptoCurrency, fiatCurrencyToUse), orderId));

            if (order != null && order.status.equals(OrderResponse.STATUS_CANCELLED)) {
                log.debug("trade cancelled");
                finished = true;
                return false;
            }
            if (order != null && order.status.equals(OrderResponse.STATUS_FILLED)) {
                result = orderId;
                finished = true;
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
            return 5 * 1000; // it doesn't make sense to run step sooner than after 5 seconds
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
