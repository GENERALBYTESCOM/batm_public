/*
 *
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
*/
package com.generalbytes.batm.server.extensions.extra.nuls.source.binance;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import com.generalbytes.batm.server.extensions.extra.nuls.NulsConstants;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author naveen
 */
public class BinanceRateSource implements IRateSource {

    private BinanceRateApi api;
    private final String coinMarketCapApiKey;
    private String preferredFiatCurrency = FiatCurrency.USD.getCode();

    public BinanceRateSource(String preferredFiatCurrency, String cmcApiKey) {
        this.coinMarketCapApiKey = cmcApiKey;
        api = RestProxyFactory.createProxy(BinanceRateApi.class, NulsConstants.BINANCE_API_BASE_URL);
        if(FiatCurrency.getCodes().contains(preferredFiatCurrency)){
            this.preferredFiatCurrency = preferredFiatCurrency;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.NULS.getCode());

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.USD.getCode());
        result.add(FiatCurrency.CNY.getCode());
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
        final BinanceTickerData nulsUsdt = api.getTicker(cryptoCurrency +NulsConstants.USDT);
        CoinmarketcapRateSource coinMarketCapSource = new CoinmarketcapRateSource(coinMarketCapApiKey, fiatCurrency);
        BigDecimal lastUsdtFiat = coinMarketCapSource.getExchangeRateLast(NulsConstants.USDT, fiatCurrency);
        if (lastUsdtFiat != null && nulsUsdt.getPrice()!=null) {
            return nulsUsdt.getPrice().multiply(lastUsdtFiat);
        }
        return null;
    }
}
