package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import si.mazi.rescu.RestProxyFactory;
/**
 * Created by kkyovsky on 11/29/17.
 *
 * Modified by sidhujag on 6/3/2018
 */

public class CoinmarketcapRateSource implements IRateSource {
    private ICoinmarketcapAPI api;
    private static HashMap<String,Integer> coinIDs = new HashMap<String, Integer>();
    private String preferredFiatCurrency = Currencies.USD;

    public CoinmarketcapRateSource(String preferedFiatCurrency) {
        this();
        if (Currencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.EUR;
        }
        if (Currencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.USD;
        }
        if (Currencies.CAD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.CAD;
        }
        coinIDs.put(Currencies.BTC, 1);
        coinIDs.put(Currencies.SYS, 541);
        coinIDs.put(Currencies.BCH, 1831);
        coinIDs.put(Currencies.BTX, 1654);
        coinIDs.put(Currencies.LTC, 2);
        coinIDs.put(Currencies.ETH, 1027);
        coinIDs.put(Currencies.DASH, 131);
        coinIDs.put(Currencies.XMR, 328);
        coinIDs.put(Currencies.POT, 122);
        coinIDs.put(Currencies.FLASH, 1755);
        coinIDs.put(Currencies.BTCP, 2575);
    }

    public CoinmarketcapRateSource() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://api.coinmarketcap.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.BTC);
        result.add(Currencies.SYS);
        result.add(Currencies.BCH);
        result.add(Currencies.BTX);
        result.add(Currencies.LTC);
        result.add(Currencies.ETH);
        result.add(Currencies.DASH);
        result.add(Currencies.XMR);
        result.add(Currencies.POT);
        result.add(Currencies.FLASH);
        result.add(Currencies.BTCP);

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        result.add(Currencies.CAD);
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
        Integer cryptoId = coinIDs.get(cryptoCurrency);
        if(cryptoId == null){
			return null;
        }
        Map<String, Object> ticker = api.getTickers(cryptoId, fiatCurrency);
        if(ticker == null){
            return null;
        }
        Map<String, Object> data = (Map<String, Object>) ticker.get("data");
        if(data == null){
            return null;
        }
        Map<String, Object> quotes = (Map<String, Object>) data.get("quotes");
        if(quotes == null){
            return null;
        }
        Map<String, Object> quote = (Map<String, Object>) quotes.get(fiatCurrency);
        if(quote == null){
            return null;
        }
        double price = (double) quote.get("price");
        return new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
}
