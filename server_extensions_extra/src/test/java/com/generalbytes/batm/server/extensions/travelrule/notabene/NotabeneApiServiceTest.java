package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApiCall;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApiException;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneApiError;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneApiServiceTest {

    @Mock
    private NotabeneAuthService authService;

    @InjectMocks
    private NotabeneApiService notabeneApiService;

    @Test
    void testCallApi_validToken() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneApiCall<NotabeneTransferInfo> notabeneApiCall = mock(NotabeneApiCall.class);
        NotabeneTransferInfo expectedResponse = mock(NotabeneTransferInfo.class);

        when(authService.getAccessToken(providerCredentials)).thenReturn("validAccessToken");
        when(notabeneApiCall.execute("Bearer validAccessToken")).thenReturn(expectedResponse);

        NotabeneTransferInfo response = notabeneApiService.callApi(providerCredentials, notabeneApiCall);

        assertEquals(expectedResponse, response);
        verify(notabeneApiCall, times(1)).execute("Bearer validAccessToken");
        verify(authService, times(1)).getAccessToken(providerCredentials);
        verify(authService, never()).refreshAccessToken(any());
        verifyNoInteractions(providerCredentials, expectedResponse);
    }

    @Test
    void testCallApi_invalidToken_refresh() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneApiCall<NotabeneTransferInfo> notabeneApiCall = mock(NotabeneApiCall.class);
        NotabeneTransferInfo expectedResponse = mock(NotabeneTransferInfo.class);

        when(authService.getAccessToken(providerCredentials))
            .thenReturn("invalidAccessToken")
            .thenReturn("validAccessToken");
        when(notabeneApiCall.execute("Bearer invalidAccessToken")).thenThrow(createUnauthorizedException());
        when(notabeneApiCall.execute("Bearer validAccessToken")).thenReturn(expectedResponse);

        NotabeneTransferInfo response = notabeneApiService.callApi(providerCredentials, notabeneApiCall);

        assertEquals(expectedResponse, response);
        verify(notabeneApiCall, times(1)).execute("Bearer invalidAccessToken");
        verify(notabeneApiCall, times(1)).execute("Bearer validAccessToken");
        verify(authService, times(2)).getAccessToken(providerCredentials);
        verify(authService, times(1)).refreshAccessToken(providerCredentials);
        verifyNoInteractions(providerCredentials, expectedResponse);
    }

    private static Stream<Arguments> provideNonUnauthorizedExceptions() {
        return Stream.of(
            Arguments.arguments(new RuntimeException("Test Exception")),
            Arguments.arguments(createNotabeneApiException(HttpServletResponse.SC_FORBIDDEN, "UnauthorizedError")),
            Arguments.arguments(createNotabeneApiException(HttpServletResponse.SC_UNAUTHORIZED, "NotabenePermissionsError"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonUnauthorizedExceptions")
    void testCallApi_exception(Exception exception) {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneApiCall<NotabeneTransferInfo> notabeneApiCall = mock(NotabeneApiCall.class);

        when(authService.getAccessToken(providerCredentials)).thenReturn("validAccessToken");
        when(notabeneApiCall.execute("Bearer validAccessToken")).thenThrow(exception);

        RuntimeException resultException = assertThrows(RuntimeException.class,
            () -> notabeneApiService.callApi(providerCredentials, notabeneApiCall));

        assertEquals(exception, resultException);
        verify(notabeneApiCall, times(1)).execute("Bearer validAccessToken");
        verify(authService, times(1)).getAccessToken(providerCredentials);
        verify(authService, never()).refreshAccessToken(any());
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testCallApi_refreshThrows_fail() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneApiCall<NotabeneTransferInfo> notabeneApiCall = mock(NotabeneApiCall.class);

        when(authService.getAccessToken(providerCredentials)).thenReturn("validAccessToken");
        when(notabeneApiCall.execute("Bearer validAccessToken")).thenThrow(createUnauthorizedException());
        doThrow(new RuntimeException("Test Exception")).when(authService).refreshAccessToken(providerCredentials);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> notabeneApiService.callApi(providerCredentials, notabeneApiCall));

        assertEquals("Test Exception", exception.getMessage());
        verify(notabeneApiCall, times(1)).execute("Bearer validAccessToken");
        verify(authService, times(1)).getAccessToken(providerCredentials);
        verify(authService, times(1)).refreshAccessToken(providerCredentials);
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testCallApi_invalidToken_refresh_exceeded_repetitions() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneApiCall<NotabeneTransferInfo> notabeneApiCall = mock(NotabeneApiCall.class);
        NotabeneTransferInfo expectedResponse = mock(NotabeneTransferInfo.class);

        when(authService.getAccessToken(providerCredentials)).thenReturn("invalidAccessToken");

        RuntimeException unauthorizedException = createUnauthorizedException();
        when(notabeneApiCall.execute("Bearer invalidAccessToken")).thenThrow(unauthorizedException);

        NotabeneApiException exception = assertThrows(NotabeneApiException.class,
            () -> notabeneApiService.callApi(providerCredentials, notabeneApiCall));

        assertEquals(unauthorizedException, exception);

        verify(notabeneApiCall, times(6)).execute("Bearer invalidAccessToken");
        verify(authService, times(6)).getAccessToken(providerCredentials);
        verify(authService, times(5)).refreshAccessToken(providerCredentials);
        verifyNoInteractions(providerCredentials, expectedResponse);
    }

    private static RuntimeException createUnauthorizedException() {
        return createNotabeneApiException(HttpServletResponse.SC_UNAUTHORIZED, "UnauthorizedError");
    }

    private static RuntimeException createNotabeneApiException(int statusCode, String name) {
        NotabeneApiError notabeneApiError = new NotabeneApiError();
        notabeneApiError.setCode(statusCode);
        notabeneApiError.setMessage("Test Exception");
        notabeneApiError.setName(name);
        return new NotabeneApiException(notabeneApiError);
    }

}