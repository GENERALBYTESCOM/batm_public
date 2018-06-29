package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.v2;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class CoinmarketcapRateSourceV2 implements IRateSource {
    /**
     * Expiry of cache in seconds
     */
    private static final long CACHE_EXPIRY_TIME_DEFAULT = 600;

    private static volatile long recentUnix = System.currentTimeMillis();

    private static volatile Map<String,Integer> coinIDs;

    private ICoinmarketcapV2API api;

    public CoinmarketcapRateSourceV2() {
        api = RestProxyFactory.createProxy(ICoinmarketcapV2API.class, "https://api.coinmarketcap.com");
        final long currentUnix = System.currentTimeMillis();
        final long difference = currentUnix - recentUnix;
        final long differenceInSeconds = TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS);
        if(coinIDs == null || coinIDs.isEmpty() || differenceInSeconds > CACHE_EXPIRY_TIME_DEFAULT) {
            HashMap<String, Integer> localCoinIDs = new HashMap<>();
            final Map<String, Object> listings = api.getListings();
            if (listings != null && !listings.isEmpty()) {
                final List<Object> dataList = (List<Object>) listings.get("data");
                if(dataList != null && !dataList.isEmpty()) {
                    for (Object dataobject : dataList) {
                        final Map<String, Object> map = (Map<String, Object>) dataobject;
                        final Integer id = (Integer) map.get("id");
                        final String symbol = (String) map.get("symbol");
                        if (!CoinmarketcapRateSourceV2.coinIDs.containsKey(symbol) && !CoinmarketcapRateSourceV2.coinIDs.containsValue(id)) {
                            CoinmarketcapRateSourceV2.coinIDs.put(symbol, id);
                        }
                    }
                }
            }
            CoinmarketcapRateSourceV2.recentUnix = System.currentTimeMillis();
            CoinmarketcapRateSourceV2.coinIDs = localCoinIDs;
        }
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
        final Set<String> fiatCurrencies = getFiatCurrencies();
        if (!fiatCurrencies.contains(fiatCurrency)) {
            return null;
        }

        final Integer cryptoId = coinIDs.get(cryptoCurrency);
        if(cryptoId == null){
            return null;
        }

        final Map<String, Map<String, Object>> ticker = api.getTicker(cryptoId, fiatCurrency);
        if(ticker == null){
            return null;
        }

        final Map<String, Object> data = ticker.get("data");
        if(data == null){
            return null;
        }

        final Map<String, Object> quotes = (Map<String, Object>) data.get("quotes");
        if(quotes == null){
            return null;
        }

        final Map<String, Object> quote = (Map<String, Object>) quotes.get(fiatCurrency);
        if(quote == null){
            return null;
        }

        final double price = (double) quote.get("price");
        final BigDecimal finalPrice = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return finalPrice;
    }

}
