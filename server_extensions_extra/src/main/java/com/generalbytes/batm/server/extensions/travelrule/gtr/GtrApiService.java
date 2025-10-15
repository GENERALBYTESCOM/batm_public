package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiCall;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for processing API calls with access token verification.
 */
@Slf4j
@AllArgsConstructor
public class GtrApiService {

    /**
     * GTR unauthorized status code.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/api-references/enum/VerifyStatusEnum">Global Travel Rule (GTR) documentation</a>
     */
    private static final int UNAUTHORIZED_STATUS_CODE = 100020;
    private static final int MAX_REFRESH_ACCESS_TOKEN_CALLS = 5;

    private final GtrAuthService authService;

    /**
     * Make a call to Global Travel Rule (GTR) API.
     *
     * <p>This method will take care of authorization.</p>
     *
     * @param apiCall The call to make.
     * @param <T>     Type of the expected response.
     * @return Response from the call.
     */
    public <T> T callApi(GtrCredentials credentials, GtrApiCall<T> apiCall) {
        return callApiInternal(credentials, apiCall, MAX_REFRESH_ACCESS_TOKEN_CALLS);
    }

    private <T> T callApiInternal(GtrCredentials credentials, GtrApiCall<T> apiCall, int remainingRepetitions) {
        try {
            String accessToken = authService.getAccessToken(credentials);
            String authorization = getAuthorizationHeaderContent(accessToken);
            return apiCall.execute(authorization);
        } catch (GtrApiException e) {
            log.trace("GTR API call failed", e);
            if (e.getStatusCode() == UNAUTHORIZED_STATUS_CODE) {
                if (remainingRepetitions <= 0) {
                    log.warn("GTR - failed to refresh access token after {} attempts", MAX_REFRESH_ACCESS_TOKEN_CALLS);
                    throw e;
                }
                authService.refreshAccessToken(credentials);

                return callApiInternal(credentials, apiCall, --remainingRepetitions);
            }
            throw e;
        }
    }

    private String getAuthorizationHeaderContent(String accessToken) {
        return "Bearer " + accessToken;
    }

}
