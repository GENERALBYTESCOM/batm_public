/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * Other information:
 *
 * This implementation was created in cooperation with Orillia BVBA
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitfinex;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.generalbytes.batm.server.coinutil.DDOSUtils;
import com.generalbytes.batm.server.extensions.*;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BitfinexExchange implements IExchangeAdvanced, IRateSourceAdvanced {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BitfinexExchange");
    private Exchange exchange = null;
    private String apiKey;
    private String apiSecret;

    private static final HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;

    public BitfinexExchange(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    private synchronized Exchange getExchange() {
        if (this.exchange == null) {
            ExchangeSpecification bfxSpec = new org.knowm.xchange.bitfinex.v1.BitfinexExchange().getDefaultExchangeSpecification();
            bfxSpec.setApiKey(this.apiKey);
            bfxSpec.setSecretKey(this.apiSecret);
            this.exchange = ExchangeFactory.INSTANCE.createExchange(bfxSpec);
        }
        return this.exchange;
    }

    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<String>();
        cryptoCurrencies.add(ICurrencies.BTC);
        cryptoCurrencies.add(ICurrencies.ETH);
        cryptoCurrencies.add(ICurrencies.LTC);
        return cryptoCurrencies;
    }

    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<String>();
        fiatCurrencies.add(ICurrencies.USD);
        return fiatCurrencies;
    }

    public String getPreferredFiatCurrency() {
        return ICurrencies.USD;
    }


    @Override
    public synchronized BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called bitfinex exchange for rate: " + key + " = " + result);
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
                    log.debug("Called bitfinex exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String cashCurrency) {
        MarketDataService marketDataService = getExchange().getMarketDataService();
        try {
            DDOSUtils.waitForPossibleCall(getClass());
            Ticker ticker = marketDataService.getTicker(new CurrencyPair(cryptoCurrency,cashCurrency));
            return ticker.getLast();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        // [TODO] Can be extended to support LTC and DRK (and other currencies supported by BFX)
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return BigDecimal.ZERO;
        }
        log.debug("Calling Bitfinex exchange (getBalance)");

        try {
            DDOSUtils.waitForPossibleCall(getClass());
            return getExchange().getAccountService().getAccountInfo().getWallet().getBalance(Currency.getInstance(cryptoCurrency)).getAvailable();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Bitfinex exchange (getBalance) failed with message: " + e.getMessage());
        }
        return null;
    }

    public BigDecimal getFiatBalance(String fiatCurrency) {
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrency)) {
            return BigDecimal.ZERO;
        }
        log.debug("Calling Bitfinex exchange (getBalance)");

        try {
            DDOSUtils.waitForPossibleCall(getClass());
            return getExchange().getAccountService().getAccountInfo().getWallet().getBalance(Currency.getInstance(fiatCurrency)).getAvailable();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Bitfinex exchange (getBalance) failed with message: " + e.getMessage());
        }
        return null;
    }

    public final String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }

        log.info("Calling bitfinex exchange (withdrawal destination: " + destinationAddress + " amount: " + amount + " " + cryptoCurrency + ")");

        AccountService accountService = getExchange().getAccountService();
        try {
            DDOSUtils.waitForPossibleCall(getClass());
            String result = accountService.withdrawFunds(Currency.getInstance(cryptoCurrency), amount, destinationAddress);
            if (result == null) {
                log.warn("Bitfinex exchange (withdrawFunds) failed with null");
                return null;
            }else if ("success".equalsIgnoreCase(result)){
                log.warn("Bitfinex exchange (withdrawFunds) finished successfully");
                return "success";
            }else{
                log.warn("Bitfinex exchange (withdrawFunds) failed with message: " + result);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Bitfinex exchange (withdrawFunds) failed with message: " + e.getMessage());
        }
        return null;
    }

    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bitfinex supports only " + ICurrencies.USD );
            return null;
        }

        log.info("Calling Bitfinex exchange (purchase " + amount + " " + cryptoCurrency + ")");
        AccountService accountService = getExchange().getAccountService();
        TradeService tradeService = getExchange().getTradeService();

        try {
            log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

            MarketOrder order = new MarketOrder(Order.OrderType.BID, amount, currencyPair);
            log.debug("marketOrder = " + order);
            DDOSUtils.waitForPossibleCall(getClass());
            String orderId = tradeService.placeMarketOrder(order);
            log.debug("orderId = " + orderId + " " + order);

            try {
                Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // get open orders
            log.debug("Open orders:");
            boolean orderProcessed = false;
            int numberOfChecks = 0;
            while (!orderProcessed && numberOfChecks < 10) {
                boolean orderFound = false;
                OpenOrders openOrders = tradeService.getOpenOrders();
                DDOSUtils.waitForPossibleCall(getClass());
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = " + openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
                if (orderFound) {
                    log.debug("Waiting for order to be processed.");
                    try {
                        Thread.sleep(3000); //don't get your ip address banned
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    orderProcessed = true;
                }
                numberOfChecks++;
            }
            if (orderProcessed) {
                return orderId;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Bitfinex exchange (purchaseCoins) failed with message: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bitfinex supports only " + ICurrencies.USD );
            return null;
        }
        return new PurchaseCoinsTask(amount,cryptoCurrency,fiatCurrencyToUse,description);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        AccountService accountService = getExchange().getAccountService();
        try {
            DDOSUtils.waitForPossibleCall(getClass());
            return accountService.requestDepositAddress(Currency.getInstance(cryptoCurrency));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bitfinex supports only " + ICurrencies.USD );
            return null;
        }

        log.info("Calling Bitfinex exchange (sell " + cryptoAmount + " " + cryptoCurrency + ")");
        AccountService accountService = getExchange().getAccountService();
        TradeService tradeService = getExchange().getTradeService();

        try {
            log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

            MarketOrder order = new MarketOrder(Order.OrderType.ASK, cryptoAmount, currencyPair);
            log.debug("marketOrder = " + order);
            DDOSUtils.waitForPossibleCall(getClass());
            String orderId = tradeService.placeMarketOrder(order);
            log.debug("orderId = " + orderId + " " + order);

            try {
                Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // get open orders
            log.debug("Open orders:");
            boolean orderProcessed = false;
            int numberOfChecks = 0;
            while (!orderProcessed && numberOfChecks < 10) {
                boolean orderFound = false;
                DDOSUtils.waitForPossibleCall(getClass());
                OpenOrders openOrders = tradeService.getOpenOrders();
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = " + openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
                if (orderFound) {
                    log.debug("Waiting for order to be processed.");
                    try {
                        Thread.sleep(3000); //don't get your ip address banned
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    orderProcessed = true;
                }
                numberOfChecks++;
            }
            if (orderProcessed) {
                return orderId;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Bitfinex exchange (sellCoins) failed with message: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bitfinex supports only " + ICurrencies.USD );
            return null;
        }
        return new SellCoinsTask(amount,cryptoCurrency,fiatCurrencyToUse,description);
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
            log.info("Calling Bitfinex exchange (purchase " + amount + " " + cryptoCurrency + ")");
            AccountService accountService = getExchange().getAccountService();
            TradeService tradeService = getExchange().getTradeService();

            try {
                log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

                CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

                MarketOrder order = new MarketOrder(Order.OrderType.BID, amount, currencyPair);
                log.debug("marketOrder = " + order);
                DDOSUtils.waitForPossibleCall(getClass());
                orderId = tradeService.placeMarketOrder(order);
                log.debug("orderId = " + orderId + " " + order);

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Bitfinex exchange (purchaseCoins) failed with message: " + e.getMessage());
            } catch (Throwable e) {
                e.printStackTrace();
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
            TradeService tradeService = getExchange().getTradeService();
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
                DDOSUtils.waitForPossibleCall(getClass());
                OpenOrders openOrders = tradeService.getOpenOrders();
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = " + openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
            log.info("Calling Bitfinex exchange (sell " + cryptoAmount + " " + cryptoCurrency + ")");
            AccountService accountService = getExchange().getAccountService();
            TradeService tradeService = getExchange().getTradeService();

            try {
                log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

                CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

                MarketOrder order = new MarketOrder(Order.OrderType.ASK, cryptoAmount, currencyPair);
                log.debug("marketOrder = " + order);
                DDOSUtils.waitForPossibleCall(getClass());
                orderId = tradeService.placeMarketOrder(order);
                log.debug("orderId = " + orderId + " " + order);

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Bitfinex exchange (sellCoins) failed with message: " + e.getMessage());
            } catch (Throwable e) {
                e.printStackTrace();
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
            TradeService tradeService = getExchange().getTradeService();
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
                DDOSUtils.waitForPossibleCall(getClass());
                OpenOrders openOrders = tradeService.getOpenOrders();
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = " + openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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

    private BigDecimal getMeasureCryptoAmount() {
        return new BigDecimal(5);
    }


    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateBuyPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount());
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(), 2, BigDecimal.ROUND_UP);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BigDecimal result = calculateSellPrice(cryptoCurrency, fiatCurrency, getMeasureCryptoAmount());
        if (result != null) {
            return result.divide(getMeasureCryptoAmount(), 2, BigDecimal.ROUND_DOWN);
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        DDOSUtils.waitForPossibleCall(getClass());
        MarketDataService marketDataService = getExchange().getMarketDataService();
        try {
            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrency);
            DDOSUtils.waitForPossibleCall(getClass());
            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> asks = orderBook.getAsks();
            BigDecimal targetAmount = cryptoAmount;
            BigDecimal asksTotal = BigDecimal.ZERO;
            BigDecimal tradableLimit = null;
            Collections.sort(asks, new Comparator<LimitOrder>() {
                @Override
                public int compare(LimitOrder lhs, LimitOrder rhs) {
                    return lhs.getLimitPrice().compareTo(rhs.getLimitPrice());
                }
            });

//            log.debug("Selected asks:");
            for (LimitOrder ask : asks) {
//                log.debug("ask = " + ask);
                asksTotal = asksTotal.add(ask.getTradableAmount());
                if (targetAmount.compareTo(asksTotal) <= 0) {
                    tradableLimit = ask.getLimitPrice();
                    break;
                }
            }

            if (tradableLimit != null) {
                log.debug("Called Bitfinex exchange for BUY rate: " + cryptoCurrency + fiatCurrency + " = " + tradableLimit);
                return tradableLimit.multiply(cryptoAmount);
            }
        } catch (ExchangeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        DDOSUtils.waitForPossibleCall(getClass());
        MarketDataService marketDataService = getExchange().getMarketDataService();
        try {
            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrency);
            DDOSUtils.waitForPossibleCall(getClass());
            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> bids = orderBook.getBids();

            BigDecimal targetAmount = cryptoAmount;
            BigDecimal bidsTotal = BigDecimal.ZERO;
            BigDecimal tradableLimit = null;

            Collections.sort(bids, new Comparator<LimitOrder>() {
                @Override
                public int compare(LimitOrder lhs, LimitOrder rhs) {
                    return rhs.getLimitPrice().compareTo(lhs.getLimitPrice());
                }
            });

            for (LimitOrder bid : bids) {
                bidsTotal = bidsTotal.add(bid.getTradableAmount());
                if (targetAmount.compareTo(bidsTotal) <= 0) {
                    tradableLimit = bid.getLimitPrice();
                    break;
                }
            }

            if (tradableLimit != null) {
                log.debug("Called Bitfinex exchange for SELL rate: " + cryptoCurrency + fiatCurrency + " = " + tradableLimit);
                return tradableLimit.multiply(cryptoAmount);
            }
        } catch (ExchangeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }
}
