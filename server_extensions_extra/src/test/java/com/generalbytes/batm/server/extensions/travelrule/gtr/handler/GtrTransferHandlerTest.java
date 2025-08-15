package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferStatusUpdateEvent;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrBeneficiary;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrOriginator;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrTransferHandlerTest {

    @Mock
    private ITravelRuleTransferListener transferListener;
    private GtrTransferHandler transferHandler;

    @BeforeEach
    void init() {
        transferHandler = new GtrTransferHandler();
        transferHandler.registerTransferListener(transferListener);
    }

    @Test
    void testHandleVerifyPiiResponse_approved() {
        GtrVerifyPiiResponse verifyPiiResponse = mock(GtrVerifyPiiResponse.class);
        when(verifyPiiResponse.isSuccess()).thenReturn(true);

        transferHandler.handleVerifyPiiResponse("transfer_public_id", verifyPiiResponse);

        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    assertUpdateEvent(TravelRuleProviderTransferStatus.APPROVED);
                    verify(verifyPiiResponse, times(1)).getRequestId();
                });
    }

    @Test
    void testHandleVerifyPiiResponse_rejected() {
        GtrVerifyPiiResponse verifyPiiResponse = mock(GtrVerifyPiiResponse.class);
        when(verifyPiiResponse.isSuccess()).thenReturn(false);

        transferHandler.handleVerifyPiiResponse("transfer_public_id", verifyPiiResponse);

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            assertUpdateEvent(TravelRuleProviderTransferStatus.REJECTED);
            verify(verifyPiiResponse, times(1)).getRequestId();
            verify(verifyPiiResponse, times(1)).getVerifyStatus();
            verify(verifyPiiResponse, times(1)).getVerifyMessage();
        });
    }

    private void assertUpdateEvent(TravelRuleProviderTransferStatus expectedStatus) {
        ArgumentCaptor<ITravelRuleTransferStatusUpdateEvent> eventCaptor
                = ArgumentCaptor.forClass(ITravelRuleTransferStatusUpdateEvent.class);
        verify(transferListener).onTransferStatusUpdate(eventCaptor.capture());
        ITravelRuleTransferStatusUpdateEvent event = eventCaptor.getValue();

        assertEquals("transfer_public_id", event.getTransferPublicId());
        assertEquals(expectedStatus, event.getNewTransferStatus());
    }

    @Test
    void testHandleVerifyPiiResponse_listenerNotInitialized() {
        transferHandler.registerTransferListener(null);

        GtrVerifyPiiResponse verifyPiiResponse = mock(GtrVerifyPiiResponse.class);

        transferHandler.handleVerifyPiiResponse("transfer_public_id", verifyPiiResponse);

        verify(verifyPiiResponse, times(1)).getRequestId();
        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> verifyNoInteractions(transferListener));
    }

    @Test
    void testHandleVerifyPiiWebhookMessage() {
        GtrPiiVerifyWebhookPayload callbackData = createGtrPiiVerifyWebhookPayload();
        GtrIvms101Payload ivms101 = createGtrIvms101Payload();
        String rawData = "raw_data";

        transferHandler.handleVerifyPiiWebhookMessage(callbackData, ivms101, rawData);

        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(this::assertIncomingTransferEvent);
    }

    @Test
    void testHandleVerifyPiiWebhookMessage_transferHandler_notRegistered() {
        GtrPiiVerifyWebhookPayload callbackData = createGtrPiiVerifyWebhookPayload();
        GtrIvms101Payload ivms101 = createGtrIvms101Payload();
        String rawData = "raw_data";

        transferHandler.registerTransferListener(null);
        transferHandler.handleVerifyPiiWebhookMessage(callbackData, ivms101, rawData);

        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(transferListener, never()).onIncomingTransferReceived(any()));
    }

    private void assertIncomingTransferEvent() {
        ArgumentCaptor<ITravelRuleIncomingTransferEvent> eventCaptor
                = ArgumentCaptor.forClass(ITravelRuleIncomingTransferEvent.class);
        verify(transferListener).onIncomingTransferReceived(eventCaptor.capture());
        ITravelRuleIncomingTransferEvent event = eventCaptor.getValue();

        assertEquals("request_id", event.getId());
    }

    private GtrPiiVerifyWebhookPayload createGtrPiiVerifyWebhookPayload() {
        GtrPiiVerifyWebhookPayload payload = new GtrPiiVerifyWebhookPayload();
        payload.setRequestId("request_id");

        return payload;
    }

    private GtrIvms101Payload createGtrIvms101Payload() {
        GtrPerson originatorPerson = new GtrPerson();
        GtrPerson beneficiaryPerson = new GtrPerson();

        GtrOriginator originator = new GtrOriginator();
        originator.setOriginatorPersons(List.of(originatorPerson));

        GtrBeneficiary beneficiary = new GtrBeneficiary();
        beneficiary.setBeneficiaryPersons(List.of(beneficiaryPerson));

        GtrIvms101 ivms101 = new GtrIvms101();
        ivms101.setOriginator(originator);
        ivms101.setBeneficiary(beneficiary);

        GtrIvms101Payload payload = new GtrIvms101Payload();
        payload.setIvms101(ivms101);

        return payload;
    }

}