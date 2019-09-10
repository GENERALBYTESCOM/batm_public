package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.coindeal;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import si.mazi.rescu.RestProxyFactory;

public class CoindealRateSource implements IRateSource {

    private ICoindealApi api;

    public CoindealRateSource() {
        api = RestProxyFactory.createProxy(ICoindealApi.class, "https://europe1.coindeal.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.FTO.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.EUR.getCode());
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return isCurrencySupported(cryptoCurrency, fiatCurrency) ? new BigDecimal(api
            .getRates(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase())
            .getLastPrice()) : null;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return FiatCurrency.EUR.getCode();
    }

    private boolean isCurrencySupported(String cryptoCurrency, String fiatCurrency) {
        return getFiatCurrencies().contains(fiatCurrency) && getCryptoCurrencies().contains(cryptoCurrency);
    }
}
