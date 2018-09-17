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
package com.generalbytes.batm.server.extensions.extra.lisk.sources;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by b00lean on 7/31/14.
 */
public class FixPriceRateSource implements IRateSource {
    private BigDecimal rate = BigDecimal.ZERO;

    private String preferedFiatCurrency = Currencies.USD;

    public FixPriceRateSource(BigDecimal rate,String preferedFiatCurrency) {
        this.rate = rate;
        if (Currencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = Currencies.USD;
        }
        if (Currencies.HKD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = Currencies.HKD;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.LSK);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (Currencies.LSK.equalsIgnoreCase(cryptoCurrency)) {
            return rate;
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD); 
        result.add(Currencies.HKD);
        return result;
    }
    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }
}
