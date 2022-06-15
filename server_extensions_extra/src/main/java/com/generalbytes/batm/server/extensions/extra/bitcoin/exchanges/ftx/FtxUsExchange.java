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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ftx;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;

import java.util.HashSet;
import java.util.Set;

public class FtxUsExchange extends FtxExchange {
    private static final String SSL_URI = "https://ftx.us/";

    private static final Set<String> SUPPORTED_FIATS = new HashSet<>();
    private static final Set<String> SUPPORTED_CRYPTOS = new HashSet<>();

    static {
        SUPPORTED_FIATS.add(FiatCurrency.AUD.getCode());
        SUPPORTED_FIATS.add(FiatCurrency.CAD.getCode());
        SUPPORTED_FIATS.add(FiatCurrency.EUR.getCode());
        SUPPORTED_FIATS.add(FiatCurrency.GBP.getCode());
        SUPPORTED_FIATS.add(FiatCurrency.USD.getCode());

        SUPPORTED_CRYPTOS.add(CryptoCurrency.BAT.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.BCH.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.BTC.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.DAI.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.DOGE.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.ETH.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.LTC.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.MKR.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.PAXG.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.SHIB.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.TRX.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.USDT.getCode());
    }

    public FtxUsExchange(String preferredFiatCurrency) {
        super(preferredFiatCurrency, SSL_URI);
    }

    public FtxUsExchange(String key, String secret, String preferredFiatCurrency) {
        super(key, secret, preferredFiatCurrency, SSL_URI);
    }

    @Override
    public Set<String> getFiatCurrencies() {
       return SUPPORTED_FIATS;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
       return SUPPORTED_CRYPTOS;
    }
}
