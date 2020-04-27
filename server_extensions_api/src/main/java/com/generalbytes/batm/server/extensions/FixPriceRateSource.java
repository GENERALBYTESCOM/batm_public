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
package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FixPriceRateSource implements IRateSource {

    private static final Set<String> FIAT_CURRENCIES = FiatCurrency.getCodes();
    private static final Set<String> CRYPTO_CURRENCIES = CryptoCurrency.getCodes();
    private final BigDecimal rate;

    private String preferredFiatCurrency = FiatCurrency.USD.getCode();

    public FixPriceRateSource(BigDecimal rate, String preferredFiatCurrency) {
        this.rate = rate;

        if (FIAT_CURRENCIES.contains(preferredFiatCurrency.toUpperCase())) {
            this.preferredFiatCurrency = preferredFiatCurrency.toUpperCase();
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTO_CURRENCIES;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (CRYPTO_CURRENCIES.contains(cryptoCurrency.toUpperCase()) && fiatCurrency.equalsIgnoreCase(preferredFiatCurrency)) {
            return rate;
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }
    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

}
