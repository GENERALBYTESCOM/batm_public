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


package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity.dto.RateInfo;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BityRateSource implements IRateSourceAdvanced{
    private final IBity api;
    private final String preferredFiatCurrency;

    public BityRateSource(String preferredFiatCurrency) {
        this.preferredFiatCurrency = preferredFiatCurrency.toUpperCase();
        api = RestProxyFactory.createProxy(IBity.class, "https://bity.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.CHF.getCode());
        result.add(FiatCurrency.EUR.getCode());
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRateForSell(cryptoCurrency,fiatCurrency);
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
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
