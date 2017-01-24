package com.generalbytes.batm.server.extensions.extra.test.shadowcash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.shadowcashd.ShadowcashJSONRPCClient;
import com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.shadowcashd.ShadowcashdRPCWallet;
import com.generalbytes.batm.server.extensions.extra.test.BaseTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author ludx
 */
public class ShadowcashdWalletTest extends BaseTest {

    private WireMockServer wireMockServer;
    private ShadowcashdRPCWallet shadowcashWallet;
    private ObjectMapper mapper = new ObjectMapper();

    private static final String MOCK_API_HOST = "localhost";
    private static final int MOCK_API_PORT = 55555; //51736;
    private static final String MOCK_USER = "testuser";
    private static final String MOCK_PASSWORD = "testpass";
    private static final String MOCK_API_BASE_URL = "http://" + MOCK_USER + ":" + MOCK_PASSWORD + "@" + MOCK_API_HOST + ":" + MOCK_API_PORT;

    @AfterClass
    public void afterClass() {
        //printLoggedRequests();
        wireMockServer.stop();
    }

    @BeforeClass
    public void beforeClass() {

        WireMockConfiguration config = wireMockConfig().port(MOCK_API_PORT)
                .withRootDirectory("src/test/resources");
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();

        WireMock.configureFor(MOCK_API_HOST, wireMockServer.port());

        // getinfo
        stubFor(
                post(urlEqualTo("/"))
                        .withRequestBody(containing("\"method\":\"getinfo\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/getinfo.json")));

        // getnewaddress
        stubFor(
                post(urlEqualTo("/"))
                        .withRequestBody(containing("\"method\":\"getnewaddress\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/getnewaddress.json")));

        // getbalance
        stubFor(
                post(urlEqualTo("/"))
                        .withRequestBody(containing("\"method\":\"getbalance\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/getbalance.json")));

        // sendtoaddress
        stubFor(
                post(urlEqualTo("/"))
                        .withRequestBody(containing("\"method\":\"sendtoaddress\""))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBodyFile("api-mocks/shadowcashd/sendtoaddress.json")));

        shadowcashWallet = new ShadowcashdRPCWallet(MOCK_API_BASE_URL);
    }

    @Test(groups = {"shadowcash.wallet"})
    public void mockGetInfoTest() throws IOException {
        final ShadowcashJSONRPCClient.ShadowInfo actual = shadowcashWallet.getInfo();
        final ShadowcashJSONRPCClient.ShadowInfo expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/getinfo.json"), ShadowInfoResponse.class).getResult();
        assertThat(actual.getVersion(), is(expected.getVersion()));
        assertThat(actual.getBalance(), is(expected.getBalance()));
        assertThat(actual.getBlocks(), is(expected.getBlocks()));
        assertThat(actual.getIp(), is(expected.getIp()));
    }

    @Test(groups = {"shadowcash.wallet"})
    public void mockGetCryptoCurrenciesTest() throws IOException {
        final Set<String> cryptoCurrencies = shadowcashWallet.getCryptoCurrencies();
        assertThat(cryptoCurrencies, hasSize(1));
        assertThat(cryptoCurrencies, contains(ICurrencies.SDC));
    }

    @Test(groups = {"shadowcash.wallet"})
    public void mockGetPreferredCryptoCurrencyTest() throws IOException {
        final String preferredCryptoCurrency = shadowcashWallet.getPreferredCryptoCurrency();
        assertThat(preferredCryptoCurrency, is(ICurrencies.SDC));
    }

    @Test(groups = {"shadowcash.wallet"})
    public void mockGetCryptoAddressTest() throws IOException {
        final String actual = shadowcashWallet.getCryptoAddress(ICurrencies.SDC);
        final String expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/getnewaddress.json"), GetNewAddressResponse.class).getResult();
        assertThat(actual, is(expected));
    }

    @Test(groups = {"shadowcash.wallet"})
    public void mockGetCryptoBalanceTest() throws IOException {
        final BigDecimal actual = shadowcashWallet.getCryptoBalance(ICurrencies.SDC);
        final BigDecimal expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/getbalance.json"), GetBalanceResponse.class).getResult();
        assertThat(actual, is(expected));
    }

    @Test(groups = {"shadowcash.wallet"})
    public void mockSendCoinsTest() throws IOException {
        final String actual = shadowcashWallet.sendCoins("SWYg6pn3QERg9LQamZgA95rxBumFPp5RYi", new BigDecimal("0.1"), ICurrencies.SDC, "");
        final String expected = mapper.readValue(getFile("__files/api-mocks/shadowcashd/sendtoaddress.json"), SendToAddressResponse.class).getResult();
        assertThat(actual, is(expected));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ShadowInfoResponse {
        String id;
        String error;
        ShadowcashJSONRPCClient.ShadowInfo result;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public ShadowcashJSONRPCClient.ShadowInfo getResult() {
            return result;
        }

        public void setResult(ShadowcashJSONRPCClient.ShadowInfo result) {
            this.result = result;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetNewAddressResponse {
        String id;
        String error;
        String result;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetBalanceResponse {
        String id;
        String error;
        BigDecimal result;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public BigDecimal getResult() {
            return result;
        }

        public void setResult(BigDecimal result) {
            this.result = result;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SendToAddressResponse {
        String id;
        String error;
        String result;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

}