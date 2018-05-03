/* ##
# Part of the SmartCash API price extension
#
# Copyright 2018 dustinface
# Created 29.04.2018
#
* This software may be distributed and modified under the terms of the GNU
* General Public License version 2 (GPL2) as published by the Free Software
* Foundation and appearing in the file GPL2.TXT included in the packaging of
* this file. Please note that GPL2 Section 2[b] requires that all works based
* on this software must also be made publicly available under the terms of
* the GPL2 ("Copyleft").
#
## */

package com.generalbytes.batm.server.extensions.extra.smartcash.sources.smartcash;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.serialization.jackson.DefaultJacksonObjectMapperFactory;
import si.mazi.rescu.serialization.jackson.JacksonObjectMapperFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;

public class SmartCashRateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(SmartCashRateSource.class);

    private String preferedFiatCurrency;
    private ISmartCashAPI api;

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    public SmartCashRateSource(String preferedFiatCurrency) {

        if (!getFiatCurrencies().contains(preferedFiatCurrency)) {
            preferedFiatCurrency = Currencies.USD;
        }

        this.preferedFiatCurrency = preferedFiatCurrency;

        api = RestProxyFactory.createProxy(ISmartCashAPI.class, "https://api.smartcash.cc");
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<String>();

        Field[] fields = APIResponse.Currency.class.getFields();

        for (Field f : fields) {
            fiatCurrencies.add(f.getName());
        }

        return fiatCurrencies;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return this.preferedFiatCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.SMART);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {

        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called smartcash exchange for rate: " + key + " = " + result);
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
                    log.debug("Called smartcash exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!Currencies.SMART.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }
        APIResponse response = api.returnResponse();

        if (response != null) {
            return response.getPrice(fiatCurrency);
        }

        return null;
    }

}
