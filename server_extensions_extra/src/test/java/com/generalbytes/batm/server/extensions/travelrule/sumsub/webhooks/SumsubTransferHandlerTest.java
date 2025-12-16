package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferStatusUpdateEvent;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.SumsubProvider;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.SumsubProviderRegistry;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubIdentity;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubInstitutionInfo;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubPaymentMethod;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.mapper.SumsubTravelRuleApiMapper;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumsubTransferHandlerTest {

    @Mock
    private SumsubProviderRegistry providerRegistry;
    @Mock
    private SumsubWebhookValidator webhookValidator;
    @Mock
    private ObjectMapper objectMapper;
    private final SumsubTransferHandler transferHandler = SumsubTransferHandler.getInstance();

    @BeforeEach
    void init() {
        transferHandler.init(providerRegistry, webhookValidator, objectMapper);
    }

    @Test
    void testRegisterTransferListener() {
        ITravelRuleTransferListener transferListener = mock(ITravelRuleTransferListener.class);

        boolean result = transferHandler.registerTransferListener(transferListener);

        assertTrue(result);
        verifyNoInteractions(providerRegistry, webhookValidator, objectMapper);
    }

    private static Stream<Arguments> testHandleIncomingMessage_approved_and_rejected_arguments() {
        return Stream.of(
                arguments(SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_TXN_APPROVED, TravelRuleProviderTransferStatus.APPROVED),
                arguments(SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_TXN_REJECTED, TravelRuleProviderTransferStatus.REJECTED)
        );
    }

    @ParameterizedTest
    @MethodSource("testHandleIncomingMessage_approved_and_rejected_arguments")
    void testHandleIncomingMessage_approved_and_rejected(String webhookType,
                                                         TravelRuleProviderTransferStatus expectedTransferStatus
    ) throws Exception {
        SumsubProvider provider = createMockedSumsubProvider();

        when(providerRegistry.get("vasp_did")).thenReturn(provider);

        SumsubWebhookMessage message = mock(SumsubWebhookMessage.class);
        when(message.getType()).thenReturn(webhookType);
        when(message.getKytDataTxnId()).thenReturn("transferPublicId");

        when(objectMapper.readValue("message", SumsubWebhookMessage.class)).thenReturn(message);

        ITravelRuleTransferListener transferListener = mock(ITravelRuleTransferListener.class);
        transferHandler.registerTransferListener(transferListener);

        SumsubWebhookRequest webhookRequest = createSumsubWebhookRequest();
        assertDoesNotThrow(() -> transferHandler.handleIncomingMessage(webhookRequest));

        ArgumentCaptor<ITravelRuleTransferStatusUpdateEvent> eventCaptor
                = ArgumentCaptor.forClass(ITravelRuleTransferStatusUpdateEvent.class);
        verify(transferListener).onTransferStatusUpdate(eventCaptor.capture());
        ITravelRuleTransferStatusUpdateEvent event = eventCaptor.getValue();

        assertEquals("transferPublicId", event.getTransferPublicId());
        assertEquals(expectedTransferStatus, event.getNewTransferStatus());
        verify(webhookValidator, times(1)).validateSignature(webhookRequest, "private_key");
        verify(webhookValidator, times(1)).validateSumsubWebhookMessage(message);
    }

    @Test
    void testHandleIncomingMessage_onHold() throws Exception {
        try (MockedStatic<SumsubTravelRuleApiMapper> apiMapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            SumsubProvider sumsubProvider = createMockedSumsubProvider();
            when(providerRegistry.get("vasp_did")).thenReturn(sumsubProvider);

            SumsubWebhookMessage message = mock(SumsubWebhookMessage.class);
            when(message.getType()).thenReturn(SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_ON_HOLD);
            when(message.getKytTxnId()).thenReturn("sumsub_id");

            when(objectMapper.readValue("message", SumsubWebhookMessage.class)).thenReturn(message);

            SumsubTransactionInformationResponse response = createSumsubTransactionInformationResponse();
            when(sumsubProvider.getTransactionInformation("sumsub_id")).thenReturn(response);

            ITravelRuleIncomingTransferEvent transferEvent = mock(ITravelRuleIncomingTransferEvent.class);
            apiMapperMock.when(() -> SumsubTravelRuleApiMapper.toITravelRuleIncomingTransferEvent(message, response))
                    .thenReturn(transferEvent);

            ITravelRuleTransferListener transferListener = mock(ITravelRuleTransferListener.class);
            transferHandler.registerTransferListener(transferListener);

            SumsubWebhookRequest webhookRequest = createSumsubWebhookRequest();
            transferHandler.handleIncomingMessage(webhookRequest);

            verify(transferListener, times(1)).onIncomingTransferReceived(transferEvent);
            verify(webhookValidator, times(1)).validateSignature(webhookRequest, "private_key");
            verify(webhookValidator, times(1)).validateSumsubWebhookMessage(message);
            verify(webhookValidator, times(1)).validateTransactionInformationResponse(response, message);
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testHandleIncomingMessage_vaspDid_blank(String vaspDid) {
        SumsubWebhookRequest webhookRequest = createSumsubWebhookRequest(vaspDid);

        ITravelRuleTransferListener transferListener = mock(ITravelRuleTransferListener.class);
        transferHandler.registerTransferListener(transferListener);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> transferHandler.handleIncomingMessage(webhookRequest)
        );

        assertEquals(
                "VASP DID for 'transfer on hold' is blank. Check HTTP header settings in Sumsub Webhook Manager.", exception.getMessage()
        );
        verifyNoInteractions(providerRegistry, webhookValidator, objectMapper, transferListener);
    }

    @Test
    void testHandleIncomingMessage_provider_notFound() {
        SumsubWebhookRequest webhookRequest = createSumsubWebhookRequest();

        ITravelRuleTransferListener transferListener = mock(ITravelRuleTransferListener.class);
        transferHandler.registerTransferListener(transferListener);

        when(providerRegistry.get("vasp_did")).thenReturn(null);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> transferHandler.handleIncomingMessage(webhookRequest)
        );

        assertEquals("Sumsub provider with VASP DID 'vasp_did' not found.", exception.getMessage());
        verifyNoInteractions(webhookValidator, objectMapper, transferListener);
    }

    @Test
    void testHandleIncomingMessage_messageDeserializationException() throws Exception {
        SumsubProvider provider = createMockedSumsubProvider();

        when(providerRegistry.get("vasp_did")).thenReturn(provider);

        when(objectMapper.readValue("message", SumsubWebhookMessage.class))
                .thenThrow(new JsonProcessingException("test-handle-incoming-message-deserialization-exception") {});

        ITravelRuleTransferListener transferListener = mock(ITravelRuleTransferListener.class);
        transferHandler.registerTransferListener(transferListener);

        SumsubWebhookRequest webhookRequest = createSumsubWebhookRequest();
        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> transferHandler.handleIncomingMessage(webhookRequest)
        );

        assertEquals("Failed to deserialize Sumsub webhook message from a JSON string. Received message: message", exception.getMessage());
        verify(webhookValidator, times(1)).validateSignature(webhookRequest, "private_key");
        verifyNoInteractions(transferListener);
    }

    @Test
    void testHandleIncomingMessage_transferListenerNotRegistered() {
        transferHandler.registerTransferListener(null);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> transferHandler.handleIncomingMessage(null)
        );

        assertEquals("Sumsub transfer listener is not registered. Check if you have created a Travel Rule Setting for Sumsub.",
                exception.getMessage());
        verifyNoInteractions(providerRegistry, webhookValidator, objectMapper);
    }

    private static Stream<Arguments> testHandleIncomingMessage_handlerNotInitialized_arguments() {
        return Stream.of(
                arguments(null, null, null),
                arguments(new SumsubProviderRegistry(), null, null),
                arguments(null, new SumsubWebhookValidator(), null),
                arguments(null, null, new ObjectMapper()),
                arguments(null, new SumsubWebhookValidator(), new ObjectMapper()),
                arguments(new SumsubProviderRegistry(), null, new ObjectMapper()),
                arguments(new SumsubProviderRegistry(), new SumsubWebhookValidator(), null)
        );
    }

    @ParameterizedTest
    @MethodSource("testHandleIncomingMessage_handlerNotInitialized_arguments")
    void testHandleIncomingMessage_handlerNotInitialized(SumsubProviderRegistry providerRegistry,
                                                         SumsubWebhookValidator webhookValidator,
                                                         ObjectMapper objectMapper
    ) {
        transferHandler.init(providerRegistry, webhookValidator, objectMapper);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> transferHandler.handleIncomingMessage(null)
        );

        assertEquals("Sumsub Transfer Handler is not initialized yet.", exception.getMessage());
    }

    private static SumsubTransactionInformationResponse createSumsubTransactionInformationResponse() {
        SumsubIdentity applicant = new SumsubIdentity();
        applicant.setInstitutionInfo(new SumsubInstitutionInfo());
        applicant.setPaymentMethod(new SumsubPaymentMethod());

        SumsubIdentity counterparty = new SumsubIdentity();
        counterparty.setInstitutionInfo(new SumsubInstitutionInfo());

        SumsubTransactionInformationResponse.TransactionData transactionData = new SumsubTransactionInformationResponse.TransactionData();
        transactionData.setApplicant(applicant);
        transactionData.setCounterparty(counterparty);

        SumsubTransactionInformationResponse response = new SumsubTransactionInformationResponse();
        response.setData(transactionData);

        return response;
    }

    private SumsubWebhookRequest createSumsubWebhookRequest() {
        return createSumsubWebhookRequest("vasp_did");
    }

    private SumsubWebhookRequest createSumsubWebhookRequest(String vaspDid) {
        return new SumsubWebhookRequest(vaspDid, "digest_algorithm", "payload_digest", "message");
    }

    private SumsubProvider createMockedSumsubProvider() {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        when(credentials.getPrivateKey()).thenReturn("private_key");

        SumsubProvider provider = mock(SumsubProvider.class);
        when(provider.getCredentials()).thenReturn(credentials);

        return provider;
    }

}