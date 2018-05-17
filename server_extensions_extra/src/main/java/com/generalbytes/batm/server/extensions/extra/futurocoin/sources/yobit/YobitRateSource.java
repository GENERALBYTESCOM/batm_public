package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto.YobitResponse;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;

public class YobitRateSource implements IRateSource {

    private IYobitAPI api;

    public YobitRateSource() {
        api = RestProxyFactory.createProxy(IYobitAPI.class, "https://yobit.net");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.FTO);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }

        YobitResponse response = api.getTicker(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase());
        return response.getFto_usd().getLast();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return Currencies.USD;
    }
}
