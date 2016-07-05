package com.generalbytes.batm.server.extensions.extra.test;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.BitcoinAverageRateSource;
import com.generalbytes.batm.server.extensions.extra.test.utils.HttpFetcher;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author ludx
 */
public class BitcoinAverageRatesTest extends BaseTest {

    private BitcoinAverageRateSource bitcoinAverageRateSource;

    private HttpFetcher httpFetcher = new HttpFetcher();
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

        WireMockConfiguration config = wireMockConfig().port(MOCK_API_PORT);
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();

        WireMock.configureFor(MOCK_API_HOST, wireMockServer.port());

        // btcaverage all currencies
        stubFor(
                get(urlEqualTo("/ticker/global"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("api-mocks/bitcoinaverage/currencies.json")));

        // btcaverage usd/btc rate
        stubFor(
                get(urlEqualTo("/ticker/global/USD"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("api-mocks/bitcoinaverage/usd.json")));

        // btcaverage eur/btc rate
        stubFor(
                get(urlEqualTo("/ticker/global/EUR"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("api-mocks/bitcoinaverage/eur.json")));

        bitcoinAverageRateSource = new BitcoinAverageRateSource("USD", MOCK_API_BASE_URL);
    }

    @Test(groups = { "bitcoinaverage" })
    public void mockBitcoinAverageCurrenciesEndpointTest() throws IOException {
        final String actual = httpFetcher.fetchAsString(MOCK_API_BASE_URL + "/ticker/global");
        final String expected = getFileAsString("__files/api-mocks/bitcoinaverage/currencies.json");
        assertThat(actual, is(expected));
    }

    @Test(groups = { "bitcoinaverage" })
    public void mockBitcoinAverageUSDEndpointTest() throws IOException {
        final String actual = httpFetcher.fetchAsString(MOCK_API_BASE_URL + "/ticker/global/USD");
        final String expected = getFileAsString("__files/api-mocks/bitcoinaverage/usd.json");
        assertThat(actual, is(expected));
        assertThat(bitcoinAverageRateSource.getExchangeRateLast(ICurrencies.BTC, ICurrencies.USD).toString(), is("447.53"));
    }

    @Test(groups = { "bitcoinaverage" })
    public void mockBitcoinAverageEUREndpointTest() throws IOException {
        final String actual = httpFetcher.fetchAsString(MOCK_API_BASE_URL + "/ticker/global/EUR");
        final String expected = getFileAsString("__files/api-mocks/bitcoinaverage/eur.json");
        assertThat(actual, is(expected));
        assertThat(bitcoinAverageRateSource.getExchangeRateLast(ICurrencies.BTC, ICurrencies.EUR).toString(), is("398.95"));
    }
}