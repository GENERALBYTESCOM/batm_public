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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseV2ApiWrapper;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBExchangeRatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoinbaseV2RateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger("batm.master.CoinbaseExchange");

    private static final HashMap<String,BigDecimal> rateAmounts = new HashMap<>();
    private static HashMap<String,Long> rateTimes = new HashMap<>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;

    private final String preferredFiatCurrency;
    private final CoinbaseV2ApiWrapper api;

    public CoinbaseV2RateSource(String preferredFiatCurrency, CoinbaseV2ApiWrapper api) {
        if (preferredFiatCurrency == null) {
            preferredFiatCurrency = FiatCurrency.USD.getCode();
        }
        this.preferredFiatCurrency = preferredFiatCurrency;
        this.api = api;

    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.ADA.getCode());
        result.add(CryptoCurrency.BCH.getCode());
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.ETC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.DAI.getCode());
        result.add(CryptoCurrency.BIZZ.getCode());
        result.add(CryptoCurrency.XRP.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.USD.getCode());
        result.add(FiatCurrency.EUR.getCode());
        result.add(FiatCurrency.GBP.getCode());
        result.add(FiatCurrency.AUD.getCode());
        result.add(FiatCurrency.JPY.getCode());
        result.add(FiatCurrency.CNY.getCode());
        result.add(FiatCurrency.CZK.getCode());
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public synchronized BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called coinbase exchange for rate: " + key + " = " + result);
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
                    log.debug("Called coinbase exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String cashCurrency) {
        if (!getFiatCurrencies().contains(cashCurrency)) {
            log.error("Unsupported fiat currency.");
            return null;
        }

        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Unsupported cryptocurrency.");
            return null;
        }

        CBExchangeRatesResponse response = api.getExchangeRates(cryptoCurrency);
        if (response != null && response.getData() != null && response.getData().getCurrency().equalsIgnoreCase(cryptoCurrency)) {
            Map<String, BigDecimal> exchangeRates = response.getData().getRates();
            if (exchangeRates != null) {
                return exchangeRates.get(cashCurrency.toUpperCase());
            }
        }
        return null;
    }

    public CoinbaseV2ApiWrapper getApi() {
        return api;
    }
}
