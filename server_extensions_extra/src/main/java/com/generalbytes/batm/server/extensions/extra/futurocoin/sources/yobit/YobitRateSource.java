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
package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto.Ticker;
import si.mazi.rescu.RestProxyFactory;

public class YobitRateSource implements IRateSourceAdvanced {

    private IYobitAPI api;

    public YobitRateSource() {
        api = RestProxyFactory.createProxy(IYobitAPI.class, "https://yobit.net");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.FTO.getCode());
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.MAX.getCode());
        result.add(CryptoCurrency.DASH.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.LSK.getCode());
        result.add(CryptoCurrency.DOGE.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.USD.getCode());
        return result;
    }

    private Ticker getTicker(String cryptoCurrency, String fiatCurrency) {
        if (!isCurrencySupported(cryptoCurrency, fiatCurrency)) {
            return null;
        }
        return api
            .getTicker(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase())
            .get(cryptoCurrency.toLowerCase() + "_" + fiatCurrency.toLowerCase());
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        Ticker ticker = getTicker(cryptoCurrency, fiatCurrency);
        return ticker == null ? null : ticker.getLast();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return FiatCurrency.USD.getCode();
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        Ticker ticker = getTicker(cryptoCurrency, fiatCurrency);
        return ticker == null ? null : ticker.getSell(); //customer buy, exchange sell
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        Ticker ticker = getTicker(cryptoCurrency, fiatCurrency);
        return ticker == null ? null : ticker.getBuy(); //customer sell, exchange buy
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal exchangeRateForBuy = getExchangeRateForBuy(cryptoCurrency, fiatCurrency);

        if (exchangeRateForBuy != null) {
            return exchangeRateForBuy.multiply(cryptoAmount);
        }

        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal exchangeRateForSell = getExchangeRateForSell(cryptoCurrency, fiatCurrency);

        if (exchangeRateForSell != null) {
            return exchangeRateForSell.multiply(cryptoAmount);
        }

        return null;
    }

    private boolean isCurrencySupported(String cryptoCurrency, String fiatCurrency) {
        return getFiatCurrencies().contains(fiatCurrency) && getCryptoCurrencies().contains(cryptoCurrency);
    }
}
