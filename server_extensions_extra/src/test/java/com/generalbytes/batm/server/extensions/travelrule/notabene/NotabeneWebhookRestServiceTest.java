package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneWebhookMessagePayload;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class NotabeneWebhookRestServiceTest {

    private NotabeneWebhookRestService notabeneWebhookRestService;

    @Test
    void testGetPrefixPath() {
        notabeneWebhookRestService = new NotabeneWebhookRestService();
        String prefixPath = notabeneWebhookRestService.getPrefixPath();
        assertEquals("notabene", prefixPath);
    }

    @Test
    void testGetImplementation() {
        notabeneWebhookRestService = new NotabeneWebhookRestService();
        Class implementation = notabeneWebhookRestService.getImplementation();
        assertEquals(NotabeneWebhookRestService.class, implementation);
    }

    @Test
    void testHandleWebhookMessage() {
        NotabeneWebhookMessage message = new NotabeneWebhookMessage();
        message.setMessage(NotabeneWebhookMessage.TYPE_TRANSACTION_UPDATED);
        NotabeneWebhookMessagePayload payload = new NotabeneWebhookMessagePayload();
        NotabeneTransferInfo transferInfo = new NotabeneTransferInfo();
        payload.setTransaction(transferInfo);
        message.setPayload(payload);

        NotabeneTransferPublisher transferPublisher = mock(NotabeneTransferPublisher.class);

        try (MockedStatic<NotabeneTransferPublisher> transferPublisherMock = mockStatic(NotabeneTransferPublisher.class)) {
            transferPublisherMock.when(NotabeneTransferPublisher::getInstance).thenReturn(transferPublisher);
            notabeneWebhookRestService = new NotabeneWebhookRestService();
            notabeneWebhookRestService.handleWebhookMessage(message);
            verify(transferPublisher).publishEvent(transferInfo);
        }
    }

    @Test
    void testHandleWebhookMessage_notUpdate() {
        NotabeneWebhookMessage message = new NotabeneWebhookMessage();
        message.setMessage("different message");

        NotabeneTransferPublisher transferPublisher = mock(NotabeneTransferPublisher.class);

        try (MockedStatic<NotabeneTransferPublisher> transferPublisherMock = mockStatic(NotabeneTransferPublisher.class)) {
            transferPublisherMock.when(NotabeneTransferPublisher::getInstance).thenReturn(transferPublisher);
            notabeneWebhookRestService = new NotabeneWebhookRestService();
            notabeneWebhookRestService.handleWebhookMessage(message);
            verify(transferPublisher, never()).publishEvent(any());
        }
    }
}