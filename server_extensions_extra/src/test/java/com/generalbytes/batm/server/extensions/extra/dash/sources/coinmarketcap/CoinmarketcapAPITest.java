package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requires external systems connectivity")
class CoinmarketcapAPITest {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.CoinmarketcapAPITest");
    public static final String API_KEY = "ba025ccf-579b-40e4-be05-cbcebd83c476";
    private static ICoinmarketcapAPI api;

    @BeforeEach
    void setUp() {
        api = RestProxyFactory.createProxy(ICoinmarketcapAPI.class, "https://sandbox-api.coinmarketcap.com");
    }

    @Test
    void jsonTest() throws IOException {
            Map<String, Map<String, Object>> r = RestProxyFactory.createProxy(TestCoinmarketcapAPI.class, "https://sandbox-api.coinmarketcap.com")
                .getTicker(API_KEY, "BTC", "USD");
            assertEquals(2, r.size());
            assertEquals(5, r.get("status").size());
            assertNull(r.get("status").get("error_message"));
            assertEquals(1, r.get("data").size());
            assertNotNull(r.get("data").get("BTC"));
    }

    @Test
    void nullApiKey() throws IOException {
        try {
            api.getTicker(null, "BTC", "USD");
        } catch (HttpStatusIOException e) {
            assertTrue(e.getHttpBody().contains("API key missing"));
            return;
        }
        fail();
    }

    /**
     * Method getTickerOneIntegerOneStringParametersTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    void getTickerOneIntegerOneStringParametersTest() throws IOException {
        CmcTickerResponse result = api.getTicker(API_KEY, "BTC", "USD");
        assertNotNull(result);

        CmcTickerData data = result.getData().get("BTC");
        assertNotNull(data);
    }

    @Test
    void getTickerDogecoinTest() throws IOException {
        CmcTickerResponse result = api.getTicker(API_KEY, "DOGE", "USD");
        assertNotNull(result);

        CmcTickerData data = result.getData().get("DOGE");
        assertNotNull(data);
        assertNotNull(data.getQuote());

        //CmcTickerQuote quote = (CmcTickerQuote) data.getQuotes();
        log.info(data.toString());
        log.info("");
    }

    /**
     * Method getTickerOneIntegerOneStringParametersTest() checks if method getTicker() of the api, with one parameter - id
     * of type string is correctly called and that data is received.
     */
    @Test
    void getTickerOneIntegerOneNullParametersTest() throws IOException {
        CmcTickerResponse result = api.getTicker(API_KEY, "BTC", null);
        assertNotNull(result);

        CmcTickerData data = result.getData().get("BTC");
        assertNotNull(data);
    }

    /**
     * Method getTickerTest() checks if method getTicker() of the api, with two parameters - id of type string,
     * and fiat of type String is correctly called and that data and quotes has been received.
     */
    @Test
    void getQuotesTest() throws IOException {
        CmcTickerResponse result = api.getTicker(API_KEY, "BTC", "EUR");
        assertNotNull(result);

        CmcTickerData data = result.getData().get("BTC");
        assertNotNull(data);

        final Map<String, CmcTickerQuote> quotes = data.getQuote();
        assertNotNull(quotes);

        CmcTickerQuote quote = quotes.get("EUR");
        assertNotNull(quote);

        BigDecimal price = quote.getPrice();
        assertNotNull(price);
        assertTrue(price.doubleValue() != 0);
    }

    /**
     * Method getQuoteByCurrencySymbolAndFiatCurrencyTest() checks the quotes for particular cryptocurrency
     * and for particular fiat currency and makes sure that data is provided.
     */
    @Test
    void getQuoteByCurrencySymbolAndFiatCurrencyTest() throws IOException {
        String currency = "BTC";
        String fiat = "EUR";

        CmcTickerResponse result = api.getTicker(API_KEY, currency, fiat);
        assertNotNull(result);

        CmcTickerData data = result.getData().get(currency);
        assertNotNull(data);

        final Map<String, CmcTickerQuote> quotes = data.getQuote();
        assertNotNull(quotes);

        CmcTickerQuote quote = quotes.get(fiat);
        assertNotNull(quote);

        BigDecimal price = quote.getPrice();
        assertNotNull(price);
        assertTrue(price.doubleValue() != 0);
    }
}
