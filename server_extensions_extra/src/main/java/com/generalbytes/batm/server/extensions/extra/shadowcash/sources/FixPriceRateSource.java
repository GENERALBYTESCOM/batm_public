/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 * <p/>
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 * <p/>
 * Contact information
 * -------------------
 * <p/>
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.shadowcash.sources;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FixPriceRateSource implements IRateSource {

    private String preferredFiatCurrency = ICurrencies.USD;
    private Map<String, BigDecimal> currencyRates = new HashMap<>();

    public FixPriceRateSource(BigDecimal rate, String preferredFiatCurrency) {
        this.preferredFiatCurrency = preferredFiatCurrency.toUpperCase();
        if (this.preferredFiatCurrency == null) {
            this.preferredFiatCurrency = ICurrencies.USD;
        }
        this.currencyRates.put(this.preferredFiatCurrency, rate);
    }

    public FixPriceRateSource(Map<String, BigDecimal> currencyRates, String preferredFiatCurrency) {
        this.currencyRates = currencyRates;
        this.preferredFiatCurrency = preferredFiatCurrency;
        if (this.preferredFiatCurrency == null) {
            this.preferredFiatCurrency = ICurrencies.USD;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.SDC);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        fiatCurrency = fiatCurrency.toUpperCase();
        if (ICurrencies.SDC.equalsIgnoreCase(cryptoCurrency)) {
            if( currencyRates.containsKey(fiatCurrency)){
                return currencyRates.get(fiatCurrency);
            }
            return BigDecimal.ZERO;
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return currencyRates.keySet();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }
}
