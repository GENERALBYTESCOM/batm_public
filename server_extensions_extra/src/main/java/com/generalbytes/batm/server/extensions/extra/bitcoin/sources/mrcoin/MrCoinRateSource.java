/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
 * Copyright (C) 2017 MrCoin Ltd.
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


package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.mrcoin;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.mrcoin.dto.RateInfo;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class MrCoinRateSource implements IRateSourceAdvanced{
    private final IMrCoin api;
    public MrCoinRateSource() {
        api = RestProxyFactory.createProxy(IMrCoin.class, "https://www.mrcoin.eu");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        result.add(ICurrencies.ETH);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.EUR);
        result.add(ICurrencies.HUF);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRateForSell(cryptoCurrency,fiatCurrency);
    }

    @Override
    public String getPreferredFiatCurrency() {
        return ICurrencies.HUF;
    }


    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        final RateInfo rates = api.getAtmTickers();
        if (rates != null) {
            return rates.getRateBuy(cryptoCurrency, fiatCurrency);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        final RateInfo rates = api.getAtmTickers();
        if (rates != null) {
            return rates.getRateSell(cryptoCurrency, fiatCurrency);
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
