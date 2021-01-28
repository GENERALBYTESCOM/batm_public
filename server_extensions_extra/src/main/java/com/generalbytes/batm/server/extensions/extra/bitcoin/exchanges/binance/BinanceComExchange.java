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

public class BinanceComExchange extends BinanceExchange {
    private static final String SSL_URI = "https://api.binance.com/";

    public BinanceComExchange(String preferredFiatCurrency) {
        super(preferredFiatCurrency, SSL_URI);
    }

    public BinanceComExchange(String key, String secret, String preferredFiatCurrency) {
        super(key, secret, preferredFiatCurrency, SSL_URI);
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.AUD.getCode());
        fiatCurrencies.add(FiatCurrency.EUR.getCode());
        fiatCurrencies.add(FiatCurrency.GBP.getCode());
        fiatCurrencies.add(FiatCurrency.RUB.getCode());
        return fiatCurrencies;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BAT.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.BNB.getCode());
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.CLOAK.getCode());
        cryptoCurrencies.add(CryptoCurrency.DASH.getCode());
        cryptoCurrencies.add(CryptoCurrency.DOGE.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.GRS.getCode());
        cryptoCurrencies.add(CryptoCurrency.KMD.getCode());
        cryptoCurrencies.add(CryptoCurrency.LSK.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.NANO.getCode());
        cryptoCurrencies.add(CryptoCurrency.NULS.getCode());
        cryptoCurrencies.add(CryptoCurrency.REP.getCode());
        cryptoCurrencies.add(CryptoCurrency.SYS.getCode());
        cryptoCurrencies.add(CryptoCurrency.TRX.getCode());
        cryptoCurrencies.add(CryptoCurrency.USDS.getCode());
        cryptoCurrencies.add(CryptoCurrency.USDT.getCode());
        cryptoCurrencies.add(CryptoCurrency.VIA.getCode());
        cryptoCurrencies.add(CryptoCurrency.XMR.getCode());
        cryptoCurrencies.add(CryptoCurrency.XRP.getCode());
        cryptoCurrencies.add(CryptoCurrency.XZC.getCode());
        return cryptoCurrencies;
    }
}
