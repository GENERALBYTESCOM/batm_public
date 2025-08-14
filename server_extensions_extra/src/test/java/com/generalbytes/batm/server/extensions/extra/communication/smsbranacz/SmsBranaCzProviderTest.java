package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsBranaCzProviderTest {
    @Mock
    private SmsBranaCzApiService apiService;
    @Mock
    private SmsBranaCzCredentialsService credentialsService;
    @InjectMocks
    private SmsBranaCzProvider provider;

    @Test
    void testGetName() {
        assertEquals("SMSBrana.cz", provider.getName());
    }

    @Test
    void testGerPublicName() {
        assertEquals("SMSBrana.cz", provider.getPublicName());
    }

    @Test
    void testSendSms_invalidCredentials() {
        when(credentialsService.getCredentials("credentials")).thenThrow(new SmsBranaCzValidationException("Invalid credentials format"));

        ISmsResponse response = provider.sendSms("credentials", null, null);

        assertErrorResponse(response, "Invalid credentials format");

    }

    @Test
    void testSendSms_success() throws IOException {
        SmsBranaCzApiCredentials apiCredentials = new SmsBranaCzApiCredentials("login", "salt", "time", "auth");
        when(credentialsService.getCredentials("login:password")).thenReturn(apiCredentials);
        when(apiService.sendSms(apiCredentials, "+420123456789", "test abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.:;%/_-+ěščřžýáíé")).thenReturn(createXmlResponse());

        ISmsResponse response = provider.sendSms("login:password", "+420123456789", "test abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.:;%/_-+ěščřžýáíé");
        assertNotNull(response);
        assertEquals(ISmsResponse.ResponseStatus.OK, response.getStatus());
        assertNull(response.getErrorResponse());
        assertEquals("1234", response.getSid());
        assertEquals(0, new BigDecimal("1.23").compareTo(response.getPrice()));
    }

    @Test
    void testSendSms_nullResponse() throws IOException {
        when(credentialsService.getCredentials("login:password")).thenReturn(new SmsBranaCzApiCredentials("login", "salt", "time", "auth"));
        when(apiService.sendSms(any(), any(), any())).thenReturn(null);

        ISmsResponse response = provider.sendSms("login:password", "+420123456789", "test message");
        assertErrorResponse(response, "No response from SMS service");
    }


    @Test
    void testSendSms_httpError() throws IOException {
        when(credentialsService.getCredentials("login:password")).thenReturn(new SmsBranaCzApiCredentials("login", "salt", "time", "auth"));
        HttpStatusIOException httpStatusIOException = createHttpStatusIOException();
        when(apiService.sendSms(any(), anyString(), anyString())).thenThrow(httpStatusIOException);

        ISmsResponse response = provider.sendSms("login:password", "+420123456789", "test message");
        assertErrorResponse(response, "HTTP error: 400");
    }

    @Test
    void testSendSms_ioException() throws IOException {
        when(credentialsService.getCredentials("login:password")).thenReturn(new SmsBranaCzApiCredentials("login", "salt", "time", "auth"));
        IOException ioException = createIOException();
        when(apiService.sendSms(any(), anyString(), anyString())).thenThrow(ioException);

        ISmsResponse response = provider.sendSms("login:password", "+420123456789", "test message");
        assertErrorResponse(response, "Connection error: Test IOException");
    }

    @Test
    void testSendSms_UnexpectedException() {
        when(credentialsService.getCredentials("login:password")).thenThrow(new RuntimeException("unexpected test exception"));

        ISmsResponse response = provider.sendSms("login:password", "+420123456789", "test message");
        assertErrorResponse(response, "Unexpected error: unexpected test exception");
    }

    private HttpStatusIOException createHttpStatusIOException() {
        HttpStatusIOException exception = mock(HttpStatusIOException.class);
        when(exception.getHttpStatusCode()).thenReturn(400);
        return exception;
    }

    private IOException createIOException() {
        IOException exception = mock(IOException.class);
        when(exception.getMessage()).thenReturn("Test IOException");
        return exception;
    }

    private static void assertErrorResponse(ISmsResponse response, String errorMessage) {
        assertNotNull(response);
        assertEquals(ISmsResponse.ResponseStatus.ERROR, response.getStatus());
        ISmsErrorResponse errorResponse = response.getErrorResponse();
        assertNotNull(errorResponse);
        assertEquals(errorMessage, errorResponse.getErrorMessage());
    }

    private String createXmlResponse() {
        return """
            <result>
              <err>0</err>
              <price>1.23</price>
              <sms_count>1</sms_count>
              <credit>123.45</credit>
              <sms_id>1234</sms_id>
            </result>
            """;
    }
}