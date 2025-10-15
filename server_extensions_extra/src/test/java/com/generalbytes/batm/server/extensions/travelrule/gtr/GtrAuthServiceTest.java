package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrLoginRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrLoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrAuthServiceTest {

    @Mock
    private GtrApi api;
    @Mock
    private GtrConfiguration configuration;
    @InjectMocks
    private GtrAuthService authService;

    @Mock
    private GtrCredentials credentials;

    @Test
    void testRefreshAccessToken() {
        GtrLoginResponse loginResponse = createGtrLoginResponse();

        mockGtrConfigurationAndCredentials();
        when(api.login(any())).thenReturn(loginResponse);

        authService.refreshAccessToken(credentials);

        String accessToken = authService.getAccessToken(credentials);
        assertEquals("jwt_token", accessToken);
        assertLoginRequest();
    }

    @ParameterizedTest
    @ValueSource(classes = {GtrApiException.class, RuntimeException.class})
    void testRefreshAccessToken_throwsException(Class<Throwable> exception) {
        mockGtrConfigurationAndCredentials();
        when(api.login(any())).thenThrow(exception);

        authService.refreshAccessToken(credentials);

        String accessToken = authService.getAccessToken(credentials);
        assertNull(accessToken);
        assertLoginRequest();
    }

    @Test
    void testRefreshAccessToken_concurrentExecution() {
        mockGtrConfigurationAndCredentials();
        GtrLoginResponse gtrLoginResponse = createGtrLoginResponse();

        when(api.login(any(GtrLoginRequest.class))).thenAnswer(invocation -> {
            Thread.sleep(100); // simulate network latency
            return gtrLoginResponse;
        });

        int threadCount = 5;
        CountDownLatch startGate = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startGate.await();
                    authService.refreshAccessToken(credentials);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startGate.countDown();

        verify(api, timeout(1000).times(1)).login(any(GtrLoginRequest.class));
        assertEquals("jwt_token", authService.getAccessToken(credentials));
        executorService.shutdown();
    }

    @Test
    void testRemoveAccessToken_accessTokenSet() {
        GtrLoginResponse loginResponse = createGtrLoginResponse();

        mockGtrConfigurationAndCredentials();
        when(api.login(any())).thenReturn(loginResponse);
        authService.refreshAccessToken(credentials);

        String accessToken = authService.getAccessToken(credentials);
        assertNotNull(accessToken);

        authService.removeAccessToken(credentials);

        String accessTokenAfterRemoval = authService.getAccessToken(credentials);
        assertNull(accessTokenAfterRemoval);
    }

    @Test
    void testRemoveAccessToken_accessTokenNotSet() {
        GtrCredentials gtrCredentials = mock(GtrCredentials.class);
        when(gtrCredentials.getAccessKey()).thenReturn("access_key");

        String accessToken = authService.getAccessToken(gtrCredentials);
        assertNull(accessToken);

        authService.removeAccessToken(gtrCredentials);

        String accessTokenAfterRemoval = authService.getAccessToken(gtrCredentials);
        assertNull(accessTokenAfterRemoval);
    }

    @Test
    void testRemoveAccessToken_accessTokenPending() throws Exception {
        mockGtrConfigurationAndCredentials();

        when(api.login(any(GtrLoginRequest.class))).thenAnswer(invocation -> {
            Thread.sleep(500); // Delay to simulate a time-consuming API call
            return createGtrLoginResponse();
        });
        authService.refreshAccessToken(credentials);
        Thread.sleep(100); // Due to waiting for thread (future) to start, otherwise stubbing exception occurs sometime

        authService.removeAccessToken(credentials);

        String accessTokenAfterRemoval = authService.getAccessToken(credentials);
        assertNull(accessTokenAfterRemoval);
    }

    private void assertLoginRequest() {
        ArgumentCaptor<GtrLoginRequest> loginRequestCaptor = ArgumentCaptor.forClass(GtrLoginRequest.class);
        verify(api, times(1)).login(loginRequestCaptor.capture());
        GtrLoginRequest loginRequest = loginRequestCaptor.getValue();

        assertEquals("vasp_code", loginRequest.getVaspCode());
        assertEquals("access_key", loginRequest.getAccessKey());
        assertEquals("signed_secret_key", loginRequest.getSignedSecretKey());
        assertEquals(30, loginRequest.getExpireInMinutes());
    }

    private GtrLoginResponse createGtrLoginResponse() {
        GtrLoginResponse response = mock(GtrLoginResponse.class);
        when(response.getJwtToken()).thenReturn("jwt_token");

        return response;
    }

    private void mockGtrConfigurationAndCredentials() {
        when(credentials.getVaspCode()).thenReturn("vasp_code");
        when(credentials.getAccessKey()).thenReturn("access_key");
        when(credentials.getSignedSecretKey()).thenReturn("signed_secret_key");
        when(configuration.getAccessTokenExpirationInMinutes()).thenReturn(30);
    }

}