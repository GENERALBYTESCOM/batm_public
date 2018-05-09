package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity.IBity;
import com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto.Ticker;
import com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto.YobitResponse;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class YobitRateSource implements IRateSource {

    private IYobitAPI api;

    public YobitRateSource() {
        api = RestProxyFactory.createProxy(IBity.class, "https://yobit.net");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.FTO);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.USD);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }

        YobitResponse response = api.getTicker(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase());
        return response.getFot_usd().getLast();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return ICurrencies.USD;
    }
}
