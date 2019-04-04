/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.sumcoin.sources.sumcoinindex;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.*;

public class SumcoinindexRateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(SumcoinindexRateSource.class);

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    private String preferedFiatCurrency = FiatCurrency.USD.getCode();
    private ISumcoinindexAPI api;

    public SumcoinindexRateSource(String preferedFiatCurrency) {
        this();
        if (FiatCurrency.EUR.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = FiatCurrency.EUR.getCode();
        }
        if (FiatCurrency.USD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = FiatCurrency.USD.getCode();
        }
    }

    public SumcoinindexRateSource() {
            api = RestProxyFactory.createProxy(ISumcoinindexAPI.class, "https://sumcoinindex.com");
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!(CryptoCurrency.SUM.getCode().equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrency))) {
            return null;
        }

        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {

            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency,fiatCurrency);
                log.debug("Called Sumcoinindex ticker for rate: " + key + " = " + result);
                rateAmounts.put(key,result);
                rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            }else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                }else{
                    //do the job;
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency,fiatCurrency);
                    log.debug("Called Sumcoinindex ticker for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
	//String cd_fiatCurrency;
        /*cd_fiatCurrency="2";
        if(FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrency)){
            cd_fiatCurrency="2";
        } */
        /*if(FiatCurrency.EUR.getCode().equalsIgnoreCase(fiatCurrency)){
            cd_fiatCurrency="1";
        } */
        if (!(CryptoCurrency.SUM.getCode().equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrency))) {
            return null;
        }
        SumcoinindexResponse ticker = api.getTicker();
        if (ticker != null && ticker.getError() == 0) {
            return ticker.getExchRateBuy();
        }
	else{
	    if(ticker.getError()!=0){
		 log.debug("API ticker error: " + ticker.getErrorMsg());
	    }
	}
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        //result.add(CryptoCurrency.BTC.getCode());
	result.add(CryptoCurrency.SUM.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.USD.getCode());
        //result.add(FiatCurrency.EUR.getCode());
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }
}
