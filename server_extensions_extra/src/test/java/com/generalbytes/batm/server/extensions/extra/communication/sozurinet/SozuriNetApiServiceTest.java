package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SozuriNetApiServiceTest {
    @Mock
    private ISozuriNetAPI api;
    @InjectMocks
    private SozuriNetApiService service;

    @Test
    void testSendSms() throws IOException {
        SozuriNetJsonResponse expectedResponse = new SozuriNetJsonResponse();
        when(api.sendSms(any(SozuriNetSmsRequest.class))).thenReturn(expectedResponse);

        SozuriNetJsonResponse response = service.sendSms(createCredentials(), "+254603572525", "some message");
        
        assertEquals(expectedResponse, response);

        ArgumentCaptor<SozuriNetSmsRequest> requestCaptor = ArgumentCaptor.forClass(SozuriNetSmsRequest.class);
        verify(api).sendSms(requestCaptor.capture());

        SozuriNetSmsRequest capturedRequest = requestCaptor.getValue();
        assertNotNull(capturedRequest);
        assertEquals("testProject", capturedRequest.getProject());
        assertEquals("testFrom", capturedRequest.getFrom());
        assertEquals("+254603572525", capturedRequest.getTo());
        assertEquals("testCampaign", capturedRequest.getCampaign());
        assertEquals("testChannel", capturedRequest.getChannel());
        assertEquals("testApiKey", capturedRequest.getApiKey());
        assertEquals("some message", capturedRequest.getMessage());
        assertEquals("promotional", capturedRequest.getType());
    }

    private SozuriNetApiCredentials createCredentials() {
        return new SozuriNetApiCredentials("testProject", "testFrom", "testCampaign", "testChannel", "testApiKey");
    }
}

