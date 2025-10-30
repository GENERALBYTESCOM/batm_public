package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SozuriNetProviderTest {
    @Mock
    private SozuriNetApiService apiService;
    @Mock
    private SozuriNetCredentialsService credentialsService;
    @InjectMocks
    private SozuriNetProvider provider;

    @Test
    void testGetName() {
        assertEquals("SozuriNet", provider.getName());
    }

    @Test
    void testGetPublicName() {
        assertEquals("SozuriNet", provider.getPublicName());
    }

    @Test
    void testSendSms_invalidCredentials() {
        when(credentialsService.getCredentials("credentials")).thenThrow(new SozuriNetValidationException("Invalid credentials format"));

        ISmsResponse response = provider.sendSms("credentials", null, null);

        assertErrorResponse(response, "Invalid credentials format");
    }

    @Test
    void testSendSms_success() throws IOException {
        SozuriNetApiCredentials apiCredentials = new SozuriNetApiCredentials("project", "from", "campaign", "channel", "apiKey");
        when(credentialsService.getCredentials("apiKey:project:from:campaign:channel")).thenReturn(apiCredentials);
        when(apiService.sendSms(apiCredentials, "+254603572525", "test message")).thenReturn(createSuccessJsonResponse());

        ISmsResponse response = provider.sendSms("apiKey:project:from:campaign:channel", "+254603572525", "test message");
        assertNotNull(response);
        assertEquals(ISmsResponse.ResponseStatus.OK, response.getStatus());
        assertNull(response.getErrorResponse());
        assertEquals("795ecc08996e79d491cce39913fbf11d2bfdb18d", response.getSid());
        assertNull(response.getPrice());
    }

    @Test
    void testSendSms_nullResponse() throws IOException {
        when(credentialsService.getCredentials("apiKey:project:from:campaign:channel")).thenReturn(new SozuriNetApiCredentials("project", "from", "campaign", "channel", "apiKey"));
        when(apiService.sendSms(any(), any(), any())).thenReturn(null);

        ISmsResponse response = provider.sendSms("apiKey:project:from:campaign:channel", "+254603572525", "test message");
        assertErrorResponse(response, "No response from SMS service");
    }

    @Test
    void testSendSms_httpError() throws IOException {
        when(credentialsService.getCredentials("apiKey:project:from:campaign:channel")).thenReturn(new SozuriNetApiCredentials("project", "from", "campaign", "channel", "apiKey"));
        HttpStatusIOException httpStatusIOException = createHttpStatusIOException();
        when(apiService.sendSms(any(), anyString(), anyString())).thenThrow(httpStatusIOException);

        ISmsResponse response = provider.sendSms("apiKey:project:from:campaign:channel", "+254603572525", "test message");
        assertErrorResponse(response, "HTTP error: 400");
    }

    @Test
    void testSendSms_ioException() throws IOException {
        when(credentialsService.getCredentials("apiKey:project:from:campaign:channel")).thenReturn(new SozuriNetApiCredentials("project", "from", "campaign", "channel", "apiKey"));
        IOException ioException = createIOException();
        when(apiService.sendSms(any(), anyString(), anyString())).thenThrow(ioException);

        ISmsResponse response = provider.sendSms("apiKey:project:from:campaign:channel", "+254603572525", "test message");
        assertErrorResponse(response, "Connection error: Test IOException");
    }

    @Test
    void testSendSms_UnexpectedException() {
        when(credentialsService.getCredentials("apiKey:project:from:campaign:channel")).thenThrow(new RuntimeException("unexpected test exception"));

        ISmsResponse response = provider.sendSms("apiKey:project:from:campaign:channel", "+254603572525", "test message");
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

    private SozuriNetJsonResponse createSuccessJsonResponse() {
        SozuriNetJsonResponse response = new SozuriNetJsonResponse();
        
        SozuriNetJsonResponse.MessageData messageData = new SozuriNetJsonResponse.MessageData();
        messageData.setMessages(1);
        response.setMessageData(messageData);

        SozuriNetJsonResponse.Recipient recipient = new SozuriNetJsonResponse.Recipient();
        recipient.setMessageId("795ecc08996e79d491cce39913fbf11d2bfdb18d");
        recipient.setTo("254603572525");
        recipient.setStatus("sent");
        recipient.setStatusCode("11");
        recipient.setBulkId("bulk69038c633ae6d2.861374281761840227");
        recipient.setMessagePart(1);
        recipient.setType("promotional");

        List<SozuriNetJsonResponse.Recipient> recipients = new ArrayList<>();
        recipients.add(recipient);
        response.setRecipients(recipients);

        return response;
    }
}

