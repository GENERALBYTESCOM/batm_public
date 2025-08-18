package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrWebhookHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GtrWebhookHandlerServiceTest {

    private static final int FAKE_CALLBACK_TYPE = 21;
    private GtrWebhookHandlerService handler;

    @BeforeEach
    void setUp() {
        handler = GtrWebhookHandlerService.getInstance();
    }

    @Test
    void testGetHandler() {
        // Handler not registered
        assertNull(handler.getHandler(FAKE_CALLBACK_TYPE));

        // Handler registered
        GtrWebhookHandler requestHandler = mock(GtrWebhookHandler.class);

        handler.registerHandler(FAKE_CALLBACK_TYPE, requestHandler);

        GtrWebhookHandler actualHandler = handler.getHandler(FAKE_CALLBACK_TYPE);

        assertNotNull(actualHandler);
        assertEquals(requestHandler, actualHandler);
    }

}