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
package com.generalbytes.batm.server.extensions.extra.ilcoin.sources.coinmarketinfo;

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

public class CoinmarketinfoRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(CoinmarketinfoRateSource.class);

    private final ICoinmarketinfoAPI api;
    private String preferredFiatCurrency = FiatCurrency.USD.getCode();

    public CoinmarketinfoRateSource(String preferedFiatCurrency) {
        api = RestProxyFactory.createProxy(ICoinmarketinfoAPI.class, "https://api.coinmarketinfo.com:9445");

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
        result.add(CryptoCurrency.ILC.getCode());
        result.add(CryptoCurrency.WILC.getCode());

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
        try {
            return api.getMarket(cryptoCurrency + "-" + fiatCurrency).data.price;
        } catch (HttpStatusIOException e) {
            log.warn(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
