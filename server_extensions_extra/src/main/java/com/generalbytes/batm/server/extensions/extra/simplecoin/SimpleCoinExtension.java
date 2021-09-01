package com.generalbytes.batm.server.extensions.extra.simplecoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.simplecoin.sources.SimpleCoinRateSource;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


public class SimpleCoinExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "SimpleCoin RateSource extension";
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String rsType = st.nextToken();

            if ("simplecoin".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken();
                }
                return new SimpleCoinRateSource(preferredFiatCurrency);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BTC.getCode());
        return result;
    }
}