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


package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity.dto.RateInfo;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BityRateSource implements IRateSourceAdvanced{
    private final IBity api;
    public BityRateSource() {
        api = RestProxyFactory.createProxy(IBity.class, "https://bity.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.CHF);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRateForSell(cryptoCurrency,fiatCurrency);
    }

    @Override
    public String getPreferredFiatCurrency() {
        return ICurrencies.CHF;
    }


    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        final RateInfo rate = api.getRateBuy(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.getRate();
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        final RateInfo rate = api.getRateSell(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.getRate();
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForBuy(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.multiply(cryptoAmount);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForSell(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.multiply(cryptoAmount);
        }
        return null;
    }

}
