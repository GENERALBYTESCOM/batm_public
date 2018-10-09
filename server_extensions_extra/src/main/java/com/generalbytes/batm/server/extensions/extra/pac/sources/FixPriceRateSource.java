package com.generalbytes.batm.server.extensions.extra.pac.sources;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class FixPriceRateSource implements IRateSource {
    private BigDecimal rate = BigDecimal.ZERO;

    private String preferedFiatCurrency = Currencies.USD;

    public FixPriceRateSource(BigDecimal rate,String preferedFiatCurrency) {
        this.rate = rate;
        if (Currencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = Currencies.USD;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.PAC);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (Currencies.PAC.equalsIgnoreCase(cryptoCurrency)) {
            return rate;
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        return result;
    }
    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }
}