package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferStatusUpdateEvent;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.SumsubProvider;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.SumsubProviderRegistry;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.mapper.SumsubTravelRuleApiMapper;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * The class responsible for handling incoming webhook messages from Sumsub.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SumsubTransferHandler {

    private static final SumsubTransferHandler INSTANCE = new SumsubTransferHandler();

    private SumsubProviderRegistry providerRegistry;
    private SumsubWebhookValidator webhookValidator;
    private ObjectMapper objectMapper;
    private ITravelRuleTransferListener transferListener;

    /**
     * Initializes a singleton.
     *
     * @param providerRegistry {@link SumsubProviderRegistry}
     */
    public void init(SumsubProviderRegistry providerRegistry, SumsubWebhookValidator webhookValidator, ObjectMapper objectMapper) {
        this.providerRegistry = providerRegistry;
        this.webhookValidator = webhookValidator;
        this.objectMapper = objectMapper;
    }

    /**
     * Get the instance of {@link SumsubTransferHandler}.
     *
     * @return The instance.
     */
    public static SumsubTransferHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the transfer listener.
     *
     * @param transferListener {@link ITravelRuleTransferListener}
     * @return Always {@code true}.
     */
    public boolean registerTransferListener(ITravelRuleTransferListener transferListener) {
        this.transferListener = transferListener;
        return true;
    }

    /**
     * Handles incoming webhook messages from Sumsub Travel Rule for transaction monitoring.
     *
     * @param webhookRequest The incoming webhook request.
     */
    public void handleIncomingMessage(SumsubWebhookRequest webhookRequest) {
        validateTransferHandlerInitialization();

        SumsubProvider provider = getSumsubProvider(webhookRequest.vaspDid());
        String secretKey = provider.getCredentials().getPrivateKey();

        webhookValidator.validateSignature(webhookRequest, secretKey);

        SumsubWebhookMessage message = deserializeWebhookMessage(webhookRequest.message());
        webhookValidator.validateSumsubWebhookMessage(message);

        switch (message.getType()) {
            case SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_TXN_APPROVED -> handleTransferApproved(message);
            case SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_TXN_REJECTED -> handleTransferRejected(message);
            case SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_ON_HOLD -> handleTransferOnHold(provider, message);
            default -> log.debug("Sumsub incoming message contain unsupported type: {}, txn ID: {}, Sumsub transaction ID: {}",
                    message.getType(), message.getKytDataTxnId(), message.getKytTxnId());
        }
    }

    private void validateTransferHandlerInitialization() {
        if (providerRegistry == null || webhookValidator == null || objectMapper == null) {
            throw new TravelRuleProviderException("Sumsub Transfer Handler is not initialized yet.");
        }

        if (transferListener == null) {
            throw new TravelRuleProviderException(
                    "Sumsub transfer listener is not registered. Check if you have created a Travel Rule Setting for Sumsub."
            );
        }
    }

    private SumsubProvider getSumsubProvider(String vaspDid) {
        if (StringUtils.isBlank(vaspDid)) {
            throw new TravelRuleProviderException(
                    "VASP DID for 'transfer on hold' is blank. Check HTTP header settings in Sumsub Webhook Manager."
            );
        }

        SumsubProvider provider = providerRegistry.get(vaspDid);
        if (provider == null) {
            throw new TravelRuleProviderException("Sumsub provider with VASP DID '" + vaspDid + "' not found.");
        }

        return provider;
    }

    private SumsubWebhookMessage deserializeWebhookMessage(String webhookMessage) {
        try {
            return objectMapper.readValue(webhookMessage, SumsubWebhookMessage.class);
        } catch (JsonProcessingException e) {
            throw new TravelRuleProviderException(
                    "Failed to deserialize Sumsub webhook message from a JSON string. Received message: " + webhookMessage
            );
        }
    }

    private void handleTransferApproved(SumsubWebhookMessage message) {
        ITravelRuleTransferStatusUpdateEvent statusUpdateEvent = createUpdateEvent(
                message.getKytDataTxnId(), TravelRuleProviderTransferStatus.APPROVED
        );
        transferListener.onTransferStatusUpdate(statusUpdateEvent);
    }

    private void handleTransferRejected(SumsubWebhookMessage message) {
        ITravelRuleTransferStatusUpdateEvent statusUpdateEvent = createUpdateEvent(
                message.getKytDataTxnId(), TravelRuleProviderTransferStatus.REJECTED
        );
        transferListener.onTransferStatusUpdate(statusUpdateEvent);
    }

    private void handleTransferOnHold(SumsubProvider provider, SumsubWebhookMessage message) {
        SumsubTransactionInformationResponse response = provider.getTransactionInformation(message.getKytTxnId());
        webhookValidator.validateTransactionInformationResponse(response, message);

        ITravelRuleIncomingTransferEvent incomingTransferEvent
                = SumsubTravelRuleApiMapper.toITravelRuleIncomingTransferEvent(message, response);

        transferListener.onIncomingTransferReceived(incomingTransferEvent);
    }

    private ITravelRuleTransferStatusUpdateEvent createUpdateEvent(String transferPublicId,
                                                                   TravelRuleProviderTransferStatus transferStatus
    ) {
        return new ITravelRuleTransferStatusUpdateEvent() {
            @Override
            public String getTransferPublicId() {
                return transferPublicId;
            }

            @Override
            public TravelRuleProviderTransferStatus getNewTransferStatus() {
                return transferStatus;
            }
        };
    }

}
