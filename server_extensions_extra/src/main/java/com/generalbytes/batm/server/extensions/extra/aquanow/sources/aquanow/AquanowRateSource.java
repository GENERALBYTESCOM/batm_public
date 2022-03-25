/* ##
# Part of the Aquanow API price extension
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

package com.generalbytes.batm.server.extensions.extra.aquanow.sources.aquanow;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;
import com.fasterxml.jackson.databind.ObjectMapper;



public class AquanowRateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(AquanowRateSource.class);

    private String preferedFiatCurrency;
    private IAquanowAPI api;

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    public AquanowRateSource(String preferedFiatCurrency) {

        if (!getFiatCurrencies().contains(preferedFiatCurrency)) {
            preferedFiatCurrency = FiatCurrency.USD.getCode();
        }

        this.preferedFiatCurrency = preferedFiatCurrency;

        api = RestProxyFactory.createProxy(IAquanowAPI.class, "https://market-dev.aquanow.io");
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
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.BCH.getCode());        
        // result.add(CryptoCurrency.SMART.getCode());
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        log.debug("Here is cryptocurrency", cryptoCurrency);
        String key = cryptoCurrency +"_" + fiatCurrency;
        log.debug("Here is key", key);

        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called aquanow exchange for rate: " + key + " = " + result);
                rateAmounts.put(key,result);
                rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            } else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                }else{
                    //do the job;
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    log.debug("Called aquanow exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        // if (!CryptoCurrency.BTC.getCode().equalsIgnoreCase(cryptoCurrency)) {
        //     log.debug("code didn't match retuning null ");
        //     return null; //unsupported currency
        // }

        try {
            // String crypto = CRYPTOCURRENCIES.get(cryptoCurrency);
            String crypto = cryptoCurrency;
            String fiat = fiatCurrency;
            String symbol = crypto + "-" + fiat; 
            // APIResponse testResponse = api.getPrice(symbol);
            // String json = "{\"dataType\":\"aggBBO\",\"lastUpdated\":1563493650001,\"symbol\":\"ETH-USD\",\"includeFees\":1,\"bestBid\":228.65391,\"bestAsk\":229.14,\"spread\":\"0.49\"}";
            String json = api.getPrice(symbol);
            ObjectMapper mapper = new ObjectMapper();
            APIResponse response = mapper.readValue(json, APIResponse.class);
            BigDecimal testt = response.getBestAsk();
            // APIResponse.getBestBid();
            // BigDecimal response = api.getPrice(symbol);

            return testt;
            // if (response != null) {
            //     return response.getPrice(symbol);
            // }

            // return api.getPrice(crypto, fiat).get(crypto).get(fiat);
        } catch (HttpStatusIOException e) {
            log.warn(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
        // APIResponse response = api.returnResponse();

        // if (response != null) {
        //     return response.getPrice(fiatCurrency);
        // }

        // return null;
    }

}
