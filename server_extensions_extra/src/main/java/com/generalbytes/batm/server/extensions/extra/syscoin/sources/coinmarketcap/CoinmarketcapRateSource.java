package com.generalbytes.batm.server.extensions.extra.syscoin.sources.coinmarketcap;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;

/**
 * Created by kkyovsky on 11/29/17.
 */

public class CoinmarketcapRateSource implements IRateSource {
    private ICoinmarketcapAPI api;

    private String preferredFiatCurrency = ICurrencies.USD;

    public CoinmarketcapRateSource(String preferedFiatCurrency) {
        this();
        if (ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = ICurrencies.EUR;
        }
        if (ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = ICurrencies.USD;
        }
		if (ICurrencies.CAD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = ICurrencies.CAD;
        }
    }

    public CoinmarketcapRateSource() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://api.coinmarketcap.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
		result.add(ICurrencies.SYS);
        result.add(ICurrencies.BTC);
        result.add(ICurrencies.BCH);
        result.add(ICurrencies.LTC);
        result.add(ICurrencies.ETH);
        result.add(ICurrencies.DASH);
        result.add(ICurrencies.XMR);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
		result.add(ICurrencies.CAD);
        result.add(ICurrencies.USD);
        result.add(ICurrencies.EUR);
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return ICurrencies.USD;
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }
        CMCTicker[] tickers = api.getTickers(fiatCurrency);
        for (int i = 0; i < tickers.length; i++) {
            CMCTicker ticker = tickers[i];
            if (cryptoCurrency.equalsIgnoreCase(ticker.getSymbol())) {
                if (ICurrencies.EUR.equalsIgnoreCase(fiatCurrency)) {
                    return ticker.getPrice_eur();
                }else if (ICurrencies.USD.equalsIgnoreCase(fiatCurrency)) {
                    return ticker.getPrice_usd();
                }else if (ICurrencies.CAD.equalsIgnoreCase(fiatCurrency)) {
                    return ticker.getPrice_cad();
                }
            }
        }
        return null;
    }

//    public static void main(String[] args) {
//        CoinmarketcapRateSource rs = new CoinmarketcapRateSource(ICurrencies.EUR);
//        BigDecimal exchangeRateLast = rs.getExchangeRateLast(ICurrencies.BTC, ICurrencies.EUR);
//        System.out.println("exchangeRateLast = " + exchangeRateLast);
//    }
}
