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
package com.generalbytes.batm.server.extensions.extra.lisk.sources.binance;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet.Lskwallet;

import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.*;

import java.util.List;
/**
 * Created by kkyovsky on 11/29/17.
 *
 * Modified by kelvin on 8/20/2018
 */

public class BinanceRateSource implements IRateSource {
    /**
     * Expiry of cache in seconds
     */
    private BinanceAPI api;
    private TetherPriceAPI apiUsdt;
    private String preferredFiatCurrency = Currencies.USD;

    public BinanceRateSource(String preferedFiatCurrency) {
        api = RestProxyFactory.createProxy(BinanceAPI.class, "https://api.binance.com");
        apiUsdt = RestProxyFactory.createProxy(TetherPriceAPI.class, "https://api.coingecko.com");

        if (Currencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.USD;
        }

        if (Currencies.HKD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.HKD;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.BTC);
        result.add(Currencies.BCH);
        result.add(Currencies.LTC);
        result.add(Currencies.ETH);
        result.add(Currencies.DASH);
        result.add(Currencies.XMR);
        result.add(Currencies.LSK);

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        result.add(Currencies.HKD);
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return this.preferredFiatCurrency;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }
        final BinanceTickerData BtcUsdt = api.getTicker("BTCUSDT");
        final BinanceTickerData lskBtc = api.getTicker(cryptoCurrency + "BTC");
        final List<Object> UsdtFiat = apiUsdt.getTetherPrice(fiatCurrency.toLowerCase(), "tether");

        if (UsdtFiat != null && BtcUsdt.getPrice()!=null && lskBtc.getPrice() !=null ) {
            final Map<String, Object> UsdtFiatJson = (Map<String, Object>) UsdtFiat.get(0);
            final Double lastUsdtFiat = (Double) UsdtFiatJson.get("current_price");

            BigDecimal lastUsdtFiatBig = BigDecimal.valueOf(lastUsdtFiat);
            BigDecimal lastBtcPriceInUsdt = BtcUsdt.getPrice();
            BigDecimal lastLskPriceInBtc = lskBtc.getPrice();
            BigDecimal lastLskPriceInUsdt = lastLskPriceInBtc.multiply(lastBtcPriceInUsdt);
            BigDecimal lastLskPriceInFiat = lastLskPriceInUsdt.multiply(lastUsdtFiatBig);

            return lastLskPriceInFiat;
        }
        return null;
    }
}