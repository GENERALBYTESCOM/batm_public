package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.Currencies;
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

    private String preferredFiatCurrency = Currencies.USD;

    public CoinmarketcapRateSource(String preferedFiatCurrency) {
        this();
        if (Currencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.EUR;
        }
        if (Currencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.USD;
        }
    }

    public CoinmarketcapRateSource() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://api.coinmarketcap.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.BTC);
        result.add(Currencies.BCH);
        result.add(Currencies.BTX);
        result.add(Currencies.LTC);
        result.add(Currencies.ETH);
        result.add(Currencies.DASH);
        result.add(Currencies.XMR);
        result.add(Currencies.POT);
        result.add(Currencies.FLASH);

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        result.add(Currencies.EUR);
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return Currencies.USD;
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }
        CMCTicker[] tickers;
        if(Currencies.FLASH.equalsIgnoreCase(cryptoCurrency)){
            tickers = api.getTickers(cryptoCurrency,fiatCurrency);
        }else
            tickers = api.getTickers(fiatCurrency);

        for (int i = 0; i < tickers.length; i++) {
            CMCTicker ticker = tickers[i];
            if (cryptoCurrency.equalsIgnoreCase(ticker.getSymbol())) {
                if (Currencies.EUR.equalsIgnoreCase(fiatCurrency)) {
                    return ticker.getPrice_eur();
                }else{
                    return ticker.getPrice_usd();
                }
            }
        }
        return null;
    }

//    public static void main(String[] args) {
//        CoinmarketcapRateSource rs = new CoinmarketcapRateSource(Currencies.EUR);
//        BigDecimal exchangeRateLast = rs.getExchangeRateLast(Currencies.BTC, Currencies.EUR);
//        System.out.println("exchangeRateLast = " + exchangeRateLast);
//    }
}
