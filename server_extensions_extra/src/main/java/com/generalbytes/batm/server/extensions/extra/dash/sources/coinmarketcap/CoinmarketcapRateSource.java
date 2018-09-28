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
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * Created by kkyovsky on 11/29/17.
 *
 * Modified by sidhujag on 6/3/2018
 */

public class CoinmarketcapRateSource implements IRateSource {
    /**
     * Expiry of cache in seconds
     */
    private static final long CACHE_EXPIRY_TIME_DEFAULT = 600;
    private ICoinmarketcapAPI api;
    private String preferredFiatCurrency = Currencies.USD;
    private static volatile long recentUnix = System.currentTimeMillis();
    private static volatile Map<String,Integer> coinIDs;

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
        if (Currencies.HKD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.HKD;
        }
    }

    private CoinmarketcapRateSource() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://api.coinmarketcap.com");
        final long currentUnix = System.currentTimeMillis();
        final long difference = currentUnix - recentUnix;
        final long differenceInSeconds = TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS);
        if(coinIDs == null || coinIDs.isEmpty() || differenceInSeconds > CACHE_EXPIRY_TIME_DEFAULT) {
            HashMap<String, Integer> localCoinIDs = new HashMap<>();
            final Map<String, Object> listings = api.getListings();
            if (listings != null && !listings.isEmpty()) {
                final List<Object> dataList = (List<Object>) listings.get("data");
                if(dataList != null && !dataList.isEmpty()) {
                    for (Object dataobject : dataList) {
                        final Map<String, Object> map = (Map<String, Object>) dataobject;
                        final Integer id = (Integer) map.get("id");
                        final String symbol = (String) map.get("symbol");
                        if (!localCoinIDs.containsKey(symbol) && !localCoinIDs.containsValue(id)) {
                            localCoinIDs.put(symbol, id);
                        }
                    }
                }
            }
            CoinmarketcapRateSource.recentUnix = System.currentTimeMillis();
            CoinmarketcapRateSource.coinIDs = localCoinIDs;
        }
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
        result.add(Currencies.PAC);
        result.add(Currencies.POT);
        result.add(Currencies.FLASH);
        result.add(Currencies.BTCP);
        result.add(Currencies.EFL);
        result.add(Currencies.BSD);
        result.add(Currencies.BTDX);
        result.add(Currencies.MEC);
        result.add(Currencies.BURST);
        result.add(Currencies.DOGE);
        result.add(Currencies.ECA);
        result.add(Currencies.ANON);
        result.add(Currencies.LSK);
        result.add(Currencies.USDT);
        result.add(Currencies.XZC);

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        result.add(Currencies.CAD);
        result.add(Currencies.EUR);
        result.add(Currencies.HKD);
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
        CmcTickerResponse ticker = api.getTicker(cryptoId, fiatCurrency);
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
