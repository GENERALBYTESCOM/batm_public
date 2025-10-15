package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneAuthApi;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneGenerateAccessTokenRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneGenerateAccessTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneAuthServiceTest {

    private static final String NOTABENE_API_URL = "https://api.notabene.id";

    @Mock
    private NotabeneAuthApi authApi;
    @Mock
    private NotabeneApiFactory apiFactory;
    @Mock
    private NotabeneConfiguration configuration;

    private NotabeneAuthService authService;

    @BeforeEach
    void setUp() {
        when(apiFactory.getNotabeneAuthApi()).thenReturn(authApi);

        authService = new NotabeneAuthService(apiFactory, configuration);
    }

    @Test
    void testRefreshAccessToken_nullProviderDetails() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> authService.refreshAccessToken(null));

        assertEquals("providerCredentials cannot be null", exception.getMessage());
        verifyNoInteractions(authApi);
    }

    @Test
    void testRefreshAccessToken_valid() throws HttpStatusIOException {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();
        when(authApi.generateAccessToken(any())).thenReturn(createAccessTokenResponse("accessToken1"));

        when(configuration.getApiUrl()).thenReturn(NOTABENE_API_URL);

        authService.refreshAccessToken(providerCredentials);
        String result = authService.getAccessToken(providerCredentials);

        assertEquals("accessToken1", result);
        verifyAccessTokenRequested();
    }

    @ParameterizedTest
    @ValueSource(ints = { 401, 403 })
    void testRefreshAccessToken_invalidGetAccessTokenResponse400(int statusCode) throws HttpStatusIOException {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();
        HttpStatusIOException httpStatusIOException = mock(HttpStatusIOException.class);
        when(httpStatusIOException.getHttpStatusCode()).thenReturn(statusCode);

        when(authApi.generateAccessToken(any())).thenThrow(httpStatusIOException);

        when(configuration.getApiUrl()).thenReturn(NOTABENE_API_URL);
        authService.refreshAccessToken(providerCredentials);

        String accessToken = authService.getAccessToken(providerCredentials);
        assertNull(accessToken);

        verifyAccessTokenRequested();
    }

    private static Object[][] invalidGetAccessTokenResponseUnexpectedExceptionSource() {
        HttpStatusIOException httpStatusIOException = mock(HttpStatusIOException.class);
        when(httpStatusIOException.getHttpStatusCode()).thenReturn(500);

        return new Object[][]{
            {httpStatusIOException},
            {new RuntimeException("unexpected exception")},
        };
    }

    @ParameterizedTest
    @MethodSource("invalidGetAccessTokenResponseUnexpectedExceptionSource")
    void testRefreshAccessToken_invalidGetAccessTokenResponseUnexpected(Exception exception) throws HttpStatusIOException {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();
        when(authApi.generateAccessToken(any())).thenThrow(exception);
        when(configuration.getApiUrl()).thenReturn(NOTABENE_API_URL);

        authService.refreshAccessToken(providerCredentials);
        String accessToken = authService.getAccessToken(providerCredentials);
        assertNull(accessToken);
        verifyAccessTokenRequested();
    }

    @Test
    void testGetAccessToken_nullTravelRuleProviderIdentification() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> authService.getAccessToken(null));

        assertEquals("providerCredentials cannot be null", exception.getMessage());
        verifyNoInteractions(authApi);
    }

    @Test
    void testGetAccessToken_noCachedToken() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        String result = authService.getAccessToken(providerCredentials);

        assertNull(result);
    }

    @Test
    void testGetAccessToken() throws Exception {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        assertNull(authService.getAccessToken(providerCredentials));

        when(authApi.generateAccessToken(any()))
            .thenReturn(createAccessTokenResponse("accessToken1"))
            .thenReturn(createAccessTokenResponse("accessToken2"));
        authService.refreshAccessToken(providerCredentials);

        await().atMost(500, TimeUnit.MILLISECONDS).until(() ->
            "accessToken1".equals(authService.getAccessToken(providerCredentials))
        );

        authService.refreshAccessToken(providerCredentials);

        await().atMost(500, TimeUnit.MILLISECONDS).until(() ->
            "accessToken2".equals(authService.getAccessToken(providerCredentials))
        );
    }

    @Test
    void testRefreshAccessToken_concurrentExecution() throws HttpStatusIOException {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(authApi.generateAccessToken(any(NotabeneGenerateAccessTokenRequest.class))).thenAnswer(invocation -> {
            Thread.sleep(100); // simulate network latency
            return createAccessTokenResponse("testToken");
        });

        int threadCount = 5;
        CountDownLatch startGate = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startGate.await();
                    authService.refreshAccessToken(providerCredentials);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startGate.countDown();

        verify(authApi, timeout(1000).times(1))
            .generateAccessToken(any(NotabeneGenerateAccessTokenRequest.class));
        assertEquals("testToken", authService.getAccessToken(providerCredentials));
        executorService.shutdown();
    }

    @Test
    void testRemoveAccessToken_accessTokenSet() throws HttpStatusIOException {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(authApi.generateAccessToken(any())).thenReturn(createAccessTokenResponse("accessToken1"));
        authService.refreshAccessToken(providerCredentials);

        String accessToken = authService.getAccessToken(providerCredentials);
        assertNotNull(accessToken);

        authService.removeAccessToken(providerCredentials);

        String accessTokenAfterRemoval = authService.getAccessToken(providerCredentials);
        assertNull(accessTokenAfterRemoval);
    }

    @Test
    void testRemoveAccessToken__accessTokenNotSet() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        String accessToken = authService.getAccessToken(providerCredentials);
        assertNull(accessToken);

        authService.removeAccessToken(providerCredentials);

        String accessTokenAfterRemoval = authService.getAccessToken(providerCredentials);
        assertNull(accessTokenAfterRemoval);
    }


    @Test
    void testRemoveAccessToken_failedToFetchAccessToken() throws HttpStatusIOException {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();
        HttpStatusIOException httpStatusIOException = mock(HttpStatusIOException.class);
        when(httpStatusIOException.getHttpStatusCode()).thenReturn(401);

        when(authApi.generateAccessToken(any())).thenThrow(httpStatusIOException);
        authService.refreshAccessToken(providerCredentials);

        String accessToken = authService.getAccessToken(providerCredentials);
        assertNull(accessToken);

        authService.removeAccessToken(providerCredentials);

        String accessTokenAfterRemoval = authService.getAccessToken(providerCredentials);
        assertNull(accessTokenAfterRemoval);
    }

    @Test
    void testRemoveAccessToken_accessTokenPending() throws Exception {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(authApi.generateAccessToken(any(NotabeneGenerateAccessTokenRequest.class))).thenAnswer(invocation -> {
            Thread.sleep(500); // Delay to simulate a time-consuming API call
            return createAccessTokenResponse("accessToken1");
        });
        authService.refreshAccessToken(providerCredentials);
        Thread.sleep(100); // Due to waiting for thread (future) to start, otherwise stubbing exception occurs sometime

        authService.removeAccessToken(providerCredentials);

        String accessTokenAfterRemoval = authService.getAccessToken(providerCredentials);
        assertNull(accessTokenAfterRemoval);
    }

    private void verifyAccessTokenRequested() throws HttpStatusIOException {
        ArgumentCaptor<NotabeneGenerateAccessTokenRequest> captor = ArgumentCaptor.forClass(NotabeneGenerateAccessTokenRequest.class);
        verify(authApi, times(1)).generateAccessToken(captor.capture());
        captor.getAllValues().forEach(this::assertGenerateAccessTokenRequest);
    }

    private void assertGenerateAccessTokenRequest(NotabeneGenerateAccessTokenRequest request) {
        assertNotNull(request);
        assertEquals("clientId", request.getClientId());
        assertEquals("clientSecret", request.getClientSecret());
        assertEquals("client_credentials", request.getGrantType());
        assertEquals(NOTABENE_API_URL, request.getAudience());
    }

    private NotabeneGenerateAccessTokenResponse createAccessTokenResponse(String accessToken) {
        NotabeneGenerateAccessTokenResponse response = new NotabeneGenerateAccessTokenResponse();
        response.setAccessToken(accessToken);
        return response;
    }

    private ITravelRuleProviderCredentials createTravelRuleProviderIdentification() {
        return new ITravelRuleProviderCredentials() {
            @Override
            public String getClientId() {
                return "clientId";
            }

            @Override
            public String getClientSecret() {
                return "clientSecret";
            }

            @Override
            public String getVaspDid() {
                return "vaspDid";
            }

            @Override
            public String getPublicKey() {
                return null;
            }

            @Override
            public String getPrivateKey() {
                return null;
            }
        };
    }

}