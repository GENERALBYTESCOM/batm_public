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
package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.coindeal;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import si.mazi.rescu.RestProxyFactory;

public class CoindealRateSource implements IRateSource {

    private ICoindealApi api;

    public CoindealRateSource() {
        api = RestProxyFactory.createProxy(ICoindealApi.class, "https://europe1.coindeal.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.FTO.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.EUR.getCode());
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return isCurrencySupported(cryptoCurrency, fiatCurrency) ? new BigDecimal(api
            .getRates(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase())
            .getLastPrice()) : null;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return FiatCurrency.EUR.getCode();
    }

    private boolean isCurrencySupported(String cryptoCurrency, String fiatCurrency) {
        return getFiatCurrencies().contains(fiatCurrency) && getCryptoCurrencies().contains(cryptoCurrency);
    }
}
