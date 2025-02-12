package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApiCall;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;

/**
 * This service is responsible for making calls to Notabene API.
 */
@Slf4j
@AllArgsConstructor
public class NotabeneApiService {

    private static final String UNAUTHORIZED_ERROR_NAME = "UnauthorizedError";
    private static final int MAX_REFRESH_ACCESS_TOKEN_CALLS = 5;

    private final NotabeneAuthService authService;

    /**
     * Make a call to Notabene API.
     *
     * <p>This method will take care of authorization.</p>
     *
     * @param providerCredentials The used {@link ITravelRuleProviderCredentials}.
     * @param apiCall             The call to make.
     * @param <T>                 Type of the expected response.
     * @return Response from the call.
     */
    public <T> T callApi(ITravelRuleProviderCredentials providerCredentials, NotabeneApiCall<T> apiCall) {
        return callApiInternal(providerCredentials, apiCall, MAX_REFRESH_ACCESS_TOKEN_CALLS);
    }

    private <T> T callApiInternal(ITravelRuleProviderCredentials providerCredentials, NotabeneApiCall<T> apiCall, int remainingRepetitions) {
        try {
            String accessToken = authService.getAccessToken(providerCredentials);
            String authorization = getAuthorizationHeaderContent(accessToken);
            return apiCall.execute(authorization);
        } catch (NotabeneApiException e) {
            log.trace("Notabene API call failed", e);
            if (e.getCode() == HttpServletResponse.SC_UNAUTHORIZED && UNAUTHORIZED_ERROR_NAME.equalsIgnoreCase(e.getName())) {
                if (remainingRepetitions <= 0) {
                    log.warn("Failed to refresh access token after {} attempts", MAX_REFRESH_ACCESS_TOKEN_CALLS);
                    throw e;
                }
                authService.refreshAccessToken(providerCredentials);

                return callApiInternal(providerCredentials, apiCall, --remainingRepetitions);
            }
            throw e;
        }
    }

    private String getAuthorizationHeaderContent(String accessToken) {
        return "Bearer " + accessToken;
    }

}
