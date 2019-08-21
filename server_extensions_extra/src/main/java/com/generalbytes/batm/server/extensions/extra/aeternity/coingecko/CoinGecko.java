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
package com.generalbytes.batm.server.extensions.extra.aeternity.coingecko;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.aeternity.coingecko.constant.Currency;
import com.generalbytes.batm.server.extensions.extra.aeternity.coingecko.impl.CoinGeckoApiClientImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoinGecko implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(CoinGecko.class);
    CoinGeckoApiClient client;

    public CoinGecko() {
        client = new CoinGeckoApiClientImpl();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> currencies = new HashSet<>();
        currencies.add(CryptoCurrency.AE.getCode());
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
        Map<String, Map<String, Double>> result = client.getPrice("aeternity",Currency.USD);
        //System.out.println(result);
        return new BigDecimal(result.get("aeternity").get(Currency.USD));
    }

    @Override
    public String getPreferredFiatCurrency() {
        return FiatCurrency.USD.getCode();
    }

}
