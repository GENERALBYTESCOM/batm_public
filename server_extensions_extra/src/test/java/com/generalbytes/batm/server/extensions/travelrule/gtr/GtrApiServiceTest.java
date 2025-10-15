package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiCall;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiError;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrApiServiceTest {

    @Mock
    private GtrAuthService authService;
    @InjectMocks
    private GtrApiService apiService;

    @Mock
    private GtrCredentials credentials;

    @Test
    void testCallApi() {
        GtrApiCall<String> apiCall = mock(GtrApiCall.class);

        when(authService.getAccessToken(credentials)).thenReturn("valid_access_token");
        when(apiCall.execute("Bearer valid_access_token")).thenReturn("valid response");

        String response = apiService.callApi(credentials, apiCall);

        assertEquals("valid response", response);
        verify(authService, times(1)).getAccessToken(credentials);
        verify(apiCall, times(1)).execute(anyString());
        verify(authService, never()).refreshAccessToken(credentials);
    }

    @Test
    void testCallApi_nonAuthorizedStatusCode() {
        GtrApiCall<String> apiCall = mock(GtrApiCall.class);
        GtrApiException gtrApiException = new GtrApiException(
                createGtrApiError("Service internal error", 100006, "GTR Internal Server Error")
        );

        when(authService.getAccessToken(credentials)).thenReturn("invalid_access_token");
        when(apiCall.execute("Bearer invalid_access_token")).thenThrow(gtrApiException);

        GtrApiException exception = assertThrows(GtrApiException.class, () -> apiService.callApi(credentials, apiCall));

        assertEquals(100006, exception.getStatusCode());
        assertEquals("Service internal error (GTR Internal Server Error)", exception.getMessage());
        verify(authService, times(1)).getAccessToken(credentials);
        verify(apiCall, times(1)).execute(anyString());
        verify(authService, never()).refreshAccessToken(credentials);
    }

    @Test
    void testCallApi_refreshToken() {
        GtrApiCall<String> apiCall = mock(GtrApiCall.class);
        GtrApiException gtrApiException = new GtrApiException(
                createGtrApiError("vasp not auth, please login", 100020, "VASP_SESSION_NOT_AUTH")
        );

        when(authService.getAccessToken(credentials)).thenReturn("invalid_access_token", "valid_access_token");
        when(apiCall.execute("Bearer invalid_access_token")).thenThrow(gtrApiException);
        when(apiCall.execute("Bearer valid_access_token")).thenReturn("valid response");

        String response = apiService.callApi(credentials, apiCall);

        assertEquals("valid response", response);
        verify(authService, times(2)).getAccessToken(credentials);
        verify(apiCall, times(2)).execute(anyString());
        verify(authService, times(1)).refreshAccessToken(credentials);
    }

    @Test
    void testCallApi_tokenRefreshLimitExceeded() {
        GtrApiCall<String> apiCall = mock(GtrApiCall.class);
        GtrApiException gtrApiException = new GtrApiException(
                createGtrApiError("vasp not auth, please login", 100020, "VASP_SESSION_NOT_AUTH")
        );

        when(authService.getAccessToken(credentials)).thenReturn("invalid_access_token");
        when(apiCall.execute("Bearer invalid_access_token")).thenThrow(gtrApiException);

        GtrApiException exception = assertThrows(GtrApiException.class, () -> apiService.callApi(credentials, apiCall));

        assertEquals(100020, exception.getStatusCode());
        assertEquals("vasp not auth, please login (VASP_SESSION_NOT_AUTH)", exception.getMessage());
        verify(authService, times(6)).getAccessToken(credentials);
        verify(apiCall, times(6)).execute(anyString());
        verify(authService, times(5)).refreshAccessToken(credentials);
    }

    private GtrApiError createGtrApiError(String msg, int verifyStatus, String verifyMessage) {
        GtrApiError gtrApiError = new GtrApiError();
        gtrApiError.setMsg(msg);
        gtrApiError.setVerifyStatus(verifyStatus);
        gtrApiError.setVerifyMessage(verifyMessage);

        return gtrApiError;
    }

}