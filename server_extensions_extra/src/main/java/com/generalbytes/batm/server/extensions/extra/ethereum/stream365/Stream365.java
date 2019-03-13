/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.ethereum.stream365;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;

public class Stream365 implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(Stream365.class);
    private IStream365 api;


    public Stream365() {
        api = RestProxyFactory.createProxy(IStream365.class, "https://api.365.stream");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> currencies = new HashSet<>();
        currencies.add(CryptoCurrency.HBX.getCode());
        currencies.add(CryptoCurrency.VOLTZ.getCode());
        return currencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> currencies = new HashSet<>();
        currencies.add(FiatCurrency.USD.getCode());
        return currencies;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency) || !getFiatCurrencies().contains(fiatCurrency)) {
            log.error("Stream365 ratesource error: unknown crypto-currency or fiat currency.");
            return null;
        }
        String pair = null;
        if (cryptoCurrency.equalsIgnoreCase(CryptoCurrency.VOLTZ.getCode()) && fiatCurrency.equalsIgnoreCase(FiatCurrency.USD.getCode())) {
            pair = "TUSD_VOLTZ";
        }else if (cryptoCurrency.equalsIgnoreCase(CryptoCurrency.HBX.getCode()) && fiatCurrency.equalsIgnoreCase(FiatCurrency.USD.getCode())){
            pair = "TUSD_HBX";
        }
        if (pair != null) {
            MarketData marketData = api.getMarketData(pair);
            if (marketData != null) {
                return marketData.getAverage();
            }
        }
        return null;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return FiatCurrency.USD.getCode();
    }

}
