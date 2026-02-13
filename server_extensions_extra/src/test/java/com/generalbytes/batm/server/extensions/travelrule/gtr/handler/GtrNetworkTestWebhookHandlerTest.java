package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GtrNetworkTestWebhookHandlerTest {

    private final GtrNetworkTestWebhookHandler handler = new GtrNetworkTestWebhookHandler();

    @Test
    void testHandle() {
        GtrWebhookMessage message = mock(GtrWebhookMessage.class);

        GtrWebhookMessageResponse response = handler.handle(message);

        assertEquals("Network Test Successful", response.getVerifyMessage());
        assertEquals(100000, response.getVerifyStatus());
    }

}