/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
 * Author   :  pawel.nowacki@teleit.pl / +48.600100825 - wanda.exchange
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bitkub;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bitkub.dto.RateInfo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

public class BitKubRateSource implements IRateSourceAdvanced {

    private static final Logger log = LoggerFactory.getLogger(BitKubRateSource.class);

    private final BitKub api;
    private final String preferredFiatCurrency;

    public BitKubRateSource(String preferredFiatCurrency) {
        api = RestProxyFactory.createProxy(BitKub.class, "https://api.bitkub.com");
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.BCH.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.DOGE.getCode());
        result.add(CryptoCurrency.USDT.getCode());
        result.add(CryptoCurrency.XRP.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.THB.getCode());
        return result;
    }



    @Override
    public String getPreferredFiatCurrency() {
        return this.preferredFiatCurrency;
    }

    private boolean isExchangeRateExist(String cryptoCurrency, String fiatCurrency)
    {
        return !getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency);
    }

    private void setTicker(String cryptoCurrency, String fiatCurrency)
    {
        String crypto = cryptoCurrency.toUpperCase();
        String fiat = fiatCurrency.toUpperCase();
        this.rateInfo = api.getTicker(crypto, fiat);
    }

    private RateInfo rateInfo;


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (isExchangeRateExist(cryptoCurrency, fiatCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }
        try {
            setTicker(cryptoCurrency, fiatCurrency);
            return rateInfo.getLast();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        if (isExchangeRateExist(cryptoCurrency, fiatCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }
        try {
            setTicker(cryptoCurrency, fiatCurrency);
            return rateInfo.getHighestBid();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }
        try {
            setTicker(cryptoCurrency, fiatCurrency);
            return rateInfo.getLowestAsk();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForBuy(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.multiply(cryptoAmount);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForSell(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.multiply(cryptoAmount);
        }
        return null;
    }
}
