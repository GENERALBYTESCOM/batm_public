/* ##
# Part of the SmartCash API price extension
#
# Copyright 2018 dustinface
# Created 29.04.2018
#
* This software may be distributed and modified under the terms of the GNU
* General Public License version 2 (GPL2) as published by the Free Software
* Foundation and appearing in the file GPL2.TXT included in the packaging of
* this file. Please note that GPL2 Section 2[b] requires that all works based
* on this software must also be made publicly available under the terms of
* the GPL2 ("Copyleft").
#
## */

package com.generalbytes.batm.server.extensions.extra.betverseico.sources.fixed;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BetVerseICOFixedRateSource implements IRateSourceAdvanced {
    private static final BigDecimal MAX_ALLOWED_PRICE_VALUE = new BigDecimal("9999999999.9999999999");

    private String preferedFiatCurrency = FiatCurrency.USD.getCode();
    private BigDecimal rate = new BigDecimal(1);

    public BetVerseICOFixedRateSource(BigDecimal rate, String preferedFiatCurrency) {
        if (rate != null && preferedFiatCurrency != null) {
            if (rate.compareTo(MAX_ALLOWED_PRICE_VALUE) > 0) {
                this.rate = null;
            }
            this.rate = rate;

            if (!getFiatCurrencies().contains(preferedFiatCurrency)) {
                preferedFiatCurrency = FiatCurrency.USD.getCode();
            }
            this.preferedFiatCurrency = preferedFiatCurrency;
        }
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<String>();
        fiatCurrencies.add(FiatCurrency.USD.getCode());
        fiatCurrencies.add(FiatCurrency.CHF.getCode());
        fiatCurrencies.add(FiatCurrency.EUR.getCode());
        return fiatCurrencies;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return this.preferedFiatCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BETVERSE_ICO.getCode());
        return result;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return this.rate;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        return this.rate;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return cryptoAmount.divide(getExchangeRateForBuy(cryptoCurrency, fiatCurrency));

    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return cryptoAmount.divide(getExchangeRateForSell(cryptoCurrency, fiatCurrency));
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRateForSell(cryptoCurrency, fiatCurrency);
    }
}
