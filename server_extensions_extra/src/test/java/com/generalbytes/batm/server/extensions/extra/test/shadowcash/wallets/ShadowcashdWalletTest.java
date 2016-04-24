package com.generalbytes.batm.server.extensions.extra.test.shadowcash.wallets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.shadowcashd.ShadowcashdRPCWallet;
import com.generalbytes.batm.server.extensions.extra.test.BaseTest;
import com.generalbytes.batm.server.extensions.extra.test.utils.HttpFetcher;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.rutherford.jsonrpc.value.shadowcash.ShadowInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * @author ludx
 */
public class ShadowcashdWalletTest extends BaseTest {

    private HttpFetcher httpFetcher = new HttpFetcher();
    private WireMockServer wireMockServer;
    private ShadowcashdRPCWallet shadowcashWallet;
    private ObjectMapper mapper = new ObjectMapper();

    private static final String MOCK_API_HOST = "localhost";
    private static final int MOCK_API_PORT = 55555; //51736;
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

        // getinfo
        stubFor(
                post(urlEqualTo("/"))
                        // .withRequestBody(equalToJson("{\"jsonrpc\":\"2.0\",\"method\":\"getinfo\"}", JSONCompareMode.LENIENT))
                        //.withRequestBody(matchingJsonPath("$.method[ ? ( @.method == 'getinfo')]"))
                        .withRequestBody(containing("\"method\":\"getinfo\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/getinfo.json")));

        // getnewaddress
        stubFor(
                post(urlEqualTo("/"))
                        // .withRequestBody(equalToJson("{\"jsonrpc\":\"2.0\",\"method\":\"getinfo\"}", JSONCompareMode.LENIENT))
                        //.withRequestBody(matchingJsonPath("$.method[ ? ( @.method == 'getinfo')]"))
                        .withRequestBody(containing("\"method\":\"getnewaddress\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/getnewaddress.json")));

        // getbalance
        stubFor(
                post(urlEqualTo("/"))
                        // .withRequestBody(equalToJson("{\"jsonrpc\":\"2.0\",\"method\":\"getinfo\"}", JSONCompareMode.LENIENT))
                        //.withRequestBody(matchingJsonPath("$.method[ ? ( @.method == 'getinfo')]"))
                        .withRequestBody(containing("\"method\":\"getbalance\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/getbalance.json")));

        // sendtoaddress
        stubFor(
                post(urlEqualTo("/"))
                        // .withRequestBody(equalToJson("{\"jsonrpc\":\"2.0\",\"method\":\"getinfo\"}", JSONCompareMode.LENIENT))
                        //.withRequestBody(matchingJsonPath("$.method[ ? ( @.method == 'getinfo')]"))
                        .withRequestBody(containing("\"method\":\"sendtoaddress\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/sendtoaddress.json")));

        shadowcashWallet = new ShadowcashdRPCWallet(MOCK_API_BASE_URL, "user", "password");
    }

    @Test(groups = {"shadowcashd"})
    public void mockGetInfoTest() throws IOException {
        final ShadowInfo actual = shadowcashWallet.getInfo();
        final ShadowInfo expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/getinfo.json"), ShadowInfoResponse.class).getResult();
        assertThat(actual, is(expected));
    }

    @Test(groups = {"shadowcashd"})
    public void mockGetCryptoCurrenciesTest() throws IOException {
        final Set<String> cryptoCurrencies = shadowcashWallet.getCryptoCurrencies();
        assertTrue(cryptoCurrencies.size() == 1);
        assertTrue(cryptoCurrencies.contains("SDC"));
    }

    @Test(groups = {"shadowcashd"})
    public void mockGetPreferredCryptoCurrencyTest() throws IOException {
        final String preferredCryptoCurrency = shadowcashWallet.getPreferredCryptoCurrency();
        assertThat(preferredCryptoCurrency, is("SDC"));
    }

    @Test(groups = {"shadowcashd"})
    public void mockGetCryptoAddressTest() throws IOException {
        final String actual = shadowcashWallet.getCryptoAddress("SDC");
        final String expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/getnewaddress.json"), GetNewAddressResponse.class).getResult();
        assertThat(actual, is(expected));
    }

    @Test(groups = {"shadowcashd"})
    public void mockGetCryptoBalanceTest() throws IOException {
        final BigDecimal actual = shadowcashWallet.getCryptoBalance("SDC");
        final BigDecimal expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/getbalance.json"), GetBalanceResponse.class).getResult();
        assertThat(actual, is(expected));
    }

    @Test(groups = {"shadowcashd"})
    public void mockSendCoinsTest() throws IOException {
        final String actual = shadowcashWallet.sendCoins("SWYg6pn3QERg9LQamZgA95rxBumFPp5RYi", new BigDecimal("0.1"), "SDC", "");
        final String expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/sendtoaddress.json"), SendToAddressResponse.class).getResult();
        assertThat(actual, is(expected));
    }

    protected void printLoggedRequests() {
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/.*")));
        Reporter.log("\n\n\n\nlogged requests: " + requests.size(), true);
        for (LoggedRequest loggedRequest : requests) {
            Reporter.log("BODY: " + loggedRequest.getBodyAsString(), true);
            Reporter.log("URL: " + loggedRequest.getAbsoluteUrl(), true);
            Reporter.log("HEADERS: " + loggedRequest.getHeaders().toString(), true);
        }
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ShadowInfoResponse {
        String id;
        String error;
        ShadowInfo result;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetNewAddressResponse {
        String id;
        String error;
        String result;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetBalanceResponse {
        String id;
        String error;
        BigDecimal result;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SendToAddressResponse {
        String id;
        String error;
        String result;
    }

}