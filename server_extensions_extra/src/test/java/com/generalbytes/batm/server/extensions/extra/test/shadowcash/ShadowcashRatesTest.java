package com.generalbytes.batm.server.extensions.extra.test.shadowcash;

import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.yahoo.YahooFinanceRateSource;
import com.generalbytes.batm.server.extensions.extra.shadowcash.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.shadowcash.sources.bittrex.BittrexRateSource;
import com.generalbytes.batm.server.extensions.extra.shadowcash.sources.poloniex.PoloniexRateSource;
import com.generalbytes.batm.server.extensions.extra.test.BaseTest;
import com.generalbytes.batm.server.extensions.extra.test.utils.HttpFetcher;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author ludx
 */
public class ShadowcashRatesTest extends BaseTest {

    private BittrexRateSource bittrexRateSource;
    private PoloniexRateSource poloniexRateSource;
    private YahooFinanceRateSource yahooFinanceRateSource;
    private FixPriceRateSource fixPriceRateSource;

    private WireMockServer wireMockServer;

    private static final String MOCK_API_HOST = "localhost";
    private static final int MOCK_API_PORT = 8888;
    private static final String MOCK_API_BASE_URL = "http://" + MOCK_API_HOST + ":" + MOCK_API_PORT;


    @AfterClass
    public void afterClass() {
        wireMockServer.stop();
    }

    @BeforeClass
    public void beforeClass() {

        WireMockConfiguration config = wireMockConfig().port(MOCK_API_PORT)
                .withRootDirectory("src/test/resources");
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();

        WireMock.configureFor(MOCK_API_HOST, wireMockServer.port());

        // bittrex btc/sdc rate
        stubFor(
                get(urlEqualTo("/api/v1.1/public/getorderbook" +
                        "?market=" + BittrexRateSource.BTC_SDC_MARKET +
                        "&type=" + BittrexRateSource.ORDERBOOK_TYPE +
                        "&depth=" + BittrexRateSource.ORDERBOOK_DEPTH))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/bittrex/getorderbook/sdcbtc-both-50.json")));

        // poloniex btc/sdc rate
        stubFor(
                get(urlEqualTo("/public" +
                        "?command=" + PoloniexRateSource.ORDERBOOK_COMMAND +
                        "&currencyPair=" + PoloniexRateSource.BTC_SDC_MARKET +
                        "&depth=" + PoloniexRateSource.ORDERBOOK_DEPTH))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/poloniex/getorderbook/sdcbtc-50.json")));

        // YahooFinance rates
        stubFor(
                get(urlEqualTo("/d/quotes.csv?e=.csv&f=sl1d1t1&s=BTCRON=X+BTCAUD=X+BTCCHF=X+BTCJPY=X+BTCEUR=X+BTCGBP=X+BTCCZK=X+BTCUSD=X+BTCCAD=X+BTCXAF=X+BTCCNY=X+"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/octet-stream")
                                .withBodyFile("api-mocks/yahoofinance/rates.csv")));


        yahooFinanceRateSource = new YahooFinanceRateSource("USD", MOCK_API_BASE_URL);
        poloniexRateSource = new PoloniexRateSource("USD", MOCK_API_BASE_URL, yahooFinanceRateSource);
        bittrexRateSource = new BittrexRateSource("USD", MOCK_API_BASE_URL, yahooFinanceRateSource);

        Map<String, BigDecimal> currencyRates = new HashMap<>();
        currencyRates.put("USD", new BigDecimal("0.20"));
        currencyRates.put("EUR", new BigDecimal("0.15"));
        fixPriceRateSource = new FixPriceRateSource(currencyRates, "USD");

    }

    @Test(groups = {"shadowcash.bittrex"}, dependsOnGroups = {"shadowcash.wallet"})
    public void mockBittrexOrderbookEndpointTest() throws IOException {
        final String actual = HttpFetcher.fetchAsString(MOCK_API_BASE_URL + "/api/v1.1/public/getorderbook" +
                "?market=" + BittrexRateSource.BTC_SDC_MARKET +
                "&type=" + BittrexRateSource.ORDERBOOK_TYPE +
                "&depth=" + BittrexRateSource.ORDERBOOK_DEPTH);
        final String expected = getFileAsString("__files/api-mocks/bittrex/getorderbook/sdcbtc-both-50.json");
        assertThat(actual, is(expected));
    }

    @Test(groups = {"shadowcash.bittrex"}, dependsOnGroups = {"shadowcash.wallet", "yahoofinance"})
    public void bittrexUSDRateTest() {

        final String cryptoCurrency = "SDC";
        final String fiatCurrency = "USD";
        BigDecimal exchangeRateLast = testRateSourceAndGetLastRate(bittrexRateSource, cryptoCurrency, fiatCurrency);
        assertEquals(exchangeRateLast, new BigDecimal("0.307193400"));
    }

    @Test(groups = {"shadowcash.bittrex"}, dependsOnGroups = {"shadowcash.wallet", "yahoofinance"})
    public void bittrexEURRateTest() {

        final String cryptoCurrency = "SDC";
        final String fiatCurrency = "EUR";
        BigDecimal exchangeRateLast = testRateSourceAndGetLastRate(bittrexRateSource, cryptoCurrency, fiatCurrency);
        assertEquals(exchangeRateLast, new BigDecimal("0.290698334"));
    }

    @Test(groups = {"shadowcash.poloniex"}, dependsOnGroups = {"shadowcash.wallet", "yahoofinance"})
    public void poloniexUSDRateTest() {

        final String cryptoCurrency = "SDC";
        final String fiatCurrency = "USD";
        BigDecimal exchangeRateLast = testRateSourceAndGetLastRate(poloniexRateSource, cryptoCurrency, fiatCurrency);
        assertEquals(exchangeRateLast, new BigDecimal("0.314412444900"));
    }

    @Test(groups = {"shadowcash.poloniex"}, dependsOnGroups = {"shadowcash.wallet", "yahoofinance"})
    public void poloniexEURRateTest() {

        final String cryptoCurrency = "SDC";
        final String fiatCurrency = "EUR";
        BigDecimal exchangeRateLast = testRateSourceAndGetLastRate(poloniexRateSource, cryptoCurrency, fiatCurrency);
        assertEquals(exchangeRateLast, new BigDecimal("0.297529744849"));
    }

    @Test(groups = {"shadowcash.fixed"}, dependsOnGroups = {"shadowcash.wallet", "yahoofinance"})
    public void fixedPriceEURRateTest() {

        final String cryptoCurrency = "SDC";
        final String fiatCurrency = "EUR";
        BigDecimal exchangeRateLast = testRateSourceAndGetLastRate(fixPriceRateSource, cryptoCurrency, fiatCurrency);
        assertTrue(exchangeRateLast.toString().equals("0.15"), "exchange rate doesn't match for " + fiatCurrency + "/" + cryptoCurrency);
    }

    @Test(groups = {"shadowcash.fixed"}, dependsOnGroups = {"shadowcash.wallet", "yahoofinance"})
    public void fixedPriceUSDRateTest() {

        final String cryptoCurrency = "SDC";
        final String fiatCurrency = "USD";
        BigDecimal exchangeRateLast = testRateSourceAndGetLastRate(fixPriceRateSource, cryptoCurrency, fiatCurrency);
        assertTrue(exchangeRateLast.toString().equals("0.20"), "exchange rate doesn't match for " + fiatCurrency + "/" + cryptoCurrency);
    }

    @Test(groups = {"shadowcash.fixed"}, dependsOnGroups = {"shadowcash.wallet"})
    public void bittrexTest() {
        Set<String> fiatCurrencies = bittrexRateSource.getFiatCurrencies();
        Set<String> cryptoCurrencies = bittrexRateSource.getCryptoCurrencies();
        String preferredFiatCurrency = bittrexRateSource.getPreferredFiatCurrency();
        assertEquals(fiatCurrencies.size(), 11);
        assertEquals(cryptoCurrencies.size(), 1);
        assertTrue(preferredFiatCurrency.equalsIgnoreCase("USD"));
    }

    @Test(groups = {"shadowcash.fixed"}, dependsOnGroups = {"shadowcash.wallet"})
    public void poloniexTest() {
        Set<String> fiatCurrencies = poloniexRateSource.getFiatCurrencies();
        Set<String> cryptoCurrencies = poloniexRateSource.getCryptoCurrencies();
        String preferredFiatCurrency = poloniexRateSource.getPreferredFiatCurrency();
        assertEquals(fiatCurrencies.size(), 11);
        assertEquals(cryptoCurrencies.size(), 1);
        assertTrue(preferredFiatCurrency.equalsIgnoreCase("USD"));
    }

}