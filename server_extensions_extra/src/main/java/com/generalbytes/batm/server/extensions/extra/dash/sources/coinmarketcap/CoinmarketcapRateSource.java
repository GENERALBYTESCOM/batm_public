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
package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import si.mazi.rescu.RestProxyFactory;
/**
 * Created by kkyovsky on 11/29/17.
 *
 * Modified by sidhujag on 6/3/2018
 */

public class CoinmarketcapRateSource implements IRateSource {
    private ICoinmarketcapAPI api;
    private static HashMap<String,Integer> coinIDs = new HashMap<String, Integer>();
    private String preferredFiatCurrency = Currencies.USD;

    public CoinmarketcapRateSource(String preferedFiatCurrency) {
        this();
        if (Currencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.EUR;
        }
        if (Currencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.USD;
        }
        if (Currencies.CAD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.CAD;
        }
        coinIDs.put(Currencies.BTC, 1);
        coinIDs.put(Currencies.SYS, 541);
        coinIDs.put(Currencies.BCH, 1831);
        coinIDs.put(Currencies.BTX, 1654);
        coinIDs.put(Currencies.LTC, 2);
        coinIDs.put(Currencies.ETH, 1027);
        coinIDs.put(Currencies.DASH, 131);
        coinIDs.put(Currencies.XMR, 328);
        coinIDs.put(Currencies.POT, 122);
        coinIDs.put(Currencies.FLASH, 1755);
        coinIDs.put(Currencies.BTCP, 2575);
        coinIDs.put(Currencies.EFL, 234);
    }

    public CoinmarketcapRateSource() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://api.coinmarketcap.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.BTC);
        result.add(Currencies.SYS);
        result.add(Currencies.BCH);
        result.add(Currencies.BTX);
        result.add(Currencies.LTC);
        result.add(Currencies.ETH);
        result.add(Currencies.DASH);
        result.add(Currencies.XMR);
        result.add(Currencies.POT);
        result.add(Currencies.FLASH);
        result.add(Currencies.BTCP);
        result.add(Currencies.EFL);

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        result.add(Currencies.CAD);
        result.add(Currencies.EUR);
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return Currencies.USD;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }

        Integer cryptoId = coinIDs.get(cryptoCurrency);
        if (cryptoId == null) {
            return null;
        }
        CmcTickerResponse ticker = api.getTickers(cryptoId, fiatCurrency);
        if (ticker == null) {
            return null;
        }
        CmcTickerData data = ticker.getData();
        if (data == null) {
            return null;
        }
        Map<String, CmcTickerQuote> quotesByFiatCurrency = data.getQuotes();
        if (quotesByFiatCurrency == null) {
            return null;
        }
        CmcTickerQuote quote = quotesByFiatCurrency.get(fiatCurrency);
        if (quote == null) {
            return null;
        }
        return quote.getPrice();
    }
}
