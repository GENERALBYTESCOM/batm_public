package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.Map;

public class CoinmarketcapAPITest {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.CoinmarketcapAPITest");
    public static final String API_KEY = "ba025ccf-579b-40e4-be05-cbcebd83c476";
    private static ICoinmarketcapAPI api;

    @BeforeClass
    public static  void setup() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://sandbox-api.coinmarketcap.com");
    }

    /**
     * Method getTickerOneIntegerOneStringParametersTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerOneIntegerOneStringParametersTest() {
        CmcTickerResponse result = api.getTicker(API_KEY, "BTC", "USD");
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData().get("BTC");
        Assert.assertNotNull(data);
    }

    @Test
    public void getTickerDogecoinTest() {
        CmcTickerResponse result = api.getTicker(API_KEY, "DOGE", "USD");
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData().get("DOGE");
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getQuote());

        //CmcTickerQuote quote = (CmcTickerQuote) data.getQuotes();
        log.info(data.toString());
        log.info("");
    }

    /**
     * Method getTickerOneIntegerOneStringParametersTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    public void getTickerOneIntegerOneNullParametersTest() {
        CmcTickerResponse result = api.getTicker(API_KEY, "BTC", null);
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData().get("BTC");
        Assert.assertNotNull(data);
    }

    /**
     * Method getTickerTest() checks if method getTicker() of the api, with two parameters - id of type string,
     * and fiat of type String is correctly called and that data and quotes has been received.
     */
    @Test
    public void getQuotesTest() {
        CmcTickerResponse result = api.getTicker(API_KEY, "BTC", "EUR");
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData().get("BTC");
        Assert.assertNotNull(data);

        final Map<String, CmcTickerQuote> quotes = data.getQuote();
        Assert.assertNotNull(quotes);

        CmcTickerQuote quote = quotes.get("EUR");
        Assert.assertNotNull(quote);

        BigDecimal price = quote.getPrice();
        Assert.assertNotNull(price);
        Assert.assertTrue(price.doubleValue() != 0);
    }

    /**
     * Method getQuoteByCurrencySymbolAndFiatCurrencyTest() checks the quotes for particular crypto currency
     * and for particular fiat currency and makes sure that data is provided.
     */
    @Test
    public void getQuoteByCurrencySymbolAndFiatCurrencyTest() {
        String currency = "BTC";
        String fiat = "EUR";

        CmcTickerResponse result = api.getTicker(API_KEY, currency, fiat);
        Assert.assertNotNull(result);

        CmcTickerData data = result.getData().get(currency);
        Assert.assertNotNull(data);

        final Map<String, CmcTickerQuote> quotes = data.getQuote();
        Assert.assertNotNull(quotes);

        CmcTickerQuote quote = quotes.get(fiat);
        Assert.assertNotNull(quote);

        BigDecimal price = quote.getPrice();
        Assert.assertNotNull(price);
        Assert.assertTrue(price.doubleValue() != 0);
    }
}
