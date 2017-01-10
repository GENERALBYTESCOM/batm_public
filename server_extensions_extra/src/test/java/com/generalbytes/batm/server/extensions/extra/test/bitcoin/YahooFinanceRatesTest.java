package com.generalbytes.batm.server.extensions.extra.test.bitcoin;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.yahoo.YahooFinanceRateSource;
import com.generalbytes.batm.server.extensions.extra.test.BaseTest;
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
public class YahooFinanceRatesTest extends BaseTest {

    private YahooFinanceRateSource yahooFinanceRateSource;

    private WireMockServer wireMockServer;

    private static final String MOCK_API_HOST = "localhost";
    private static final int MOCK_API_PORT = 8888;
    private static final String MOCK_API_BASE_URL = "http://" + MOCK_API_HOST + ":" + MOCK_API_PORT;

    @AfterClass
    public void afterClass() {
        //printLoggedRequests();
        wireMockServer.stop();
    }

    @BeforeClass
    public void beforeClass() {

        WireMockConfiguration config = wireMockConfig().port(MOCK_API_PORT);
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();

        WireMock.configureFor(MOCK_API_HOST, wireMockServer.port());

        // YahooFinance rates
        stubFor(
                get(urlEqualTo("/d/quotes.csv?e=.csv&f=sl1d1t1&s=BTCRON=X+BTCAUD=X+BTCCHF=X+BTCJPY=X+BTCEUR=X+BTCGBP=X+BTCCZK=X+BTCUSD=X+BTCCAD=X+BTCXAF=X+BTCCNY=X+"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/octet-stream")
                                .withBodyFile("api-mocks/yahoofinance/rates.csv")));

        yahooFinanceRateSource = new YahooFinanceRateSource("USD", MOCK_API_BASE_URL);
    }

    @Test(groups = { "yahoofinance" })
    public void mockYahooFinanceUSDEndpointTest() throws IOException {
        final String actual = HttpFetcher.fetchAsString(MOCK_API_BASE_URL + "/d/quotes.csv?e=.csv&f=sl1d1t1&s=BTCRON=X+BTCAUD=X+BTCCHF=X+BTCJPY=X+BTCEUR=X+BTCGBP=X+BTCCZK=X+BTCUSD=X+BTCCAD=X+BTCXAF=X+BTCCNY=X+");
        final String expected = getFileAsString("__files/api-mocks/yahoofinance/rates.csv");
        assertThat(actual, is(expected));
        assertThat(yahooFinanceRateSource.getExchangeRateLast(ICurrencies.BTC, ICurrencies.USD).toString(), is("903.5100"));
    }

    @Test(groups = { "yahoofinance" })
    public void mockYahooFinanceEUREndpointTest() throws IOException {
        final String actual = HttpFetcher.fetchAsString(MOCK_API_BASE_URL + "/d/quotes.csv?e=.csv&f=sl1d1t1&s=BTCRON=X+BTCAUD=X+BTCCHF=X+BTCJPY=X+BTCEUR=X+BTCGBP=X+BTCCZK=X+BTCUSD=X+BTCCAD=X+BTCXAF=X+BTCCNY=X+");
        final String expected = getFileAsString("__files/api-mocks/yahoofinance/rates.csv");
        assertThat(actual, is(expected));
        assertThat(yahooFinanceRateSource.getExchangeRateLast(ICurrencies.BTC, ICurrencies.EUR).toString(), is("854.9951"));
    }
}