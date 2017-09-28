package com.generalbytes.batm.server.extensions.extra.decent.sources.coinmarketcap;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class CoinmarketcapRateSource implements IRateSource {

    private String preferedFiatCurrency = ICurrencies.EUR;
    private ICoinmarketcapRateAPI api;

    public CoinmarketcapRateSource(String preferedFiatCurrency) {
        if (ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.USD;
        }
        if (ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.EUR;
        }
        api = RestProxyFactory.createProxy(ICoinmarketcapRateAPI.class, "https://api.coinmarketcap.com");
    }


    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.DCT);
        //result.add(ICurrencies.BTC);
        return result;
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        // coinmarketcap uses coin name instead coin symbol
        if (ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            cryptoCurrency = "bitcoin";
        } else if (ICurrencies.DCT.equalsIgnoreCase(cryptoCurrency)) {
            cryptoCurrency = "decent";
        }

        CoinmarketcapResponse[] response = api.getRequest(cryptoCurrency, fiatCurrency);

        if (response != null) {
            if (ICurrencies.USD.equalsIgnoreCase(fiatCurrency)) {
                BigDecimal rate = response[0].getPrice_usd();
                return rate;
            } else if (ICurrencies.EUR.equalsIgnoreCase(fiatCurrency)) {
                BigDecimal rate = response[0].getPrice_eur();
                return rate;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.USD);
        result.add(ICurrencies.EUR);
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }

}