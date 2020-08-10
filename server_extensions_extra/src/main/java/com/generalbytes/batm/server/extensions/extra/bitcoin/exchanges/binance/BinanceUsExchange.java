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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;

import java.util.HashSet;
import java.util.Set;

public class BinanceUsExchange extends BinanceExchange {
    private static final String SSL_URI = "https://api.binance.us/";

    public BinanceUsExchange(String preferredFiatCurrency) {
        super(preferredFiatCurrency, SSL_URI);
    }

    public BinanceUsExchange(String key, String secret, String preferredFiatCurrency) {
        super(key, secret, preferredFiatCurrency, SSL_URI);
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.USD.getCode());
        return fiatCurrencies;
    }


    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BAT.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.BNB.getCode());
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.DASH.getCode());
        cryptoCurrencies.add(CryptoCurrency.DOGE.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.USDT.getCode());
        cryptoCurrencies.add(CryptoCurrency.XRP.getCode());
        return cryptoCurrencies;
    }
}
