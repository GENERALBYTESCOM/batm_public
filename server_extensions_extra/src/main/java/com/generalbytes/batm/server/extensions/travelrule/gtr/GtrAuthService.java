package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrLoginRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for managing the access tokens for Global Travel Rule (GTR) API.
 */
@Slf4j
@RequiredArgsConstructor
public class GtrAuthService {

    private final Map<String, String> accessTokens = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<String>> tokenRequests = new ConcurrentHashMap<>();
    private final GtrApi api;
    private final GtrConfiguration configuration;

    /**
     * Get the current access token.
     * <p>
     * There is no guarantee that this token is valid.
     * If it is not use {@link #refreshAccessToken} to refresh it.
     * </p>
     *
     * @param credentials {@link GtrCredentials}
     * @return The access token.
     */
    public String getAccessToken(GtrCredentials credentials) {
        CompletableFuture<String> tokenRequest = tokenRequests.get(credentials.getAccessKey());
        if (tokenRequest != null) {
            try {
                tokenRequest.join();
            } catch (Exception e) {
                log.error("An error occurred while waiting for the GTR token request to complete.", e);
                return null; // The background token refresh failed. Return null as no valid token is available.
            }
        }

        return accessTokens.get(credentials.getAccessKey());
    }

    /**
     * Refresh the access token.
     *
     * <p>Use {@link #getAccessToken} to get the current token.</p>
     *
     * @param credentials {@link GtrCredentials}
     */
    public void refreshAccessToken(GtrCredentials credentials) {
        tokenRequests.computeIfAbsent(
                credentials.getAccessKey(),
                accessKey -> {
                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> getAccessTokenInternal(credentials));
                    future.whenComplete((accessToken, throwable) -> {
                        if (accessToken == null || throwable != null) {
                            logRefreshAccessTokenFailure(throwable);
                            accessTokens.remove(accessKey);
                        }

                        tokenRequests.remove(accessKey);
                    });
                    return future;
                }
        );
    }

    private void logRefreshAccessTokenFailure(Throwable throwable) {
        String reason = throwable == null ? "refresh failure" : throwable.getMessage();
        log.error("GTR - failed to refresh access token: {}", reason);
    }

    /**
     * Removes access token.
     *
     * @param credentials {@link GtrCredentials}
     */
    public void removeAccessToken(GtrCredentials credentials) {
        String accessKey = credentials.getAccessKey();
        CompletableFuture<String> pendingRequest = tokenRequests.remove(accessKey);
        if (pendingRequest != null) {
            pendingRequest.cancel(true);
        }

        String accessToken = accessTokens.remove(accessKey);
        if (accessToken != null) {
            log.debug("Removed GTR access token for accessKey: {}", accessKey);
        } else {
            log.debug("No GTR access token found for accessKey: {}", accessKey);
        }
    }

    private String getAccessTokenInternal(GtrCredentials credentials) {
        GtrLoginRequest loginRequest = createGtrLoginRequest(credentials);
        GtrLoginResponse loginResponse = callGtrLogin(loginRequest);
        if (loginResponse == null) {
            return null;
        }

        accessTokens.put(credentials.getAccessKey(), loginResponse.getJwtToken());

        return loginResponse.getJwtToken();
    }

    private GtrLoginRequest createGtrLoginRequest(GtrCredentials credentials) {
        GtrLoginRequest request = new GtrLoginRequest();
        request.setVaspCode(credentials.getVaspCode());
        request.setAccessKey(credentials.getAccessKey());
        request.setSignedSecretKey(credentials.getSignedSecretKey());
        request.setExpireInMinutes(configuration.getAccessTokenExpirationInMinutes());

        return request;
    }

    private GtrLoginResponse callGtrLogin(GtrLoginRequest request) {
        try {
            return api.login(request);
        } catch (GtrApiException e) {
            log.error("GTR - failed login, auth token not obtained: {}", e.getMessage());
        } catch (Exception e) {
            log.error("GTR - failed login, auth token not obtained, unexpected error", e);
        }
        return null;
    }

}
