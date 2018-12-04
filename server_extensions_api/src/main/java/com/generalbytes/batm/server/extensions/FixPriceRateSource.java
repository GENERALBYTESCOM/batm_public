package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.generalbytes.batm.server.extensions.Currencies.CRYPTO_CURRENCIES;
import static com.generalbytes.batm.server.extensions.Currencies.FIAT_CURRENCIES;

/**
 * Created by b00lean on 7/31/14.
 */
public class FixPriceRateSource implements IRateSource {
    private BigDecimal rate = BigDecimal.ZERO;

    private String preferedFiatCurrency = Currencies.USD;

    public FixPriceRateSource(BigDecimal rate, String preferedFiatCurrency) {
        this.rate = rate;

        if (FIAT_CURRENCIES.contains(preferedFiatCurrency.toUpperCase())) {
            this.preferedFiatCurrency = preferedFiatCurrency.toUpperCase();
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.addAll(CRYPTO_CURRENCIES);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (CRYPTO_CURRENCIES.contains(cryptoCurrency.toUpperCase()) && fiatCurrency.equals(preferedFiatCurrency)) {
            return rate;
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.addAll(FIAT_CURRENCIES);
        return result;
    }
    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }

}
