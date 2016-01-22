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
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.maxcoin.sources;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MaxcoinTickerRateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(MaxcoinTickerRateSource.class);

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    private IMaxcoinTickerRateAPI api;

    public MaxcoinTickerRateSource() {
        api = RestProxyFactory.createProxy(IMaxcoinTickerRateAPI.class, "http://www.maxcointicker.com");
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!(ICurrencies.MAX.equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(ICurrencies.USD.equalsIgnoreCase(fiatCurrency) || ICurrencies.EUR.equalsIgnoreCase(fiatCurrency))) {
            return null;
        }

        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called MaxTicker exchange for rate: " + key + " = " + result);
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
                    log.debug("Called MaxTicker exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!(ICurrencies.MAX.equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(ICurrencies.USD.equalsIgnoreCase(fiatCurrency) || ICurrencies.EUR.equalsIgnoreCase(fiatCurrency))) {
            return null;
        }
        MaxcoinTickerResponse ticker = api.getTicker();
        if (ticker != null) {
            if (ICurrencies.USD.equalsIgnoreCase(fiatCurrency)){
                return ticker.getMpusd();
            }else if (ICurrencies.USD.equalsIgnoreCase(fiatCurrency)){
                return ticker.getBtceuro();
            }
            return null;
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.MAX);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.USD);
        result.add(ICurrencies.EUR);
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return ICurrencies.USD;
    }

    public static void main(String[] args) {
        MaxcoinTickerRateSource rs = new MaxcoinTickerRateSource();
        BigDecimal exchangeRateLast = rs.getExchangeRateLast(ICurrencies.MAX, ICurrencies.USD);
        System.out.println("exchangeRateLast = " + exchangeRateLast);
    }
}
