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
package com.generalbytes.batm.server.extensions.extra.bitcoin.sources;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.*;


public class BitcoinAverageRateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(BitcoinAverageRateSource.class);

    private static Map<String, String> fiatCurrenciesAndURLs = new HashMap<String, String>();
    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    private String preferedFiatCurrency = ICurrencies.USD;

    public BitcoinAverageRateSource(String preferedFiatCurrency) {
        if (ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.EUR;
        }
        if (ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.USD;
        }else{
            this.preferedFiatCurrency = preferedFiatCurrency;
        }

    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
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
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }

        if (fiatCurrenciesAndURLs.isEmpty()) {
            loadFiatCurrencies();
        }

        String url = fiatCurrenciesAndURLs.get(fiatCurrency);
        if (url != null) {
            IBitcoinAverage api = RestProxyFactory.createProxy(IBitcoinAverage.class, url);
            BitcoinAverageRate btcRate = api.getBTCRate();
            if (btcRate != null) {
                return btcRate.getLast();
            }
            return null;
        }else {
            return null; //unsupported fiat currency
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        if (fiatCurrenciesAndURLs == null || fiatCurrenciesAndURLs.isEmpty()) {
            //load them from the website
            loadFiatCurrencies();
        }

        Set<String> fiatCurrencies = fiatCurrenciesAndURLs.keySet();
        return new HashSet<String>(fiatCurrencies);
    }

    private void loadFiatCurrencies() {
        IBitcoinAverage api = RestProxyFactory.createProxy(IBitcoinAverage.class, "https://api.bitcoinaverage.com/ticker/global/");
        HashMap<String, String> currenciesAndURLs = api.getFiatCurrenciesAndURLs();
        if (currenciesAndURLs != null) {
            //workaround
            for (Map.Entry<String, String> entry : currenciesAndURLs.entrySet()) {
                entry.setValue(entry.getValue().replace("http://localhost","https://api.bitcoinaverage.com"));
            }

            fiatCurrenciesAndURLs = currenciesAndURLs;
        }
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }

}
