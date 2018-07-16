package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;

public class YobitRateSource implements IRateSourceAdvanced {

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
        return isCurrencySupported(cryptoCurrency, fiatCurrency) ? api
            .getTicker(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase())
            .getFto_usd()
            .getLast() : null;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return Currencies.USD;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return isCurrencySupported(cryptoCurrency, fiatCurrency) ? api
            .getTicker(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase())
            .getFto_usd()
            .getBuy() : null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        return isCurrencySupported(cryptoCurrency, fiatCurrency) ? api
            .getTicker(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase())
            .getFto_usd()
            .getSell() : null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal exchangeRateForBuy = getExchangeRateForBuy(cryptoCurrency, fiatCurrency);

        if (exchangeRateForBuy != null) {
            return exchangeRateForBuy.multiply(cryptoAmount);
        }

        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal exchangeRateForSell = getExchangeRateForSell(cryptoCurrency, fiatCurrency);

        if (exchangeRateForSell != null) {
            return exchangeRateForSell.multiply(cryptoAmount);
        }

        return null;
    }

    private boolean isCurrencySupported(String cryptoCurrency, String fiatCurrency) {
        return getFiatCurrencies().contains(fiatCurrency) && getCryptoCurrencies().contains(cryptoCurrency);
    }
}
