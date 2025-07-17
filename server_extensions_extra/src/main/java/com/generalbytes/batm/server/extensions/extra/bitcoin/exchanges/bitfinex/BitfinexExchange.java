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
 * Other information:
 *
 * This implementation was created in cooperation with Orillia BVBA
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitfinex;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
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
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.exceptions.CurrencyPairNotValidException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;


public class BitfinexExchange implements IExchangeAdvanced, IRateSourceAdvanced {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BitfinexExchange");
    private Exchange exchange = null;
    private String apiKey;
    private String apiSecret;
    private String preferredFiatCurrency;

    private static final HashMap<String, BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String, Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;
    private Set<String> depositCurrenciesSupported = new HashSet<>(Arrays.asList("BTC", "LTC", "ETH", "IOT", "BCH", "BTG", "EOS", "XMR", "NEO", "XRP", "XLM", "TRX", "ZEC", "DASH")); // see BitfinexAccountServiceRaw.requestDepositAddressRaw()
    private static final HashSet<String> FIAT_CURRENCIES = new HashSet<>();
    private static final HashSet<String> CRYPTO_CURRENCIES = new HashSet<>(Arrays.asList("BTC", "ETH", "LTC", "BCH", "DASH", "XMR"));

    static {
        FIAT_CURRENCIES.add(FiatCurrency.USD.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.EUR.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.GBP.getCode());
        FIAT_CURRENCIES.add(FiatCurrency.JPY.getCode());
        FIAT_CURRENCIES.add(CryptoCurrency.USDT.getCode());
    }

    /**
     * exchange
     * @param apiKey
     * @param apiSecret
     * @param preferredFiatCurrency
     */
    public BitfinexExchange(String apiKey, String apiSecret, String preferredFiatCurrency) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    /**
     * ratesource
     * @param preferredFiatCurrency
     */
    public BitfinexExchange(String preferredFiatCurrency) {
        this(null, null, preferredFiatCurrency);
    }

    private synchronized Exchange getExchange() {
        if (this.exchange == null) {
            ExchangeSpecification bfxSpec = new org.knowm.xchange.bitfinex.BitfinexExchange().getDefaultExchangeSpecification();
            bfxSpec.setApiKey(this.apiKey);
            bfxSpec.setSecretKey(this.apiSecret);
            bfxSpec.setShouldLoadRemoteMetaData(false);
            this.exchange = ExchangeFactory.INSTANCE.createExchange(bfxSpec);
            exchange.applySpecification(bfxSpec);
        }
        return this.exchange;
    }

    public Set<String> getCryptoCurrencies() {
        return CRYPTO_CURRENCIES;
    }

    public Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }

    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
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
        CurrencyPair currencyPair = new CurrencyPair(getExchangeSpecificSymbol(cryptoCurrency), getExchangeSpecificSymbol(cashCurrency));
        try {
            RateLimiter.waitForPossibleCall(getClass());
            Ticker ticker = marketDataService.getTicker(currencyPair);
            return ticker.getLast();
        } catch (CurrencyPairNotValidException e) {
            log.warn("Currency pair not valid: {}, {}", currencyPair, e.getMessage());
        } catch (IOException | TimeoutException e) {
            log.error("Error", e);
        }
        return null;
    }

    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return BigDecimal.ZERO;
        }
        log.debug("Calling Bitfinex exchange (getBalance)");

        try {
            RateLimiter.waitForPossibleCall(getClass());
            AccountInfo accountInfo = getExchange().getAccountService().getAccountInfo();
            return getWallet(accountInfo).getBalance(Currency.getInstance(getExchangeSpecificSymbol(cryptoCurrency))).getAvailable();
        } catch (IOException | TimeoutException e) {
            log.error("Bitfinex exchange (getBalance) failed", e);
        }
        return null;
    }

    public BigDecimal getFiatBalance(String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return BigDecimal.ZERO;
        }
        log.debug("Calling Bitfinex exchange (getBalance)");

        try {
            RateLimiter.waitForPossibleCall(getClass());
            AccountInfo accountInfo = getExchange().getAccountService().getAccountInfo();
            return getWallet(accountInfo).getBalance(Currency.getInstance(fiatCurrency)).getAvailable();
        } catch (IOException | TimeoutException e) {
            log.error("Bitfinex exchange (getBalance) failed", e);
        }
        return null;
    }

    private Wallet getWallet(AccountInfo accountInfo) {
        Map<String, Wallet> wallets = accountInfo.getWallets();
        if (wallets.containsKey("exchange")) {
            return wallets.get("exchange");
        }
        throw new UnsupportedOperationException("Wallets in account: " + wallets.keySet());
    }

    public final String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(getCryptoCurrencies().toArray()));
            return null;
        }

        log.info("Calling bitfinex exchange (withdrawal destination: " + destinationAddress + " amount: " + amount + " " + cryptoCurrency + ")");

        AccountService accountService = getExchange().getAccountService();
        try {
            RateLimiter.waitForPossibleCall(getClass());
            String result = accountService.withdrawFunds(Currency.getInstance(getExchangeSpecificSymbol(cryptoCurrency)), amount, destinationAddress);
            log.debug("Bitfinex exchange (withdrawFunds) result: {}", result);
            return result;
        } catch (IOException | TimeoutException e) {
            log.error("Bitfinex exchange (withdrawFunds) ", e);
        }
        return null;
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        CurrencyPair currencyPair = new CurrencyPair(getExchangeSpecificSymbol(cryptoCurrency), getExchangeSpecificSymbol(fiatCurrencyToUse));

        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }

        log.info("Calling Bitfinex exchange (purchase " + amount + " " + cryptoCurrency + ")");
        AccountService accountService = getExchange().getAccountService();
        TradeService tradeService = getExchange().getTradeService();

        try {
            log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

            MarketOrder order = new MarketOrder(Order.OrderType.BID, amount, currencyPair);
            log.debug("marketOrder = " + order);
            RateLimiter.waitForPossibleCall(getClass());
            String orderId = tradeService.placeMarketOrder(order);
            log.debug("orderId = " + orderId + " " + order);

            try {
                Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
            } catch (InterruptedException e) {
                log.error("Error", e);
            }

            // get open orders
            log.debug("Open orders:");
            boolean orderProcessed = false;
            int numberOfChecks = 0;
            while (!orderProcessed && numberOfChecks < 10) {
                boolean orderFound = false;
                OpenOrders openOrders = tradeService.getOpenOrders();
                RateLimiter.waitForPossibleCall(getClass());
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
                        log.error("Error", e);
                    }
                }else{
                    orderProcessed = true;
                }
                numberOfChecks++;
            }
            if (orderProcessed) {
                return orderId;
            }
        } catch (CurrencyPairNotValidException e) {
            log.warn("Currency pair not valid: {}, {}", currencyPair, e.getMessage());
        } catch (IOException | TimeoutException e) {
            log.error("Bitfinex exchange (purchaseCoins) failed", e);
        }
        return null;
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        return new PurchaseCoinsTask(amount,cryptoCurrency,fiatCurrencyToUse,description);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        Set<String> supportedCryptoCurrencies = depositCurrenciesSupported;
        if (!supportedCryptoCurrencies.contains(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + Arrays.toString(supportedCryptoCurrencies.toArray()) + " for deposit");
            return null;
        }
        AccountService accountService = getExchange().getAccountService();
        try {
            RateLimiter.waitForPossibleCall(getClass());
            return accountService.requestDepositAddress(Currency.getInstance(cryptoCurrency)); // here it must be without getExchangeSpecificSymbol
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        CurrencyPair currencyPair = new CurrencyPair(getExchangeSpecificSymbol(cryptoCurrency), getExchangeSpecificSymbol(fiatCurrencyToUse));

        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }

        log.info("Calling Bitfinex exchange (sell " + cryptoAmount + " " + cryptoCurrency + ")");
        AccountService accountService = getExchange().getAccountService();
        TradeService tradeService = getExchange().getTradeService();

        try {
            log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

            MarketOrder order = new MarketOrder(Order.OrderType.ASK, cryptoAmount, currencyPair);
            log.debug("marketOrder = " + order);
            RateLimiter.waitForPossibleCall(getClass());
            String orderId = tradeService.placeMarketOrder(order);
            log.debug("orderId = " + orderId + " " + order);

            try {
                Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
            } catch (InterruptedException e) {
                log.error("Error", e);
            }

            // get open orders
            log.debug("Open orders:");
            boolean orderProcessed = false;
            int numberOfChecks = 0;
            while (!orderProcessed && numberOfChecks < 10) {
                boolean orderFound = false;
                RateLimiter.waitForPossibleCall(getClass());
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
                        log.error("Error", e);
                    }
                }else{
                    orderProcessed = true;
                }
                numberOfChecks++;
            }
            if (orderProcessed) {
                return orderId;
            }
        } catch (CurrencyPairNotValidException e) {
            log.warn("Currency pair not valid: {}, {}", currencyPair, e.getMessage());
        } catch (IOException | TimeoutException e) {
            log.error("Bitfinex exchange (sellCoins) failed", e);
        }
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        return new SellCoinsTask(amount,cryptoCurrency,fiatCurrencyToUse,description);
    }

    class PurchaseCoinsTask implements ITask {
        private static final long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; //5 hours

        private final BigDecimal amount;
        private final String cryptoCurrency;
        private final String fiatCurrencyToUse;
        private final String description;
        private final long checkTillTime;

        private String orderId;
        private String result;
        private boolean finished;

        PurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
            this.amount = amount;
            this.cryptoCurrency = cryptoCurrency;
            this.fiatCurrencyToUse = fiatCurrencyToUse;
            this.description = description;
            this.checkTillTime = System.currentTimeMillis() + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
        }

        @Override
        public boolean onCreate() {
            log.info("Calling Bitfinex exchange (purchase " + amount + " " + cryptoCurrency + ")");
            AccountService accountService = getExchange().getAccountService();
            TradeService tradeService = getExchange().getTradeService();
            CurrencyPair currencyPair = new CurrencyPair(getExchangeSpecificSymbol(cryptoCurrency), getExchangeSpecificSymbol(fiatCurrencyToUse));

            try {
                log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

                MarketOrder order = new MarketOrder(Order.OrderType.BID, amount, currencyPair);
                log.debug("marketOrder = " + order);
                RateLimiter.waitForPossibleCall(getClass());
                orderId = tradeService.placeMarketOrder(order);
                log.debug("orderId = " + orderId + " " + order);

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    log.error("Error", e);
                }
            } catch (CurrencyPairNotValidException e) {
                log.warn("Currency pair not valid: {}, {}", currencyPair, e.getMessage());
            } catch (IOException e) {
                log.error("Bitfinex exchange (purchaseCoins) failed", e);
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
            TradeService tradeService = getExchange().getTradeService();
            // get open orders
            boolean orderProcessed = false;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug("Giving up on waiting for trade " + orderId + " to complete");
                finished = true;
                return false;
            }

            log.debug("Open orders:");
            boolean orderFound = false;
            try {
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
        private static final long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; //5 hours

        private final BigDecimal cryptoAmount;
        private final String cryptoCurrency;
        private final String fiatCurrencyToUse;
        private final String description;
        private final long checkTillTime;

        private String orderId;
        private String result;
        private boolean finished;

        SellCoinsTask(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
            this.cryptoAmount = cryptoAmount;
            this.cryptoCurrency = cryptoCurrency;
            this.fiatCurrencyToUse = fiatCurrencyToUse;
            this.description = description;
            this.checkTillTime = System.currentTimeMillis() + MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH;
        }

        @Override
        public boolean onCreate() {
            log.info("Calling Bitfinex exchange (sell " + cryptoAmount + " " + cryptoCurrency + ")");
            AccountService accountService = getExchange().getAccountService();
            TradeService tradeService = getExchange().getTradeService();
            CurrencyPair currencyPair = new CurrencyPair(getExchangeSpecificSymbol(cryptoCurrency), getExchangeSpecificSymbol(fiatCurrencyToUse));

            try {
                log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

                MarketOrder order = new MarketOrder(Order.OrderType.ASK, cryptoAmount, currencyPair);
                log.debug("marketOrder = " + order);
                RateLimiter.waitForPossibleCall(getClass());
                orderId = tradeService.placeMarketOrder(order);
                log.debug("orderId = " + orderId + " " + order);

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    log.error("Error", e);
                }
            } catch (CurrencyPairNotValidException e) {
                log.warn("Currency pair not valid: {}, {}", currencyPair, e.getMessage());
            } catch (IOException e) {
                log.error("Bitfinex exchange (sellCoins) failed", e);
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
            TradeService tradeService = getExchange().getTradeService();
            // get open orders
            boolean orderProcessed = false;
            if (System.currentTimeMillis() > checkTillTime) {
                log.debug("Giving up on waiting for trade " + orderId + " to complete");
                finished = true;
                return false;
            }

            log.debug("Open orders:");
            boolean orderFound = false;
            try {
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

    private BigDecimal getMeasureCryptoAmount(String cryptoCurrency) {
        if (CryptoCurrency.BTC.getCode().equals(cryptoCurrency)) {
            return XChangeExchange.BTC_RATE_SOURCE_CRYPTO_AMOUNT;
        }
        return new BigDecimal(5);
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

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        CurrencyPair currencyPair = new CurrencyPair(getExchangeSpecificSymbol(cryptoCurrency), getExchangeSpecificSymbol(fiatCurrency));
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }

        MarketDataService marketDataService = getExchange().getMarketDataService();
        try {
            RateLimiter.waitForPossibleCall(getClass());
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
                asksTotal = asksTotal.add(ask.getOriginalAmount());
                if (targetAmount.compareTo(asksTotal) <= 0) {
                    tradableLimit = ask.getLimitPrice();
                    break;
                }
            }

            if (tradableLimit != null) {
                log.debug("Called Bitfinex exchange for BUY rate: " + cryptoCurrency + fiatCurrency + " = " + tradableLimit);
                return tradableLimit.multiply(cryptoAmount);
            }
        } catch (CurrencyPairNotValidException e) {
            log.warn("Currency pair not valid: {}, {}", currencyPair, e.getMessage());
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        CurrencyPair currencyPair = new CurrencyPair(getExchangeSpecificSymbol(cryptoCurrency), getExchangeSpecificSymbol(fiatCurrency));

        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }

        MarketDataService marketDataService = getExchange().getMarketDataService();
        try {
            RateLimiter.waitForPossibleCall(getClass());
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
                bidsTotal = bidsTotal.add(bid.getOriginalAmount());
                if (targetAmount.compareTo(bidsTotal) <= 0) {
                    tradableLimit = bid.getLimitPrice();
                    break;
                }
            }

            if (tradableLimit != null) {
                log.debug("Called Bitfinex exchange for SELL rate: " + cryptoCurrency + fiatCurrency + " = " + tradableLimit);
                return tradableLimit.multiply(cryptoAmount);
            }
        } catch (CurrencyPairNotValidException e) {
            log.warn("Currency pair not valid: {}, {}", currencyPair, e.getMessage());
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;

    }

    private String getExchangeSpecificSymbol(String cryptoCurrency) {
        if (CryptoCurrency.DASH.getCode().equals(cryptoCurrency)) {
            return "DSH";
        }
        if (CryptoCurrency.BCH.getCode().equals(cryptoCurrency)) {
            return "BCHN";
        }
        if (CryptoCurrency.USDT.getCode().equals(cryptoCurrency)) {
            return "UST";
        }
        return cryptoCurrency;
    }
}
