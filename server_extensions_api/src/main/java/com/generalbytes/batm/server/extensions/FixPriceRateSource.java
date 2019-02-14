package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by b00lean on 7/31/14.
 */
public class FixPriceRateSource implements IRateSource {
    private static final List<String> FIAT_CURRENCIES = FiatCurrency.names();
    private static final List<String> CRYPTO_CURRENCIES = CryptoCurrency.names();
    private final BigDecimal rate;

    private String preferredFiatCurrency = Currencies.USD;

    public FixPriceRateSource(BigDecimal rate, String preferredFiatCurrency) {
        this.rate = rate;

        if (FIAT_CURRENCIES.contains(preferredFiatCurrency.toUpperCase())) {
            this.preferredFiatCurrency = preferredFiatCurrency.toUpperCase();
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
        if (CRYPTO_CURRENCIES.contains(cryptoCurrency.toUpperCase()) && fiatCurrency.equalsIgnoreCase(preferredFiatCurrency)) {
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
        return preferredFiatCurrency;
    }

}
