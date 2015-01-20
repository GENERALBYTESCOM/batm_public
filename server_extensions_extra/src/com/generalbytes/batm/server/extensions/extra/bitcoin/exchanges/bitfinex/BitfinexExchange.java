/*************************************************************************************
 * Copyright (C) 2014 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.server.extensions.*;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.service.polling.PollingTradeService;

public class BitfinexExchange implements IExchangeAdvanced, IRateSource {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BitfinexExchange");
    private Exchange exchange = null;
    private String apiKey;
    private String apiSecret;

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;

    public BitfinexExchange(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    private synchronized Exchange getExchange() {
        if (this.exchange == null) {
            ExchangeSpecification bfxSpec = new com.xeiam.xchange.bitfinex.v1.BitfinexExchange().getDefaultExchangeSpecification();
            bfxSpec.setApiKey(this.apiKey);
            bfxSpec.setSecretKey(this.apiSecret);
            this.exchange = ExchangeFactory.INSTANCE.createExchange(bfxSpec);
        }
        return this.exchange;
    }

    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<String>();
        cryptoCurrencies.add(ICurrencies.BTC);
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
        PollingMarketDataService marketDataService = getExchange().getPollingMarketDataService();
        try {
            Ticker ticker = marketDataService.getTicker(new CurrencyPair(cryptoCurrency,cashCurrency));
            return ticker.getLast();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        // [TODO] Can be extended to support LTC and DRK (and other currencies supported by BFX)
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return BigDecimal.ZERO;
        }
        log.debug("Calling Bitfinex exchange (getBalance)");

        try {
            return getExchange().getPollingAccountService().getAccountInfo().getBalance(cryptoCurrency);
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
            return getExchange().getPollingAccountService().getAccountInfo().getBalance(fiatCurrency);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Bitfinex exchange (getBalance) failed with message: " + e.getMessage());
        }
        return null;
    }

    public final String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Bitfinex supports only " + ICurrencies.BTC);
            return null;
        }

        log.info("Calling bitfinex exchange (withdrawal destination: " + destinationAddress + " amount: " + amount + " " + cryptoCurrency + ")");

        PollingAccountService accountService = getExchange().getPollingAccountService();
        try {
            String result = accountService.withdrawFunds(cryptoCurrency, amount, destinationAddress);
            if (result == null) {
                return null;
            }else if ("success".equalsIgnoreCase(result)){
                return "success";
            }else{
                log.warn("Bitfinex exchange (withdrawFunds) failed with message no message");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Bitfinex exchange (withdrawFunds) failed with message: " + e.getMessage());
        }
        return null;
    }

    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + ICurrencies.BTC);
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bitfinex supports only " + ICurrencies.USD );
            return null;
        }

        log.info("Calling Bitfinex exchange (purchase " + amount + " " + cryptoCurrency + ")");
        PollingAccountService accountService = getExchange().getPollingAccountService();
        PollingTradeService tradeService = getExchange().getPollingTradeService();

        try {
            log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

            CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

            MarketOrder order = new MarketOrder(OrderType.BID, amount, currencyPair);
            log.debug("marketOrder = " + order);

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
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + ICurrencies.BTC);
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrencyToUse)) {
            log.error("Bitfinex supports only " + ICurrencies.USD );
            return null;
        }
        return new PurchaseCoinsTaks(amount,cryptoCurrency,fiatCurrencyToUse,description);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Bitfinex implementation supports only " + ICurrencies.BTC);
            return null;
        }
        PollingAccountService accountService = getExchange().getPollingAccountService();
        try {
            return accountService.requestDepositAddress(cryptoCurrency);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        //TODO: NOT SUPPORTED YET
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        //TODO: NOT SUPPORTED YET
        return null;
    }

    class PurchaseCoinsTaks implements ITask {
        private long MAXIMUM_TIME_TO_WAIT_FOR_ORDER_TO_FINISH = 5 * 60 * 60 * 1000; //5 hours

        private BigDecimal amount;
        private String cryptoCurrency;
        private String fiatCurrencyToUse;
        private String description;

        private String orderId;
        private String result;
        private boolean finished;

        PurchaseCoinsTaks(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
            this.amount = amount;
            this.cryptoCurrency = cryptoCurrency;
            this.fiatCurrencyToUse = fiatCurrencyToUse;
            this.description = description;
        }

        @Override
        public boolean onCreate() {
            log.info("Calling Bitfinex exchange (purchase " + amount + " " + cryptoCurrency + ")");
            PollingAccountService accountService = getExchange().getPollingAccountService();
            PollingTradeService tradeService = getExchange().getPollingTradeService();

            try {
                log.debug("AccountInfo as String: " + accountService.getAccountInfo().toString());

                CurrencyPair currencyPair = new CurrencyPair(cryptoCurrency, fiatCurrencyToUse);

                MarketOrder order = new MarketOrder(OrderType.BID, amount, currencyPair);
                log.debug("marketOrder = " + order);

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
            PollingTradeService tradeService = getExchange().getPollingTradeService();
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
}
