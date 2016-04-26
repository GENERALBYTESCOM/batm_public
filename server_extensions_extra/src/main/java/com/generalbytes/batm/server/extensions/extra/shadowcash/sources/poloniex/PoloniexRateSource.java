/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 * <p/>
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 * <p/>
 * Contact information
 * -------------------
 * <p/>
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.shadowcash.sources.poloniex;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.BitcoinAverageRateSource;
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexDepth;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PoloniexRateSource implements IRateSource {

    private BitcoinAverageRateSource bitcoinAverageRateSource;
    private String preferredFiatCurrency = ICurrencies.USD;
    private IPoloniexAPI api;

    private static String baseUrl = "https://poloniex.com";
    private static HashMap<String, BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String, Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;

    public static final String BTC_SDC_MARKET = "BTC_SDC";
    public static final String ORDERBOOK_COMMAND = "returnOrderBook";
    public static final int ORDERBOOK_DEPTH = 50;
    private static final int SDC_AMOUNT_FOR_PRICE = 10000;

    public PoloniexRateSource(String preferredFiatCurrency, String baseUrl, BitcoinAverageRateSource bitcoinAverageRateSource) {
        this.baseUrl = baseUrl;
        setup(preferredFiatCurrency, bitcoinAverageRateSource);
    }

    public PoloniexRateSource(String preferredFiatCurrency) {
        setup(preferredFiatCurrency);
    }

    private void setup(String preferredFiatCurrency, BitcoinAverageRateSource bitcoinAverageRateSource) {
        this.bitcoinAverageRateSource = bitcoinAverageRateSource;
        setup(preferredFiatCurrency);
    }

    private void setup(String preferredFiatCurrency) {
        if (preferredFiatCurrency != null) {
            this.preferredFiatCurrency = preferredFiatCurrency;
        }

        if (this.bitcoinAverageRateSource == null) {
            this.bitcoinAverageRateSource = new BitcoinAverageRateSource(this.preferredFiatCurrency);
        }
        api = RestProxyFactory.createProxy(IPoloniexAPI.class, baseUrl);
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return bitcoinAverageRateSource.getFiatCurrencies();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return bitcoinAverageRateSource.getPreferredFiatCurrency();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.SDC);
        return result;
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.SDC.equalsIgnoreCase(cryptoCurrency)) {
            return null;
        }
        String key = cryptoCurrency + "_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                rateAmounts.put(key, result);
                rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            } else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                } else {
                    //do the job;
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    rateAmounts.put(key, result);
                    rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.SDC.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }
        PoloniexDepth orderBookResponse = api.returnOrderBook(ORDERBOOK_COMMAND, BTC_SDC_MARKET, ORDERBOOK_DEPTH);
        if (orderBookResponse != null) {
            List<List<BigDecimal>> asks = orderBookResponse.getAsks();
            BigDecimal asksTotal = BigDecimal.ZERO;
            BigDecimal targetAmount = new BigDecimal(SDC_AMOUNT_FOR_PRICE); //calculate price based on this amount of SDC
            BigDecimal tradableLimit = BigDecimal.ZERO;

            for (int i = 0; i < asks.size(); i++) {
                List<BigDecimal> ask = asks.get(i);
                asksTotal = asksTotal.add(ask.get(1));
                if (targetAmount.compareTo(asksTotal) <= 0) {
                    tradableLimit = ask.get(0);
                    break;
                }
            }

            if (tradableLimit != null) {
                BigDecimal btcRate = bitcoinAverageRateSource.getExchangeRateLast(ICurrencies.BTC, fiatCurrency);
                if (btcRate != null) {
                    return btcRate.multiply(tradableLimit);
                }
            }
            return null;
        }

        return null;
    }
}
