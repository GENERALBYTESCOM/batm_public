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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BinanceUsExchange extends BinanceExchange {
    private static final String SSL_URI = "https://api.binance.us/";

    private static final Set<String> SUPPORTED_FIATS = new HashSet<>();
    private static final Set<SupportedCryptoCurrency> SUPPORTED_CRYPTOS = new HashSet<>();

    static {
        SUPPORTED_FIATS.add(FiatCurrency.USD.getCode());
        SUPPORTED_FIATS.add(CryptoCurrency.BUSD.getCode());
        SUPPORTED_FIATS.add(CryptoCurrency.USDC.getCode());
        SUPPORTED_FIATS.add(CryptoCurrency.USDT.getCode());

        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.BAT.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.BCH.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.BNB.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.BTC.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.DASH.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.DOGE.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.ETH.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.LTC.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.NANO.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.PAXG.getCode()));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.USDT.getCode(), new BigDecimal("0.000001")));
        SUPPORTED_CRYPTOS.add(new SupportedCryptoCurrency(CryptoCurrency.XRP.getCode(), new BigDecimal("0.000001")));
    }

    public BinanceUsExchange(String preferredFiatCurrency) {
        super(preferredFiatCurrency, SSL_URI);
    }

    public BinanceUsExchange(String key, String secret, String preferredFiatCurrency) {
        super(key, secret, preferredFiatCurrency, SSL_URI);
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return SUPPORTED_FIATS;
    }

    @Override
    public Set<SupportedCryptoCurrency> getSupportedCryptoCurrencies() {
        return SUPPORTED_CRYPTOS;
    }
}
