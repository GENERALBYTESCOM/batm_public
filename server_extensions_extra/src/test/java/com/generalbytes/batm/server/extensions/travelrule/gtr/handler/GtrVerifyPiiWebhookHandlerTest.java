package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.gtr.GtrValidator;
import com.generalbytes.batm.server.extensions.travelrule.gtr.GtrVerifyPiiService;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrVerifyPiiWebhookHandlerTest {

    @Mock
    private GtrVerifyPiiService verifyPiiService;
    @Mock
    private GtrValidator validator;
    @InjectMocks
    private GtrVerifyPiiWebhookHandler verifyPiiWebhookHandler;

    private static Stream<Arguments> testHandle_arguments() {
        return Stream.of(
                arguments(TravelRuleProviderTransferStatus.APPROVED, "Verification Success"),
                arguments(TravelRuleProviderTransferStatus.IN_PROGRESS, "Verification Failed"),
                arguments(TravelRuleProviderTransferStatus.REJECTED, "Verification Failed")
        );
    }

    @ParameterizedTest
    @MethodSource("testHandle_arguments")
    void testHandle(TravelRuleProviderTransferStatus resolvedTransferStatus, String expectedVerifyMessage) throws InterruptedException {
        CountDownLatch latchTest = new CountDownLatch(1);

        Thread thread1 = new Thread(() -> {
            GtrPiiVerifyWebhookPayload callbackData = mock(GtrPiiVerifyWebhookPayload.class);
            when(callbackData.getRequestId()).thenReturn("request_id");

            GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);
            when(webhookMessage.getCallbackData()).thenReturn(callbackData);

            GtrWebhookMessageResponse response = verifyPiiWebhookHandler.handle(webhookMessage);

            assertEquals(expectedVerifyMessage, response.getVerifyMessage());
            verify(validator, times(1)).validateGtrWebhookMessage(webhookMessage);
            verify(validator, times(1)).validateVerifyPiiIncomingMessage(callbackData);
            verify(verifyPiiService, times(1)).processVerifyPiiWebhookMessage(webhookMessage, callbackData);

            latchTest.countDown();
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);
            when(event.getTransferExternalId()).thenReturn("request_id");
            when(event.getResolvedStatus()).thenReturn(resolvedTransferStatus);

            await()
                    .atMost(1, TimeUnit.SECONDS)
                    .until(() -> thread1.getState() == Thread.State.TIMED_WAITING);

            boolean success = verifyPiiWebhookHandler.onTransferResolved(event);

            assertTrue(success);
        });
        thread2.start();

        if (!latchTest.await(2, TimeUnit.SECONDS)) {
            fail("Test timed out waiting for PII verification");
        }

        thread1.join();
        thread2.join();
    }

    @Test
    void testHandle_timeoutException() {
        GtrPiiVerifyWebhookPayload callbackData = mock(GtrPiiVerifyWebhookPayload.class);
        when(callbackData.getRequestId()).thenReturn("request_id");

        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);
        when(webhookMessage.getCallbackData()).thenReturn(callbackData);

        GtrWebhookMessageResponse response = verifyPiiWebhookHandler.handle(webhookMessage);

        assertEquals("Verification Failed", response.getVerifyMessage());
        verify(validator, times(1)).validateGtrWebhookMessage(webhookMessage);
        verify(validator, times(1)).validateVerifyPiiIncomingMessage(callbackData);
        verify(verifyPiiService, times(1)).processVerifyPiiWebhookMessage(webhookMessage, callbackData);
    }

    @Test
    void testHandle_validatorException_message() {
        GtrPiiVerifyWebhookPayload callbackData = mock(GtrPiiVerifyWebhookPayload.class);
        when(callbackData.getRequestId()).thenReturn("request_id");

        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);
        when(webhookMessage.getCallbackData()).thenReturn(callbackData);

        doThrow(new TravelRuleProviderException("test-handle-pii-verification-validator-message-exception"))
                .when(validator).validateGtrWebhookMessage(webhookMessage);

        GtrWebhookMessageResponse response = verifyPiiWebhookHandler.handle(webhookMessage);

        assertEquals("Verification Failed", response.getVerifyMessage());
        verify(validator, times(1)).validateGtrWebhookMessage(webhookMessage);
        verify(validator, never()).validateVerifyPiiIncomingMessage(callbackData);
        verifyNoInteractions(verifyPiiService);
    }

    @Test
    void testHandle_validatorException_payload() {
        GtrPiiVerifyWebhookPayload callbackData = mock(GtrPiiVerifyWebhookPayload.class);
        when(callbackData.getRequestId()).thenReturn("request_id");

        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);
        when(webhookMessage.getCallbackData()).thenReturn(callbackData);

        doThrow(new TravelRuleProviderException("test-handle-pii-verification-validator-payload-exception"))
                .when(validator).validateVerifyPiiIncomingMessage(callbackData);

        GtrWebhookMessageResponse response = verifyPiiWebhookHandler.handle(webhookMessage);

        assertEquals("Verification Failed", response.getVerifyMessage());
        verify(validator, times(1)).validateGtrWebhookMessage(webhookMessage);
        verify(validator, times(1)).validateVerifyPiiIncomingMessage(callbackData);
        verifyNoInteractions(verifyPiiService);
    }

    @Test
    void testHandle_exception() {
        GtrPiiVerifyWebhookPayload callbackData = mock(GtrPiiVerifyWebhookPayload.class);
        when(callbackData.getRequestId()).thenReturn("request_id");

        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);
        when(webhookMessage.getCallbackData()).thenReturn(callbackData);

        doThrow(new RuntimeException("test-handle-pii-verification-runtime-exception"))
                .when(verifyPiiService).processVerifyPiiWebhookMessage(webhookMessage, callbackData);

        GtrWebhookMessageResponse response = verifyPiiWebhookHandler.handle(webhookMessage);

        assertEquals("Verification Failed", response.getVerifyMessage());
        verify(validator, times(1)).validateGtrWebhookMessage(webhookMessage);
        verify(validator, times(1)).validateVerifyPiiIncomingMessage(callbackData);
        verify(verifyPiiService, times(1)).processVerifyPiiWebhookMessage(webhookMessage, callbackData);
    }

    @Test
    void testOnTransferResolved_handleWasNotCalled() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);
        when(event.getTransferExternalId()).thenReturn("external_id");

        boolean result = verifyPiiWebhookHandler.onTransferResolved(event);

        assertFalse(result);
    }

}