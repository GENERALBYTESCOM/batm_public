package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SumsubWebhookRestServiceTest {

    @Mock
    private SumsubTransferHandler transferHandler;
    private SumsubWebhookRestService webhookRestService;

    @BeforeEach
    void init() {
        try (MockedStatic<SumsubTransferHandler> transferHandlerMock = mockStatic(SumsubTransferHandler.class)) {
            transferHandlerMock.when(SumsubTransferHandler::getInstance).thenReturn(transferHandler);
            webhookRestService = new SumsubWebhookRestService();
        }
    }

    @Test
    void testGetPrefixPath() {
        String prefixPath = webhookRestService.getPrefixPath();

        assertEquals("sumsub", prefixPath);
        verifyNoInteractions(transferHandler);
    }

    @Test
    void testGetImplementation() {
        Class<?> implementation = webhookRestService.getImplementation();

        assertEquals(SumsubWebhookRestService.class, implementation);
    }

    @Test
    void testHandleWebhookMessage() {
        assertDoesNotThrow(() -> webhookRestService.handleWebhookMessage("vasp_did", "digest_algorithm", "payload_digest", "message"));

        ArgumentCaptor<SumsubWebhookRequest> requestCaptor = ArgumentCaptor.forClass(SumsubWebhookRequest.class);

        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(transferHandler, times(1)).handleIncomingMessage(requestCaptor.capture()));

        SumsubWebhookRequest request = requestCaptor.getValue();
        assertEquals("vasp_did", request.vaspDid());
        assertEquals("digest_algorithm", request.digestAlgorithm());
        assertEquals("payload_digest", request.payloadDigest());
        assertEquals("message", request.message());
    }

    private static Stream<Arguments> exception_arguments() {
        return Stream.of(
                arguments(new RuntimeException("test-handle-webhook-message-runtime-exception")),
                arguments(new TravelRuleProviderException("test-handle-webhook-message-travel-rule-provider-exception"))
        );
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testHandleWebhookMessage_handlerException(Exception thrownException) {
        doThrow(thrownException).when(transferHandler).handleIncomingMessage(any());

        assertDoesNotThrow(() -> webhookRestService.handleWebhookMessage("vasp_did", "digest_algorithm", "payload_digest", "message"));

        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(transferHandler, times(1)).handleIncomingMessage(any()));
    }

}