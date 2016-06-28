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


public class BitcoinAverageRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(BitcoinAverageRateSource.class);

    private static Map<String, String> fiatCurrenciesAndURLs = new HashMap<String, String>();
    private static HashMap<String, BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String, Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    private static String baseUrl = "https://api.bitcoinaverage.com";
    private IBitcoinAverage api;

    private String preferredFiatCurrency = ICurrencies.USD;

    public BitcoinAverageRateSource(String preferredFiatCurrency) {
        this(preferredFiatCurrency, baseUrl);
    }

    /**
     * This constructor is for manually setting the baseUrl so we can mock the api for tests
     *
     * @param preferredFiatCurrency
     * @param baseUrl
     */
    public BitcoinAverageRateSource(String preferredFiatCurrency, String baseUrl) {

        // this seems pretty pointless, but whatever
        if (ICurrencies.EUR.equalsIgnoreCase(preferredFiatCurrency)) {
            this.preferredFiatCurrency = ICurrencies.EUR;
        } else if (ICurrencies.USD.equalsIgnoreCase(preferredFiatCurrency)) {
            this.preferredFiatCurrency = ICurrencies.USD;
        } else {
            this.preferredFiatCurrency = preferredFiatCurrency;
        }

        this.baseUrl = baseUrl;
        this.api = RestProxyFactory.createProxy(IBitcoinAverage.class, baseUrl);
        loadFiatCurrencies();
    }

/*
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
*/

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return null;
        }
        String key = cryptoCurrency + "_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
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
                    log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key, result);
                    rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }

        // if unsupported currency is not fine, I doubt unsupported cryptocurrency is either
        if (!fiatCurrenciesAndURLs.containsKey(fiatCurrency)) {
            return null; // unsupported fiat currency
        }

        /*
        This is now allready done when this ratesource is created...
        if (fiatCurrenciesAndURLs.isEmpty()) {
            loadFiatCurrencies();
        }
        */

        /*
        IBitcoinAverage Paths are now fixed. I don't see the need to store fiatCurrenciesAndURLs (or the url part),
        unless we want to check if theres support for the fiatCurrency, which we now do.

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
        */

        BitcoinAverageRate btcRate = api.getBTCRate(fiatCurrency);
        if (btcRate != null) {
            return btcRate.getLast();
        }
        return null;
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
        /*
        no need for this...
        IBitcoinAverage api = RestProxyFactory.createProxy(IBitcoinAverage.class, "https://api.bitcoinaverage.com/ticker/global/");
        HashMap<String, String> currenciesAndURLs = api.getFiatCurrenciesAndURLs();
        if (fiatCurrenciesAndURLs != null) {
            fiatCurrenciesAndURLs = currenciesAndURLs;
        }
        */
        fiatCurrenciesAndURLs = api.getFiatCurrenciesAndURLs();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

}
