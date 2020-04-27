/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.worldcoin.sources.cd;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLContext;
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

    private String preferedFiatCurrency = FiatCurrency.USD.getCode();
    private ICryptodiggersRateAPI api;

    public CryptodiggersRateSource(String preferedFiatCurrency) {
        this();
        if (FiatCurrency.EUR.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = FiatCurrency.EUR.getCode();
        }
        if (FiatCurrency.USD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = FiatCurrency.USD.getCode();
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
            log.error("Error", e);
        } catch (KeyManagementException e) {
            log.error("Error", e);
        }

        //api = RestProxyFactory.createProxy(ICryptodiggersRateAPI.class, "https://www.cryptodiggers.eu");
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!(CryptoCurrency.BTC.getCode().equalsIgnoreCase(cryptoCurrency) || CryptoCurrency.WDC.getCode().equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrency) || FiatCurrency.EUR.getCode().equalsIgnoreCase(fiatCurrency))) {
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
        if(FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrency)){
            cd_fiatCurrency="2";
        }
        if(FiatCurrency.EUR.getCode().equalsIgnoreCase(fiatCurrency)){
            cd_fiatCurrency="1";
        }
        if (!(CryptoCurrency.BTC.getCode().equalsIgnoreCase(cryptoCurrency) || CryptoCurrency.WDC.getCode().equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(FiatCurrency.USD.getCode().equalsIgnoreCase(fiatCurrency) || FiatCurrency.EUR.getCode().equalsIgnoreCase(fiatCurrency))) {
            return null;
        }
        CryptodiggersResponse ticker = api.getTicker("7",cd_fiatCurrency);
        if (ticker != null && ticker.geterror() == 0) {
            return ticker.getexch_rate_buy();
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        //result.add(CryptoCurrency.BTC.getCode());
	result.add(CryptoCurrency.WDC.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.USD.getCode());
        result.add(FiatCurrency.EUR.getCode());
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }
}
