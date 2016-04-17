/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.shadowcash.sources.bittrex;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.BitcoinAverageRateSource;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.bittrex.v1.dto.marketdata.BittrexLevel;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class BittrexRateSource implements IRateSource{

    private BitcoinAverageRateSource btcRs;
    private String preferredFiatCurrency = ICurrencies.USD;
    private IBittrexAPI api;

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    public BittrexRateSource(String preferredFiatCurrency) {

        if (preferredFiatCurrency != null) {
            this.preferredFiatCurrency = preferredFiatCurrency;
        }

        btcRs = new BitcoinAverageRateSource(this.preferredFiatCurrency);
        api = RestProxyFactory.createProxy(IBittrexAPI.class, "https://bittrex.com");
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return btcRs.getFiatCurrencies();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return btcRs.getPreferredFiatCurrency();
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
        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
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
                    log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.SDC.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }
        BittrexOrderBookResponse orderBookResponse = api.returnOrderBook("BTC-SDC", "both", 10000);
        if (orderBookResponse != null) {
            BittrexLevel[] asks = orderBookResponse.getDepth().getAsks();
            BigDecimal asksTotal = BigDecimal.ZERO;
            BigDecimal targetAmount = new BigDecimal(10000); //calculate price based on this amount of SDC
            BigDecimal tradableLimit = BigDecimal.ZERO;

            for (int i = 0; i < asks.length; i++) {
                BittrexLevel ask = asks[i];
                log.debug("ask = " + ask);
                asksTotal = asksTotal.add(ask.getAmount());
                if (targetAmount.compareTo(asksTotal) <= 0) {
                    tradableLimit = ask.getPrice();
                    break;
                }
            }

            if (tradableLimit != null) {
                BigDecimal btcRate = btcRs.getExchangeRateLast(ICurrencies.BTC, fiatCurrency);
                if (btcRate != null) {
                    return btcRate.multiply(tradableLimit);
                }
            }
            return null;
        }

        return null;
    }
}
