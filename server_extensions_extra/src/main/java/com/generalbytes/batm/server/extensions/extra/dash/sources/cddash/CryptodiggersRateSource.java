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
package com.generalbytes.batm.server.extensions.extra.dash.sources.cddash;

import com.generalbytes.batm.server.extensions.extra.dash.sources.cddash.*;
import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.ClientConfig;

import java.math.BigDecimal;
import java.util.*;

public class CryptodiggersRateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(CryptodiggersRateSource.class);

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    private String preferedFiatCurrency = ICurrencies.USD;
    private ICryptodiggersRateAPI api;

    public CryptodiggersRateSource(String preferedFiatCurrency) {
        this();
        if (ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.EUR;
        }
        if (ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.USD;
        }
    }

    public CryptodiggersRateSource() {
	final ClientConfig config = new ClientConfig();
        try {
            SSLContext sslcontext=SSLContext.getInstance("TLS");
            sslcontext.init(null,null,null);
            final CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
            api = RestProxyFactory.createProxy(ICryptodiggersRateAPI.class, "https://www.cryptodiggers.eu", config);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!(ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency) || ICurrencies.DASH.equalsIgnoreCase(cryptoCurrency))) {
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
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency,fiatCurrency);
                log.debug("Called Cryptodiggers ticker for rate: " + key + " = " + result);
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
                    log.debug("Called Cryptodiggers ticker for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
	String cd_fiatCurrency;
        cd_fiatCurrency="2";
        if(ICurrencies.USD.equalsIgnoreCase(fiatCurrency)){
            cd_fiatCurrency="2";
        }
        if(ICurrencies.EUR.equalsIgnoreCase(fiatCurrency)){
            cd_fiatCurrency="1";
        }
        if (!(ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency) || ICurrencies.DASH.equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(ICurrencies.USD.equalsIgnoreCase(fiatCurrency) || ICurrencies.EUR.equalsIgnoreCase(fiatCurrency))) {
            return null;
        }
        CryptodiggersResponse ticker = api.getTicker("19",cd_fiatCurrency);
        if (ticker != null && ticker.geterror() == 0) {
            return ticker.getexch_rate_buy();
        }
	else{
	    if(ticker.geterror()!=0){
		 log.debug("API ticker error: " + ticker.geterror_msg());
	    }
	}
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        //result.add(ICurrencies.BTC);
	result.add(ICurrencies.DASH);
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
        return preferedFiatCurrency;
    }
}
