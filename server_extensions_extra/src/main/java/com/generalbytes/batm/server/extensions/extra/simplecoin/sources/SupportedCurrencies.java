/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;

import java.util.HashSet;
import java.util.Set;

public class SupportedCurrencies {
    private static Set<String> supportedCryptoCurrency = new HashSet<>();
    private static Set<String> supportedFiatCurrency = new HashSet<>();
    private static String preferredFiatCurrency;
    private static String preferredCryptoCurrency;

    public SupportedCurrencies() {
        supportedCryptoCurrency.add(CryptoCurrency.BTC.getCode());
        supportedCryptoCurrency.add(CryptoCurrency.BCH.getCode());
        supportedCryptoCurrency.add(CryptoCurrency.ETH.getCode());
        supportedCryptoCurrency.add(CryptoCurrency.LTC.getCode());
        supportedCryptoCurrency.add(CryptoCurrency.XRP.getCode());

        supportedFiatCurrency.add(FiatCurrency.CZK.getCode());
        supportedFiatCurrency.add(FiatCurrency.EUR.getCode());
        supportedFiatCurrency.add(FiatCurrency.USD.getCode());

        preferredFiatCurrency = FiatCurrency.CZK.getCode();
        preferredCryptoCurrency = CryptoCurrency.BTC.getCode();
    }

    public Set<String> getSupportedCryptoCurrency() {
        return supportedCryptoCurrency;
    }

    public Set<String> getSupportedFiatCurrency() {
        return supportedFiatCurrency;
    }

    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    public String getPreferredCryptoCurrency() {
        return preferredCryptoCurrency;
    }

    public void setPreferredFiatCurrency(String currency) {
        if (isFiatSupported(currency)) {
            preferredFiatCurrency = currency;
        }
    }

    public void setPreferredCryptoCurrency(String currency) {
        if (isCryptoSupported(currency)) {
            SupportedCurrencies.preferredFiatCurrency = currency;
        }
    }

    public boolean isCryptoSupported(String cryptoCurrency) {
        if (supportedCryptoCurrency.contains(cryptoCurrency)) {
            return true;
        }
        return false;
    }

    public boolean isFiatSupported(String fiatCurrency) {
        if (preferredFiatCurrency.contains(fiatCurrency)) {
            return true;
        }
        return false;
    }
}
