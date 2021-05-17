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
package com.generalbytes.batm.server.extensions.extra.ilcoin.sources.nomics;

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
import java.util.Objects;
import java.util.Set;
import java.util.List;

public class NomicsRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(NomicsRateSource.class);

    private final INomicsAPI api;
    private final String apiKey;
    private String preferredFiatCurrency = FiatCurrency.USD.getCode();

    public NomicsRateSource(String apiKey, String preferedFiatCurrency) {
        api = RestProxyFactory.createProxy(INomicsAPI.class, "https://api.nomics.com");
        this.apiKey = apiKey;
        this.preferredFiatCurrency = preferedFiatCurrency;

        if (apiKey == null) {
            log.warn("NomicsRateSource API key must be configured, see https://nomics.com/docs/");
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
        result.add(FiatCurrency.USD.getCode());
        result.add(FiatCurrency.AED.getCode());
        result.add(FiatCurrency.AUD.getCode());
        result.add(FiatCurrency.BHD.getCode());
        result.add(FiatCurrency.BRL.getCode());
        result.add(FiatCurrency.CAD.getCode());
        result.add(FiatCurrency.CHF.getCode());
        result.add(FiatCurrency.CNY.getCode());
        result.add(FiatCurrency.CZK.getCode());
        result.add(FiatCurrency.DKK.getCode());
        result.add(FiatCurrency.EUR.getCode());
        result.add(FiatCurrency.GBP.getCode());
        result.add(FiatCurrency.HKD.getCode());
        result.add(FiatCurrency.HUF.getCode());
        result.add(FiatCurrency.ILS.getCode());
        result.add(FiatCurrency.INR.getCode());
        result.add(FiatCurrency.JPY.getCode());
        result.add(FiatCurrency.KRW.getCode());
        result.add(FiatCurrency.KWD.getCode());
        result.add(FiatCurrency.MXN.getCode());
        result.add(FiatCurrency.MYR.getCode());
        result.add(FiatCurrency.NOK.getCode());
        result.add(FiatCurrency.NZD.getCode());
        result.add(FiatCurrency.PHP.getCode());
        result.add(FiatCurrency.PLN.getCode());
        result.add(FiatCurrency.RUB.getCode());
        result.add(FiatCurrency.SAR.getCode());
        result.add(FiatCurrency.SGD.getCode());
        result.add(FiatCurrency.THB.getCode());
        result.add(FiatCurrency.TRY.getCode());
        result.add(FiatCurrency.TWD.getCode());
        result.add(FiatCurrency.UAH.getCode());
        result.add(FiatCurrency.VND.getCode());
        result.add(FiatCurrency.ZAR.getCode());
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }

        try {
            List<NomicsTickerResponse> tickerList = api.getTicker(this.apiKey, cryptoCurrency, fiatCurrency);
            if (tickerList.size() == 1) {
                return tickerList.get(0).price;
            }
            return null;
        } catch (HttpStatusIOException e) { 
            log.warn(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
