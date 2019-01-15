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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/**
 * Created by kkyovsky on 11/29/17.
 *
 * Modified by sidhujag on 6/3/2018
 */

public class CoinmarketcapRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(CoinmarketcapRateSource.class);

    private final ICoinmarketcapAPI api;
    private String preferredFiatCurrency = Currencies.USD;
    private final String apiKey;

    public CoinmarketcapRateSource(String apiKey, String preferedFiatCurrency) {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://pro-api.coinmarketcap.com"); // https://sandbox-api.coinmarketcap.com
        this.apiKey = Objects.requireNonNull(apiKey, "CoinmarketcapRateSource API key must be configured, see https://coinmarketcap.com/api/");

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
        result.add(Currencies.CLOAK);
        result.add(Currencies.DAI);

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
        return preferredFiatCurrency;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }

        try {
            CmcTickerResponse ticker = api.getTicker(apiKey, cryptoCurrency, fiatCurrency);
            if (ticker == null) {
                return null;
            }
            CmcTickerData data = ticker.getData().get(cryptoCurrency);
            if (data == null) {
                return null;
            }
            Map<String, CmcTickerQuote> quotesByFiatCurrency = data.getQuote();
            if (quotesByFiatCurrency == null) {
                return null;
            }
            CmcTickerQuote quote = quotesByFiatCurrency.get(fiatCurrency);
            if (quote == null) {
                return null;
            }
            return quote.getPrice();
        } catch (HttpStatusIOException e) {
            log.warn(e.getHttpBody(), e);
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }
}
