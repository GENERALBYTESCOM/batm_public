package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrReceiveTxIdWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GtrReceiveTxIdWebhookHandlerTest {

    private final GtrReceiveTxIdWebhookHandler handler = new GtrReceiveTxIdWebhookHandler();

    @Test
    void testHandle() {
        GtrReceiveTxIdWebhookPayload callbackData = mock(GtrReceiveTxIdWebhookPayload.class);

        GtrWebhookMessage message = mock(GtrWebhookMessage.class);
        when(message.getCallbackData()).thenReturn(callbackData);

        GtrWebhookMessageResponse response = handler.handle(message);

        assertEquals("TX ID Received", response.getVerifyMessage());
        assertEquals(100000, response.getVerifyStatus());
    }

}