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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.util.OrderBookPriceCalculator;
import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.AddressWithTag;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.exceptions.CurrencyPairNotValidException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.account.params.DefaultRequestDepositAddressParams;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.DefaultWithdrawFundsParams;
import org.knowm.xchange.service.trade.params.NetworkWithdrawFundsParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Comparator.comparing;

public abstract class XChangeExchange implements IExchangeAdvanced, IRateSourceAdvanced {
    private static final OrderBookPriceCalculator<LimitOrder> orderBookPriceCalculator
        = new OrderBookPriceCalculator<>(LimitOrder::getLimitPrice, LimitOrder::getOriginalAmount);

    private final String preferredFiatCurrency;
    private static final long CACHE_REFRESH_IN_SECONDS = 30;
    private static final Cache<String, BigDecimal> rateCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(CACHE_REFRESH_IN_SECONDS, TimeUnit.SECONDS)
        .build();

    private Exchange exchange = null;

    private final String name;
    protected final Logger log;
    private final com.google.common.util.concurrent.RateLimiter rateLimiter;
    private final ExchangeSpecification exchangeSpecification;

    public static final BigDecimal BTC_RATE_SOURCE_CRYPTO_AMOUNT = BigDecimal.ONE;

    protected XChangeExchange(ExchangeSpecification specification, String preferredFiatCurrency) {
        exchangeSpecification = specification;
        String exchangeName = exchangeSpecification.getExchangeName();
        String sslUri = exchangeSpecification.getSslUri();
        name = exchangeName + " (" + sslUri + ")"; // just for logging, do not setExchangeName() as it's used to load configuration json internally
        log = LoggerFactory.getLogger("batm.master.exchange." + exchangeName);
        rateLimiter = com.google.common.util.concurrent.RateLimiter.create(getAllowedCallsPerSecond());
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    protected synchronized Exchange getExchange() {
        if (exchange == null) {
            // this calls remote host so it's lazy loaded and not called when rate is cached and no remote call is needed
            exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        }
        return exchange;
    }

    protected abstract boolean isWithdrawSuccessful(String result);

    protected abstract double getAllowedCallsPerSecond();

    private boolean isCryptoCurrencySupported(String currency) {
        if (!getCryptoCurrencies().contains(currency)) {
            log.debug("{} exchange doesn't support cryptocurrency '{}'", name, currency);
            return false;
        }
        return true;
    }

    private boolean isFiatCurrencySupported(String currency) {
        if (!getFiatCurrencies().contains(currency)) {
            log.debug("{} exchange doesn't support fiat currency '{}'", name, currency);
            return false;
        }
        return true;
    }

    class RateCaller implements Callable<BigDecimal> {
        private final String key;

        RateCaller(String key) {
            this.key = key;
        }

        @Override
        public BigDecimal call() throws IOException {
            String[] keyParts = getCacheKeyParts(key);
            String cryptoCurrency = keyParts[0];
            String fiatCurrency = keyParts[1];

            return getExchange().getMarketDataService()
                .getTicker(new CurrencyPair(translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrency))
                .getLast();
        }
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        String key = buildCacheKey(cryptoCurrency, fiatCurrency);
        try {
            BigDecimal result = rateCache.get(key, new RateCaller(key));
            log.debug("{} exchange rate request: {} = {}", name, key, result);
            return result;
        } catch (ExecutionException | UncheckedExecutionException e) {
            log.error("{} exchange rate request: {}", name, key, e);
            return null;
        }
    }

    private String buildCacheKey(String cryptoCurrency, String fiatCurrency) {
        return String.format("%s:%s", cryptoCurrency, fiatCurrency);
    }

    private String[] getCacheKeyParts(String key) {
        return key.split(":");
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return BigDecimal.ZERO;
        }
        try {
            String cryptocurrencyTicker = translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency);
            AccountInfo accountInfo = getExchange().getAccountService().getAccountInfo();
            Wallet wallet = getWallet(accountInfo, cryptocurrencyTicker);
            BigDecimal balance = wallet.getBalance(Currency.getInstance(cryptocurrencyTicker)).getAvailable();
            log.debug("{} exchange balance request: {} = {}", name, cryptoCurrency, balance);
            return balance;
        } catch (IOException e) {
            log.error("Error", e);
            log.error("{} exchange balance request: {}", name, cryptoCurrency, e);
        }
        return null;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        if (!isFiatCurrencySupported(fiatCurrency)) {
            return BigDecimal.ZERO;
        }
        try {
            AccountInfo accountInfo = getExchange().getAccountService().getAccountInfo();
            Wallet wallet = getWallet(accountInfo, fiatCurrency);
            BigDecimal balance = wallet.getBalance(Currency.getInstance(fiatCurrency)).getAvailable();
            log.debug("{} exchange balance request: {} = {}", name, fiatCurrency, balance);
            return balance;
        } catch (Exception e) {
            log.error("Error", e);
            log.error("{} exchange balance request: {}", name, fiatCurrency, e);
        }
        return null;
    }

    public Wallet getWallet(AccountInfo accountInfo, String currency) {
        return accountInfo.getWallet(translateCryptoCurrencySymbolToExchangeSpecificSymbol(currency));
    }

    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }

        if (CryptoCurrency.XRP.getCode().equals(cryptoCurrency) || CryptoCurrency.USDTTRON.getCode().equals(cryptoCurrency)) {
            amount = amount.setScale(6, RoundingMode.FLOOR);
        }

        log.info("{} exchange withdrawing {} {} to {}", name, amount, cryptoCurrency, destinationAddress);

        try {
            String result = withdrawFunds(cryptoCurrency, amount, destinationAddress);
            if (isWithdrawSuccessful(result)) {
                log.debug("{} exchange withdrawal completed with result: {}", name, result);
                return "success";
            } else {
                log.error("{} exchange withdrawal failed with result: '{}'", name, result);
            }
        } catch (HttpStatusIOException e) {
            log.info("{} exchange withdrawal failed; HTTP status: {}, body: {}", name, e.getHttpStatusCode(), e.getHttpBody(), e);
        } catch (IOException | ExchangeException e) {
            log.error("{} exchange withdrawal failed", name, e);
        }
        return null;
    }

    private String withdrawFunds(String cryptoCurrency, BigDecimal amount, String destinationAddress) throws IOException {
        AccountService accountService = getExchange().getAccountService();
        Currency exchangeCryptoCurrency = Currency.getInstance(translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency));

        if (CryptoCurrency.USDTTRON.getCode().equals(cryptoCurrency)) {
            NetworkWithdrawFundsParams usdtTronFundsParams = NetworkWithdrawFundsParams.builder()
                .currency(Currency.USDT)
                .network(CryptoCurrency.TRX.getCode())
                .address(destinationAddress)
                .amount(amount)
                .build();

            return accountService.withdrawFunds(usdtTronFundsParams);
        }

        if (CryptoCurrency.XRP.getCode().equals(cryptoCurrency) || CryptoCurrency.BNB.getCode().equals(cryptoCurrency)) {
            String[] addressParts = destinationAddress.split(":");
            if (addressParts.length == 2) {
                return accountService.withdrawFunds(
                    new DefaultWithdrawFundsParams(new AddressWithTag(addressParts[0], addressParts[1]), exchangeCryptoCurrency, amount)
                );
            }
        }

        return accountService.withdrawFunds(exchangeCryptoCurrency, getWithdrawAmount(amount, cryptoCurrency), destinationAddress);
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency == null || fiatCurrencyToUse == null) {
            return null;
        }
        if (!isFiatCurrencySupported(fiatCurrencyToUse)) {
            return null;
        }
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }

        AccountService accountService = getExchange().getAccountService();
        MarketDataService marketDataService = getExchange().getMarketDataService();
        TradeService tradeService = getExchange().getTradeService();

        try {
            log.debug("AccountInfo as String: {}", accountService.getAccountInfo());

            CurrencyPair currencyPair = new CurrencyPair(
                translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrencyToUse
            );

            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> asks = orderBook.getAsks();
            BigDecimal tradablePrice = orderBookPriceCalculator.getBuyPrice(amount, asks);
            log.debug("tradablePrice: {}", tradablePrice);
            LimitOrder order = new LimitOrder(
                Order.OrderType.BID, getTradableAmount(amount, currencyPair), currencyPair, "", null, tradablePrice
            );
            log.debug("order = {}", order);
            RateLimiter.waitForPossibleCall(getClass());
            String orderId = tradeService.placeLimitOrder(order);
            log.debug("orderId = {} {}", orderId, order);

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
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = {}", openOrder);
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
                } else {
                    orderProcessed = true;
                }
                numberOfChecks++;
            }
            if (orderProcessed) {
                return orderId;
            }
        } catch (IOException | TimeoutException e) {
            log.error("{} exchange purchase coins failed", name, e);
        }
        return null;
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency == null || fiatCurrencyToUse == null) {
            return null;
        }
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }
        if (!isFiatCurrencySupported(fiatCurrencyToUse)) {
            return null;
        }
        return new PurchaseCoinsTask(amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        if (cryptoCurrency == null) {
            return null;
        }
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }

        AccountService accountService = getExchange().getAccountService();
        try {
            if (CryptoCurrency.XRP.getCode().equals(cryptoCurrency) || CryptoCurrency.BNB.getCode().equals(cryptoCurrency)) {
                AddressWithTag addressWithTag = accountService.requestDepositAddressData(
                    Currency.getInstance(translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency))
                );
                if (addressWithTag == null) {
                    return null;
                }
                if (addressWithTag.getAddressTag() == null || addressWithTag.getAddressTag().isEmpty()) {
                    return addressWithTag.getAddress();
                }
                return addressWithTag.getAddress() + ":" + addressWithTag.getAddressTag();
            }

            if (CryptoCurrency.USDTTRON.getCode().equals(cryptoCurrency)) {
                DefaultRequestDepositAddressParams usdtTronDepositAddressParams = DefaultRequestDepositAddressParams.builder()
                    .currency(Currency.USDT)
                    .network(CryptoCurrency.TRX.getCode())
                    .build();

                return accountService.requestDepositAddress(usdtTronDepositAddressParams);
            }

            return accountService.requestDepositAddress(
                Currency.getInstance(translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency))
            );
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency == null || fiatCurrencyToUse == null) {
            return null;
        }

        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }

        if (!isFiatCurrencySupported(fiatCurrencyToUse)) {
            return null;
        }

        log.info("Calling {} exchange (sell {} {})", name, cryptoAmount, cryptoCurrency);
        AccountService accountService = getExchange().getAccountService();
        MarketDataService marketDataService = getExchange().getMarketDataService();
        TradeService tradeService = getExchange().getTradeService();

        try {
            log.debug("AccountInfo as String: {}", accountService.getAccountInfo());

            CurrencyPair currencyPair = new CurrencyPair(
                translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrencyToUse
            );

            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> bids = orderBook.getBids();
            log.debug("bids.size(): {}", bids.size());

            BigDecimal tradablePrice = orderBookPriceCalculator.getSellPrice(cryptoAmount, bids);
            log.debug("tradablePrice: {}", tradablePrice);
            LimitOrder order = new LimitOrder(
                Order.OrderType.ASK, getTradableAmount(cryptoAmount, currencyPair), currencyPair, "", null, tradablePrice
            );

            log.debug("order: {}", order);
            RateLimiter.waitForPossibleCall(getClass());
            String orderId = tradeService.placeLimitOrder(order);
            log.debug("orderId = {} {}", orderId, order);

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
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = {}", openOrder);
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
                } else {
                    orderProcessed = true;
                }
                numberOfChecks++;
            }
            if (orderProcessed) {
                return orderId;
            }
        } catch (IOException | TimeoutException e) {
            log.error("{} exchange sell coins failed", name, e);
        }
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency == null || fiatCurrencyToUse == null) {
            return null;
        }
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }
        if (!isFiatCurrencySupported(fiatCurrencyToUse)) {
            return null;
        }
        return new SellCoinsTask(amount, cryptoCurrency, fiatCurrencyToUse, description);
    }

    protected BigDecimal getRateSourceCryptoVolume(String cryptoCurrency) {
        if (CryptoCurrency.BTC.getCode().equals(cryptoCurrency)) {
            return BTC_RATE_SOURCE_CRYPTO_AMOUNT;
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
        if (cryptoCurrency == null || fiatCurrency == null) {
            return null;
        }
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }
        if (!isFiatCurrencySupported(fiatCurrency)) {
            return null;
        }

        rateLimiter.acquire();
        MarketDataService marketDataService = getExchange().getMarketDataService();
        try {
            CurrencyPair currencyPair = new CurrencyPair(
                translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrency
            );
            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> asks = orderBook.getAsks();
            BigDecimal targetAmount = cryptoAmount;
            BigDecimal asksTotal = BigDecimal.ZERO;
            BigDecimal tradableLimit = null;

            asks.sort(comparing(LimitOrder::getLimitPrice));

            for (LimitOrder ask : asks) {
                asksTotal = asksTotal.add(ask.getOriginalAmount());
                if (targetAmount.compareTo(asksTotal) <= 0) {
                    tradableLimit = ask.getLimitPrice();
                    break;
                }
            }

            if (tradableLimit == null) {
                log.error("Not enough asks received from the exchange, asks count: {}, asks total: {}, crypto amount: {}",
                    asks.size(), asksTotal, cryptoAmount);
                return null;
            }
            log.debug("Called {} exchange for BUY rate: {}:{} = {}", name, cryptoCurrency, fiatCurrency, tradableLimit);
            return tradableLimit.multiply(cryptoAmount);
        } catch (CurrencyPairNotValidException e) {
            log.warn("{} exchange failed to calculate buy price: Currency pair {}-{} not valid", name, cryptoCurrency, fiatCurrency);
            return null;
        } catch (Exception e) {
            log.error("{} exchange failed to calculate buy price", name, e);
            return null;
        }
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        if (cryptoCurrency == null || fiatCurrency == null) {
            return null;
        }
        if (!isCryptoCurrencySupported(cryptoCurrency)) {
            return null;
        }
        if (!isFiatCurrencySupported(fiatCurrency)) {
            return null;
        }

        rateLimiter.acquire();
        MarketDataService marketDataService = getExchange().getMarketDataService();
        try {
            CurrencyPair currencyPair = new CurrencyPair(
                translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrency
            );

            OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
            List<LimitOrder> bids = orderBook.getBids();

            BigDecimal targetAmount = cryptoAmount;
            BigDecimal bidsTotal = BigDecimal.ZERO;
            BigDecimal tradableLimit = null;

            bids.sort((lhs, rhs) -> rhs.getLimitPrice().compareTo(lhs.getLimitPrice()));

            for (LimitOrder bid : bids) {
                bidsTotal = bidsTotal.add(bid.getOriginalAmount());
                if (targetAmount.compareTo(bidsTotal) <= 0) {
                    tradableLimit = bid.getLimitPrice();
                    break;
                }
            }

            if (tradableLimit == null) {
                log.error("Not enough bids received from the exchange, bids count: {}, bids total: {}, crypto amount: {}, currency pair: {}",
                    bids.size(), bidsTotal, cryptoAmount, currencyPair);
                return null;
            }
            log.debug("Called {} exchange for SELL rate: {}:{} = {}", name, cryptoCurrency, fiatCurrency, tradableLimit);
            return tradableLimit.multiply(cryptoAmount);
        } catch (CurrencyPairNotValidException e) {
            log.warn("{} exchange failed to calculate sell price: Currency pair {}-{} not valid", name, cryptoCurrency, fiatCurrency);
            return null;
        } catch (Exception e) {
            log.error("{} exchange failed to calculate sell price", name, e);
            return null;
        }
    }

    class PurchaseCoinsTask implements ITask {
        private static final long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000L; //5 hours

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
            log.debug("{} exchange purchase {} {}", name, amount, cryptoCurrency);
            AccountService accountService = getExchange().getAccountService();
            MarketDataService marketDataService = getExchange().getMarketDataService();
            TradeService tradeService = getExchange().getTradeService();

            try {
                log.debug("AccountInfo as String: {}", accountService.getAccountInfo());

                CurrencyPair currencyPair = new CurrencyPair(
                    translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrencyToUse
                );

                OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
                List<LimitOrder> asks = orderBook.getAsks();

                BigDecimal tradablePrice = orderBookPriceCalculator.getBuyPrice(amount, asks);
                log.debug("tradablePrice: {}", tradablePrice);
                LimitOrder order = new LimitOrder(
                    Order.OrderType.BID, getTradableAmount(amount, currencyPair), currencyPair, "", null, tradablePrice
                );

                log.debug("limitOrder = {}", order);

                orderId = tradeService.placeLimitOrder(order);
                log.debug("orderId = {} {}", orderId, order);

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    log.error("Error", e);
                }
            } catch (IOException e) {
                log.error("Error", e);
                log.error("{} exchange purchase task failed", name, e);
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
                log.debug("Giving up on waiting for trade {} to complete", orderId);
                finished = true;
                return false;
            }

            log.debug("Open orders:");
            boolean orderFound = false;
            try {
                OpenOrders openOrders = tradeService.getOpenOrders();
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = {}", openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("Error", e);
            }

            if (orderFound) {
                log.debug("Waiting for order to be processed.");
            } else {
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
            return 5 * 1000L; //it doesn't make sense to run step sooner than after 5 seconds
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
            log.info("Calling {} exchange (sell {} {})", name, cryptoAmount, cryptoCurrency);
            AccountService accountService = getExchange().getAccountService();
            MarketDataService marketDataService = getExchange().getMarketDataService();
            TradeService tradeService = getExchange().getTradeService();

            try {
                log.debug("AccountInfo as String: {}", accountService.getAccountInfo());

                CurrencyPair currencyPair = new CurrencyPair(
                    translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrencyToUse
                );

                OrderBook orderBook = marketDataService.getOrderBook(currencyPair);
                List<LimitOrder> bids = orderBook.getBids();
                log.debug("bids.size(): {}", bids.size());

                BigDecimal tradablePrice = orderBookPriceCalculator.getSellPrice(cryptoAmount, bids);
                log.debug("tradablePrice: {}", tradablePrice);
                LimitOrder order = new LimitOrder(
                    Order.OrderType.ASK, getTradableAmount(cryptoAmount, currencyPair), currencyPair, "", null, tradablePrice
                );
                log.debug("order = {}", order);

                RateLimiter.waitForPossibleCall(getClass());
                orderId = tradeService.placeLimitOrder(order);
                log.debug("orderId = {} {}", orderId, order);

                try {
                    Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
                } catch (InterruptedException e) {
                    log.error("Error", e);
                }
            } catch (IOException | ExchangeException e) {
                log.error("{} exchange sell coins task failed", name, e);
            } catch (Exception e) {
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
                log.debug("Giving up on waiting for trade {} to complete", orderId);
                finished = true;
                return false;
            }

            log.debug("Open orders:");
            boolean orderFound = false;
            try {
                OpenOrders openOrders = tradeService.getOpenOrders();
                for (LimitOrder openOrder : openOrders.getOpenOrders()) {
                    log.debug("openOrder = {}", openOrder);
                    if (orderId.equals(openOrder.getId())) {
                        orderFound = true;
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("Error", e);
            }

            if (orderFound) {
                log.debug("Waiting for order to be processed.");
            } else {
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

    /**
     * @param cryptoAmount
     * @param currencyPair
     * @return Adjusted crypto amount that is possible to be traded on the exchange
     * (e.g. rounded for the right precision that the exchange supports)
     */
    protected BigDecimal getTradableAmount(BigDecimal cryptoAmount, CurrencyPair currencyPair) {
        return cryptoAmount;
    }

    /**
     * @param cryptoAmount
     * @param cryptoCurrency
     * @return Adjusted crypto amount that is possible to be withdrawn on the exchange
     * (e.g. rounded for the right precision that the exchange supports)
     */
    protected BigDecimal getWithdrawAmount(BigDecimal cryptoAmount, String cryptoCurrency) {
        // call getTradableAmount by default and round cryptoAmount even when withdraw has no specific requirements for exchange
        CurrencyPair pair = new CurrencyPair(
            translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), getPreferredFiatCurrency()
        );
        return getTradableAmount(cryptoAmount, pair);
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    protected String translateCryptoCurrencySymbolToExchangeSpecificSymbol(String from) {
        if (CryptoCurrency.USDTTRON.getCode().equals(from)) {
            return CryptoCurrency.USDT.getCode();
        }

        return from;
    }

    protected BigDecimal getWithdrawalFee(String cryptoCurrency) {
        String cryptocurrencyTicker = translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency);

        Currency exchangeCryptocurrency = Currency.getInstance(translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptocurrencyTicker));
        BigDecimal withdrawalFee = exchange.getExchangeMetaData().getCurrencies().get(exchangeCryptocurrency).getWithdrawalFee();
        log.info("Withdrawal fee: {} {}", withdrawalFee, cryptocurrencyTicker);
        return withdrawalFee;
    }
}
