package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto.Ticker;
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
        result.add(Currencies.BTC);
        result.add(Currencies.LTC);
        result.add(Currencies.MAX);
        result.add(Currencies.DASH);
        result.add(Currencies.ETH);
        result.add(Currencies.LSK);
        result.add(Currencies.DOGE);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        return result;
    }

    private Ticker getTicker(String cryptoCurrency, String fiatCurrency) {
        if (!isCurrencySupported(cryptoCurrency, fiatCurrency)) {
            return null;
        }
        return api
            .getTicker(cryptoCurrency.toLowerCase(), fiatCurrency.toLowerCase())
            .get(cryptoCurrency.toLowerCase() + "_" + fiatCurrency.toLowerCase());
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        Ticker ticker = getTicker(cryptoCurrency, fiatCurrency);
        return ticker == null ? null : ticker.getLast();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return Currencies.USD;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        Ticker ticker = getTicker(cryptoCurrency, fiatCurrency);
        return ticker == null ? null : ticker.getSell(); //customer buy, exchange sell
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        Ticker ticker = getTicker(cryptoCurrency, fiatCurrency);
        return ticker == null ? null : ticker.getBuy(); //customer sell, exchange buy
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
