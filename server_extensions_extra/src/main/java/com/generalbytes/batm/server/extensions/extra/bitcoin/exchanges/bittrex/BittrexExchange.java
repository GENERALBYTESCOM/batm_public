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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex;

import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class BittrexExchange implements IRateSourceAdvanced, IExchangeAdvanced {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BittrexExchange");

    private static final Set<String> FIAT_CURRENCIES = new HashSet<>();
    public static final Comparator<LimitOrder> asksComparator = Comparator.comparing(LimitOrder::getLimitPrice);
    public static final Comparator<LimitOrder> bidsComparator = Comparator.comparing(LimitOrder::getLimitPrice).reversed();

    private Exchange exchange = null;
    private String apiKey;
    private String apiSecret;

    private static final HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;

    static {
        initConstants();
    }

    private static void initConstants() {
        FIAT_CURRENCIES.add(FiatCurrency.USD.getCode());
    }

    private synchronized Exchange getExchange() throws TimeoutException {
        if (this.exchange == null) {
            RateLimiter.waitForPossibleCall(getClass());
            ExchangeSpecification bfxSpec = new org.knowm.xchange.bittrex.BittrexExchange().getDefaultExchangeSpecification();
            bfxSpec.setApiKey(this.apiKey);
            bfxSpec.setSecretKey(this.apiSecret);
            this.exchange = ExchangeFactory.INSTANCE.createExchange(bfxSpec);
        }
        return this.exchange;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<String>();
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.BAY.getCode());
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }

    public BittrexExchange(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return BigDecimal.ZERO;
        }
        log.debug("Calling Bittrex exchange (getbalance)");

        try {
            return getExchange().getAccountService().getAccountInfo().getWallet().getBalance(Currency.getInstance(cryptoCurrency)).getAvailable();
        } catch (IOException | TimeoutException e) {
            log.error("Bittrex exchange (getbalance) failed", e);
        }
        return null;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return FiatCurrency.USD.getCode();
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bittrex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }

        log.info("Calling Bittrex exchange (withdrawal destination: " + destinationAddress + " amount: " + amount + " " + cryptoCurrency + ")");

        try {
            AccountService accountService = getExchange().getAccountService();
            RateLimiter.waitForPossibleCall(getClass());
            String result = accountService.withdrawFunds(Currency.getInstance(cryptoCurrency), amount, destinationAddress);
            log.info("Bittrex exchange (withdrawFunds) finished with result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Bittrex exchange (withdrawFunds) failed", e);
        }
        return null;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        if (!FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrency)) {
            return BigDecimal.ZERO;
        }
        log.debug("Calling Bittrex exchange (getbalance)");

        try {
            return getExchange().getAccountService().getAccountInfo().getWallet().getBalance(Currency.getInstance(fiatCurrency)).getAvailable();
        } catch (IOException | TimeoutException e) {
            log.error("Bittrex exchange (getbalance) failed", e);
        }
        return null;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bittrex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bittrex supports only " + FiatCurrency.USD.getCode() );
            return null;
        }

        log.info("Calling Bittrex exchange (sell " + cryptoAmount + " " + cryptoCurrency + ")");

        try {
            TradeService tradeService = getExchange().getTradeService();
            MarketDataService marketDataService = getExchange().getMarketDataService();

            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> bids = orderBook.getBids();
            log.debug("bids.size(): {}", bids.size());

            Collections.sort(bids, bidsComparator);

            LimitOrder order = new LimitOrder(Order.OrderType.ASK, cryptoAmount, currencyPair,
                "", null, getTradablePrice(cryptoAmount, bids));

            log.debug("order: {}", order);
            RateLimiter.waitForPossibleCall(getClass());
            String orderId = tradeService.placeLimitOrder(order);
            log.debug("orderId: {}", orderId);

            sleep(2000); //give exchange 2 seconds to reflect open order in order book

            if (waitForOrderProcessed(tradeService, orderId, 10)) {
                return orderId;
            }
        } catch (IOException | TimeoutException e) {
            log.error("Bittrex exchange (sellCoins) failed", e);
        }
        return null;
    }

    @Override
    public String purchaseCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bittrex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bittrex supports only " + FiatCurrency.USD.getCode() );
            return null;
        }

        log.info("Calling Bittrex exchange (purchase " + cryptoAmount + " " + cryptoCurrency + ")");

        try {
            TradeService tradeService = getExchange().getTradeService();
            MarketDataService marketDataService = getExchange().getMarketDataService();
            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> asks = orderBook.getAsks();

            Collections.sort(asks, asksComparator);

            LimitOrder limitOrder = new LimitOrder(Order.OrderType.BID, cryptoAmount, currencyPair, "", null, getTradablePrice(cryptoAmount, asks));
            log.debug("limitOrder = {}", limitOrder);
            RateLimiter.waitForPossibleCall(getClass());
            String orderId = tradeService.placeLimitOrder(limitOrder);
            log.debug("orderId = {}", orderId);

            sleep(2000); //give exchange 2 seconds to reflect open order in order book

            if (waitForOrderProcessed(tradeService, orderId, 10)) {
                return orderId;
            }
        } catch (IOException | TimeoutException e) {
            log.error("Bittrex exchange (purchaseCoins) failed", e);
        }
        return null;
    }

    /**
     *
     * @param cryptoAmount
     * @param bidsOrAsksSorted bids: highest first, asks: lowest first
     * @return
     * @throws IOException when tradable price not found, e.g orderbook not received or too small.
     */
    private BigDecimal getTradablePrice(BigDecimal cryptoAmount, List<LimitOrder> bidsOrAsksSorted) throws IOException {
        BigDecimal total = BigDecimal.ZERO;

        for (LimitOrder order : bidsOrAsksSorted) {
            total = total.add(order.getOriginalAmount());
            if (cryptoAmount.compareTo(total) <= 0) {
                log.debug("tradablePrice: {}", order.getLimitPrice());
                return order.getLimitPrice();
            }
        }
        throw new IOException("Bittrex TradablePrice not available");
    }

    /**
     * wait for order to be processed
     * @param tradeService
     * @param orderId
     * @param maxTries
     * @return true if order was processed
     * @throws IOException
     */
    private boolean waitForOrderProcessed(TradeService tradeService, String orderId, int maxTries) throws IOException, TimeoutException {
        boolean orderProcessed = false;
        int numberOfChecks = 0;
        while (!orderProcessed && numberOfChecks < maxTries) {
            RateLimiter.waitForPossibleCall(getClass());
            OpenOrders openOrders = tradeService.getOpenOrders(tradeService.createOpenOrdersParams());
            boolean orderFound = openOrders.getOpenOrders().stream().map(LimitOrder::getId).anyMatch(orderId::equals);
            if (orderFound) {
                log.debug("Waiting for order to be processed.");
                sleep(3000); //don't get your ip address banned
            } else {
                orderProcessed = true;
            }
            numberOfChecks++;
        }
        return orderProcessed;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bittrex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        try {
            AccountService accountService = getExchange().getAccountService();
            RateLimiter.waitForPossibleCall(getClass());
            return accountService.requestDepositAddress(Currency.getInstance(cryptoCurrency));
        } catch (IOException | TimeoutException e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateBuyPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount(cryptoCurrency));
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(cryptoCurrency), 2, BigDecimal.ROUND_UP);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateSellPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount(cryptoCurrency));
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(cryptoCurrency), 2, BigDecimal.ROUND_DOWN);
        }
        return null;
    }

    private BigDecimal getMeasureCryptoAmount(String cryptoCurrency) {
        if (CryptoCurrency.BTC.getCode().equals(cryptoCurrency)) {
            return XChangeExchange.BTC_RATE_SOURCE_CRYPTO_AMOUNT;
        }
        return new BigDecimal(5);
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            MarketDataService marketDataService = getExchange().getMarketDataService();
            RateLimiter.waitForPossibleCall(getClass());

            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrency);
            List<LimitOrder> asks = marketDataService.getOrderBook(currencyPair).getAsks();
            Collections.sort(asks, asksComparator);

            BigDecimal tradableLimit = getTradablePrice(cryptoAmount, asks);

            if (tradableLimit != null) {
                log.debug("Called Bittrex exchange for BUY rate: {}{} = {}", cryptoCurrency, fiatCurrency, tradableLimit);
                return tradableLimit.multiply(cryptoAmount);
            }
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            MarketDataService marketDataService = getExchange().getMarketDataService();
            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrency);
            RateLimiter.waitForPossibleCall(getClass());
            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);

            List<LimitOrder> bids = orderBook.getBids();
            Collections.sort(bids, bidsComparator);

            BigDecimal tradableLimit = getTradablePrice(cryptoAmount, bids);

            if (tradableLimit != null) {
                log.debug("Called Bittrex exchange for SELL rate: {}{} = {}", cryptoCurrency, fiatCurrency, tradableLimit);
                return tradableLimit.multiply(cryptoAmount);
            }
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;
    }



    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called bittrex exchange for rate: " + key + " = " + result);
                rateAmounts.put(key,result);
                rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            }else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                }else{
                    //do the job;
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    log.debug("Called bittrex exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String cashCurrency) {
        try {
            MarketDataService marketDataService = getExchange().getMarketDataService();
            RateLimiter.waitForPossibleCall(getClass());
            Ticker ticker = marketDataService.getTicker(new CurrencyPair(cryptoCurrency,cashCurrency));
            return ticker.getLast();
        } catch (IOException | TimeoutException e) {
            log.error("Error", e);
        }
        return null;
    }


    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bittrex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bittrex supports only " + FiatCurrency.USD.getCode() );
            return null;
        }
        return new BittrexExchange.PurchaseCoinsTask(amount,cryptoCurrency,fiatCurrencyToUse,description);
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bittrex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bittrex supports only " + FiatCurrency.USD.getCode() );
            return null;
        }
        return new BittrexExchange.SellCoinsTask(amount,cryptoCurrency,fiatCurrencyToUse,description);
    }

    class PurchaseCoinsTask implements ITask {
        private long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; //5 hours

        private BigDecimal amount;
        private String cryptoCurrency;
        private String fiatCurrencyToUse;
        private String description;

        private String orderId;
        private String result;
        private boolean finished;

        PurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
            this.amount = amount;
            this.cryptoCurrency = cryptoCurrency;
            this.fiatCurrencyToUse = fiatCurrencyToUse;
            this.description = description;
        }

        @Override
        public boolean onCreate() {
            log.info("Calling Bittrex exchange (purchase " + amount + " " + cryptoCurrency + ")");

            try {
                TradeService tradeService = getExchange().getTradeService();
                MarketDataService marketDataService = getExchange().getMarketDataService();
                CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

                OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
                List<LimitOrder> asks = orderBook.getAsks();

                Collections.sort(asks, asksComparator);

                LimitOrder order = new LimitOrder(Order.OrderType.BID, amount, currencyPair, "", null,
                    getTradablePrice(amount, asks));

                log.debug("order: {}", order);
                RateLimiter.waitForPossibleCall(getClass());
                orderId = tradeService.placeLimitOrder(order);
                log.debug("orderId = " + orderId + " " + order);

                sleep(2000); //give exchange 2 seconds to reflect open order in order book
            } catch (IOException e) {
                log.error("Bittrex exchange (purchaseCoins) failed", e);
            } catch (Throwable e) {
                log.error("Error", e);
            }
            return (orderId != null);
        }

        @Override
        public boolean onDoStep() {
            if (orderId == null) {
                log.debug("Giving up on waiting for trade to complete. Because it did not happen");
                finished = true;
                result = "Skipped";
                return false;
            }
            // get open orders
            boolean orderProcessed = false;
            long checkTillTime = System.currentTimeMillis() + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug("Giving up on waiting for trade " + orderId + " to complete");
                finished = true;
                return false;
            }

            log.debug("Open orders:");
            boolean orderFound = false;
            try {
                TradeService tradeService = getExchange().getTradeService();
                RateLimiter.waitForPossibleCall(getClass());
                OpenOrders openOrders = tradeService.getOpenOrders();
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = " + openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (IOException | TimeoutException e) {
                log.error("Error", e);
            }

            if (orderFound) {
                log.debug("Waiting for order to be processed.");
            }else{
                orderProcessed = true;
            }

            if (orderProcessed) {
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
            log.debug("Purchase task finished.");
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 5 * 1000; //it doesn't make sense to run step sooner than after 5 seconds
        }
    }

    class SellCoinsTask implements ITask {
        private long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; //5 hours

        private BigDecimal cryptoAmount;
        private String cryptoCurrency;
        private String fiatCurrencyToUse;
        private String description;

        private String orderId;
        private String result;
        private boolean finished;

        SellCoinsTask(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
            this.cryptoAmount = cryptoAmount;
            this.cryptoCurrency = cryptoCurrency;
            this.fiatCurrencyToUse = fiatCurrencyToUse;
            this.description = description;
        }

        @Override
        public boolean onCreate() {
            log.info("Calling Bittrex exchange (sell " + cryptoAmount + " " + cryptoCurrency + ")");

            try {
                TradeService tradeService = getExchange().getTradeService();
                MarketDataService marketDataService = getExchange().getMarketDataService();

                CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

                OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
                List<LimitOrder> bids = orderBook.getBids();
                log.debug("bids.size(): {}", bids.size());

                Collections.sort(bids, bidsComparator);

                LimitOrder order = new LimitOrder(Order.OrderType.ASK, cryptoAmount, currencyPair,
                    "", null, getTradablePrice(cryptoAmount, bids));

                log.debug("order: {}", order);
                RateLimiter.waitForPossibleCall(getClass());
                orderId = tradeService.placeLimitOrder(order);
                log.debug("orderId: {}", orderId);

                sleep(2000); //give exchange 2 seconds to reflect open order in order book
            } catch (IOException | TimeoutException e) {
                log.error("Bittrex exchange (sellCoins) failed", e);
            } catch (Throwable e) {
                log.error("Error", e);
            }
            return (orderId != null);
        }

        @Override
        public boolean onDoStep() {
            if (orderId == null) {
                log.debug("Giving up on waiting for trade to complete. Because it did not happen");
                finished = true;
                result = "Skipped";
                return false;
            }
            // get open orders
            boolean orderProcessed = false;
            long checkTillTime = System.currentTimeMillis() + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug("Giving up on waiting for trade " + orderId + " to complete");
                finished = true;
                return false;
            }

            log.debug("Open orders:");
            boolean orderFound = false;
            try {
                TradeService tradeService = getExchange().getTradeService();
                RateLimiter.waitForPossibleCall(getClass());
                OpenOrders openOrders = tradeService.getOpenOrders();
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = " + openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (IOException | TimeoutException e) {
                log.error("Error", e);
            }

            if (orderFound) {
                log.debug("Waiting for order to be processed.");
            }else{
                orderProcessed = true;
            }

            if (orderProcessed) {
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
            log.debug("Sell task finished.");
        }

        @Override
        public long getShortestTimeForNexStepInvocation() {
            return 5 * 1000; //it doesn't make sense to run step sooner than after 5 seconds
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.info("", e);
        }
    }

//    public static void main(String[] args) {
//        log.info(new BittrexExchange("XXX", "XXX")
//            .purchaseCoins(new BigDecimal(50), "BCH", "USD", "desc"));
//    }
}
