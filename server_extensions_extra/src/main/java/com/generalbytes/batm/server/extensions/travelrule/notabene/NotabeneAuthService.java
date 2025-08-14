package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneAuthApi;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneGenerateAccessTokenRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneGenerateAccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import si.mazi.rescu.HttpStatusIOException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This service is responsible for managing the access token for Notabene API.
 */
@Slf4j
public class NotabeneAuthService {

    private final Map<String, String> accessTokens = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<String>> tokenRequests = new ConcurrentHashMap<>();
    private final NotabeneAuthApi notabeneAuthApi;
    private final NotabeneConfiguration configuration;

    public NotabeneAuthService(NotabeneApiFactory apiFactory, NotabeneConfiguration configuration) {
        this.notabeneAuthApi = apiFactory.getNotabeneAuthApi();
        this.configuration = configuration;
    }

    public void removeAccessToken(ITravelRuleProviderCredentials providerCredentials) {
        String clientId = providerCredentials.getClientId();
        synchronized (tokenRequests) {
            log.debug("Clearing access token for clientId: {}", clientId);
            CompletableFuture<?> future = tokenRequests.get(clientId);
            if (future != null && !future.isDone()) {
                try {
                    future.join();
                } catch (Exception e) {
                    log.trace("An error occurred while waiting for the token request to complete. Does not affect the token removal.", e);
                }
            }
            String accessToken = accessTokens.remove(clientId);
            tokenRequests.remove(clientId);

            if (accessToken != null) {
                log.debug("Removed access token for clientId: {}", clientId);
            } else {
                log.debug("No access token found for clientId: {}", clientId);
            }
        }
    }

    /**
     * Get the current access token.
     *
     * <p>There is no guarantee that this token is valid. If it is not,
     * use {@link #refreshAccessToken(ITravelRuleProviderCredentials)} to refresh it.</p>
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to get the access token for.
     * @return The access token.
     * @throws IllegalArgumentException If providerCredentials is null.
     */
    public String getAccessToken(ITravelRuleProviderCredentials providerCredentials) {
        if (providerCredentials == null) {
            throw new IllegalArgumentException("providerCredentials cannot be null");
        }

        CompletableFuture<String> tokenRequest = tokenRequests.get(providerCredentials.getClientId());
        if (tokenRequest != null) {
            try {
                tokenRequest.join();
            } catch (Exception e) {
                log.debug("An error occurred while waiting for the Notabene token request to complete.", e);
                return null; // The background token refresh failed. Return null as no valid token is available.
            }
        }

        return accessTokens.get(providerCredentials.getClientId());
    }

    /**
     * Refresh the access token for the given {@link ITravelRuleProviderCredentials}.
     *
     * <p>Use {@link #getAccessToken(ITravelRuleProviderCredentials)} to get the current token.</p>
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to refresh the token for.
     */
    public void refreshAccessToken(ITravelRuleProviderCredentials providerCredentials) {
        if (providerCredentials == null) {
            throw new IllegalArgumentException("providerCredentials cannot be null");
        }

        tokenRequests.computeIfAbsent(providerCredentials.getClientId(), getAccessTokenAsync(providerCredentials))
            .whenComplete((result, throwable) -> handleCompletedCall(providerCredentials, throwable));
    }

    private Function<String, CompletableFuture<String>> getAccessTokenAsync(ITravelRuleProviderCredentials providerCredentials) {
        return clientId -> CompletableFuture.supplyAsync(() -> getAccessToken(providerCredentials, clientId));
    }

    private String getAccessToken(ITravelRuleProviderCredentials providerCredentials, String clientId) {
        NotabeneGenerateAccessTokenRequest request = createNotabeneGenerateAccessTokenRequest(providerCredentials);
        NotabeneGenerateAccessTokenResponse response = callGetAccessTokenApi(request);
        if (response == null) {
            return null;
        }
        accessTokens.put(clientId, response.getAccessToken());
        return response.getAccessToken();
    }

    private void handleCompletedCall(ITravelRuleProviderCredentials providerCredentials, Throwable throwable) {
        if (throwable != null) {
            log.error("Failed to refresh access token: {}", throwable.getMessage());
        } else {
            tokenRequests.remove(providerCredentials.getClientId());
        }
    }

    private NotabeneGenerateAccessTokenResponse callGetAccessTokenApi(NotabeneGenerateAccessTokenRequest request) {
        try {
            return notabeneAuthApi.generateAccessToken(request);
        } catch (HttpStatusIOException e) {
            if (e.getHttpStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
                throw new CompletionException("unauthorized request", e);
            }
            if (e.getHttpStatusCode() == HttpServletResponse.SC_FORBIDDEN) {
                throw new CompletionException("forbidden request", e);
            }
            log.error("Failed to get access token, unexpected error, status: {}, message: {}, response: {}",
                e.getHttpStatusCode(), e.getMessage(), e.getHttpBody());
        } catch (Exception e) {
            log.error("Failed to refresh access token, unexpected error", e);
        }
        return null;
    }

    private NotabeneGenerateAccessTokenRequest createNotabeneGenerateAccessTokenRequest(ITravelRuleProviderCredentials travelRuleProvider) {
        NotabeneGenerateAccessTokenRequest request = new NotabeneGenerateAccessTokenRequest();
        request.setClientId(travelRuleProvider.getClientId());
        request.setClientSecret(travelRuleProvider.getClientSecret());
        request.setGrantType("client_credentials");
        request.setAudience(configuration.getApiUrl());
        return request;
    }

}
