package com.generalbytes.batm.server.extensions.extra.potcoin.sources.coinmarketcap;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class CoinmarketcapRateSource implements IRateSource {

    private String preferedFiatCurrency = ICurrencies.USD;
    private ICoinmarketcapRateAPI api;

    /**
     * Constructor
     */
    public CoinmarketcapRateSource(String preferedFiatCurrency) {
        if (ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.USD;
        }
        if (ICurrencies.CAD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.CAD;
        }
        if (ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.EUR;
        }
        api = RestProxyFactory.createProxy(ICoinmarketcapRateAPI.class, "https://api.coinmarketcap.com");
    }

    /**
     * This method returns list of supported crypto currencies
     * @return
     */
    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.POT);
        //result.add(ICurrencies.BTC);
        return result;
    }

    /**
     * Returns current price of cryptocurrency in specified fiat currency
     * @param cryptoCurrency
     * @param fiatCurrency
     * @return
     */
    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        // coinmarketcap uses coin name instead coin symbol
        if (ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            cryptoCurrency = "bitcoin";
        } else if (ICurrencies.POT.equalsIgnoreCase(cryptoCurrency)) {
            cryptoCurrency = "potcoin";
        }

        CoinmarketcapResponse[] response = api.getRequest(cryptoCurrency, fiatCurrency);

        if (response != null) {
            if (ICurrencies.USD.equalsIgnoreCase(fiatCurrency)) {
                BigDecimal rate = response[0].getPrice_usd();
                return rate;
            } else if (ICurrencies.CAD.equalsIgnoreCase(fiatCurrency)) {
                BigDecimal rate = response[0].getPrice_cad();
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

    /**
     * This method returns list of supported fiat currencies
     * @return
     */
    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.USD);
        result.add(ICurrencies.CAD);
        result.add(ICurrencies.EUR);
        return result;
    }

    /**
     * Returns fiat currency that is used for actual purchases of cryptocurrency by server
     * @return
     */
    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }

}
