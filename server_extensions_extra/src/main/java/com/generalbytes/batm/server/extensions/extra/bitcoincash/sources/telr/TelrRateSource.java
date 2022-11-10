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
package com.generalbytes.batm.server.extensions.extra.bitcoincash.sources.telr;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class TelrRateSource implements IRateSourceAdvanced {
    private static final Logger log = LoggerFactory.getLogger(TelrRateSource.class);

    static {
    }

    private final ITelrRateSource api;
    private final String address;
    private final String secret;
    private final String signature;
    private final String preferredFiatCurrency;

    public TelrRateSource(
            String address,
            String secret,
            String signature,
            String preferredFiatCurrency) {
        this.address = address;
        this.secret = secret;
        this.signature = signature;
        this.preferredFiatCurrency = preferredFiatCurrency;

        api = RestProxyFactory.createProxy(ITelrRateSource.class, "https://api.telr.io");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        /* Initialize result. */
        Set<String> result = new HashSet<>();

        /* Add currencies. */
        // NOTE: api endpoint is https://api.telr.io/v1/crypto/list
        result.add(CryptoCurrency.BCH.getCode());
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.DAI.getCode());
        result.add(CryptoCurrency.BET_VERSE.getCode());
        result.add(CryptoCurrency.BET_VERSE_ICO.getCode());
        result.add(CryptoCurrency.BIZZ.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.LTC.getCode());

        /* Return result. */
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        /* Initialize result. */
        Set<String> result = new HashSet<>();

        /* Add currencies. */
        // NOTE: api endpoint is https://api.telr.io/v1/fiat/list
        result.add(FiatCurrency.USD.getCode());

        /* Return result. */
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRateForSell(cryptoCurrency, fiatCurrency);
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        /* Request ticker price. */
        final String tickerPrice = api.getPrice(
                this.address,
                this.secret,
                this.signature,
                cryptoCurrency);

        /* Validate ticker price. */
        if (tickerPrice != null) {
            return new BigDecimal(tickerPrice);
        }

        /* Return null. */
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        /* Request ticker price. */
        final String tickerPrice = api.getPrice(
                this.address,
                this.secret,
                this.signature,
                cryptoCurrency);

        /* Validate ticker price. */
        if (tickerPrice != null) {
            return new BigDecimal(tickerPrice);
        }

        /* Return null. */
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
