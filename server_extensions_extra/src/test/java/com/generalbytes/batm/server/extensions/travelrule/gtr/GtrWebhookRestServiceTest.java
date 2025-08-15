package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrWebhookHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrWebhookRestServiceTest {

    private static final int FAKE_CALLBACK_TYPE = 21;
    @Mock
    private GtrWebhookHandlerService handlerService;
    private GtrWebhookRestService service;

    @BeforeEach
    void setUp() {
        try (MockedStatic<GtrWebhookHandlerService> mockedHandlerService = mockStatic(GtrWebhookHandlerService.class)) {
            mockedHandlerService.when(GtrWebhookHandlerService::getInstance).thenReturn(handlerService);

            service = new GtrWebhookRestService();
        }
    }

    @Test
    void testGetPrefixPath() {
        assertEquals("com/generalbytes/batm/server/extensions/travelrule/gtr", service.getPrefixPath());
    }

    @Test
    void testGetImplementation() {
        assertEquals(GtrWebhookRestService.class, service.getImplementation());
    }

    @Test
    void testRequest_noHandler() {
        GtrWebhookMessage message = new GtrWebhookMessage();
        message.setCallbackType(FAKE_CALLBACK_TYPE);

        when(handlerService.getHandler(FAKE_CALLBACK_TYPE)).thenReturn(null);

        GtrWebhookMessageResponse response = service.handleWebhookMessage(message);

        assertNotNull(response);
        assertEquals("unknown callbackType", response.getVerifyMessage());
        assertEquals(GtrApiConstants.VerifyStatus.CLIENT_BAD_PARAMETERS, response.getVerifyStatus());
    }

    @Test
    void testRequest_handlerThrowsException() {
        GtrWebhookMessage message = new GtrWebhookMessage();
        message.setCallbackType(FAKE_CALLBACK_TYPE);
        GtrWebhookHandler requestHandler = mock(GtrWebhookHandler.class);

        when(handlerService.getHandler(FAKE_CALLBACK_TYPE)).thenReturn(requestHandler);
        when(requestHandler.handle(message)).thenThrow(new RuntimeException("Test Exception"));

        GtrWebhookMessageResponse response = service.handleWebhookMessage(message);

        assertNotNull(response);
        assertEquals("unexpected error occurred", response.getVerifyMessage());
        assertEquals(GtrApiConstants.VerifyStatus.BENEFICIARY_INTERNAL_SERVER_ERROR, response.getVerifyStatus());
    }

    @Test
    void testRequest() {
        GtrWebhookMessage message = new GtrWebhookMessage();
        message.setCallbackType(FAKE_CALLBACK_TYPE);
        GtrWebhookMessageResponse expectedResponse = new GtrWebhookMessageResponse();
        GtrWebhookHandler requestHandler = mock(GtrWebhookHandler.class);

        when(handlerService.getHandler(FAKE_CALLBACK_TYPE)).thenReturn(requestHandler);
        when(requestHandler.handle(message)).thenReturn(expectedResponse);

        GtrWebhookMessageResponse response = service.handleWebhookMessage(message);

        assertEquals(expectedResponse, response);
        verify(requestHandler).handle(message);
    }
}