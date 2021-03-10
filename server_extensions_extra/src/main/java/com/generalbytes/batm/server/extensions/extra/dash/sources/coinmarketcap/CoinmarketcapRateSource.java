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
package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
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

public class CoinmarketcapRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(CoinmarketcapRateSource.class);

    private final ICoinmarketcapAPI api;
    private String preferredFiatCurrency = FiatCurrency.USD.getCode();
    private final String apiKey;

    public CoinmarketcapRateSource(String apiKey, String preferedFiatCurrency) {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://pro-api.coinmarketcap.com"); // https://sandbox-api.coinmarketcap.com
        this.apiKey = apiKey;
        if (apiKey == null) {
            log.warn("CoinmarketcapRateSource API key must be configured, see https://coinmarketcap.com/api/");
        }

        if (FiatCurrency.EUR.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.EUR.getCode();
        }
        if (FiatCurrency.USD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.USD.getCode();
        }
        if (FiatCurrency.CAD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.CAD.getCode();
        }
        if (FiatCurrency.HKD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.HKD.getCode();
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.ANON.getCode());
        result.add(CryptoCurrency.ANT.getCode());
        result.add(CryptoCurrency.BAT.getCode());
        result.add(CryptoCurrency.BCH.getCode());
        result.add(CryptoCurrency.BSD.getCode());
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.BTCP.getCode());
        result.add(CryptoCurrency.BTDX.getCode());
        result.add(CryptoCurrency.BTX.getCode());
        result.add(CryptoCurrency.BURST.getCode());
        result.add(CryptoCurrency.CLOAK.getCode());
        result.add(CryptoCurrency.DAI.getCode());
        result.add(CryptoCurrency.BIZZ.getCode());
        result.add(CryptoCurrency.DASH.getCode());
        result.add(CryptoCurrency.DOGE.getCode());
        result.add(CryptoCurrency.ECA.getCode());
        result.add(CryptoCurrency.EFL.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.EURS.getCode());
        result.add(CryptoCurrency.FLASH.getCode());
        result.add(CryptoCurrency.HATCH.getCode());
        result.add(CryptoCurrency.ILC.getCode());
        result.add(CryptoCurrency.LSK.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.MEC.getCode());
        result.add(CryptoCurrency.MKR.getCode());
        result.add(CryptoCurrency.MUE.getCode());
        result.add(CryptoCurrency.NANO.getCode());
        result.add(CryptoCurrency.PAC.getCode());
        result.add(CryptoCurrency.POT.getCode());
        result.add(CryptoCurrency.REP.getCode());
        result.add(CryptoCurrency.SYS.getCode());
        result.add(CryptoCurrency.USDS.getCode());
        result.add(CryptoCurrency.USDT.getCode());
        result.add(CryptoCurrency.XMR.getCode());
        result.add(CryptoCurrency.XRP.getCode());
        result.add(CryptoCurrency.XPM.getCode());
        result.add(CryptoCurrency.XZC.getCode());
        result.add(CryptoCurrency.ZPAE.getCode());

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.CAD.getCode());
        result.add(FiatCurrency.EUR.getCode());
        result.add(FiatCurrency.HKD.getCode());
        result.add(FiatCurrency.USD.getCode());
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
        if(apiKey == null) {
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
