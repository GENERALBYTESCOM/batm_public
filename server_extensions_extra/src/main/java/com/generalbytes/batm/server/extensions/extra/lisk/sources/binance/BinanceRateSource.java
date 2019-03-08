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

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;

import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.*;

public class BinanceRateSource implements IRateSource {

    private BinanceAPI api;
    private final String coinmarketcapApiKey;
    private String preferredFiatCurrency = FiatCurrency.USD.getCode();

    public BinanceRateSource(String preferedFiatCurrency, String coinmarketcapApiKey) {
        this.coinmarketcapApiKey = coinmarketcapApiKey;
        api = RestProxyFactory.createProxy(BinanceAPI.class, "https://api.binance.com");
        
        if (FiatCurrency.USD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.USD.getCode();
        }

        if (FiatCurrency.HKD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.HKD.getCode();
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.LSK.getCode());
        result.add(CryptoCurrency.NULS.getCode());

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.USD.getCode());
        result.add(FiatCurrency.HKD.getCode());
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
        final BinanceTickerData btcUsdt = api.getTicker("BTCUSDT");
        final BinanceTickerData lskBtc = api.getTicker(cryptoCurrency + "BTC");
        CoinmarketcapRateSource coinMarketCapSource = new CoinmarketcapRateSource(coinmarketcapApiKey, fiatCurrency);
        BigDecimal lastUsdtFiat = coinMarketCapSource.getExchangeRateLast("USDT", fiatCurrency);
        if (lastUsdtFiat != null && btcUsdt.getPrice()!=null && lskBtc.getPrice() !=null ) {
            BigDecimal lastBtcPriceInUsdt = btcUsdt.getPrice();
            BigDecimal lastLskPriceInBtc = lskBtc.getPrice();
            BigDecimal lastLskPriceInUsdt = lastLskPriceInBtc.multiply(lastBtcPriceInUsdt);
            BigDecimal lastLskPriceInFiat = lastLskPriceInUsdt.multiply(lastUsdtFiat);
            return lastLskPriceInFiat;
        }
        return null;
    }
}
