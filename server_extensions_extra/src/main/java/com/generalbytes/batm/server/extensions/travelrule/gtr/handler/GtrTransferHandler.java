package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferStatusUpdateEvent;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrVerifyPiiMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for GTR transfer.
 */
@Slf4j
@NoArgsConstructor
public class GtrTransferHandler {

    private ITravelRuleTransferListener transferListener;

    /**
     * Registers a transfer listener.
     *
     * @param transferListener {@link ITravelRuleTransferListener}
     */
    public void registerTransferListener(ITravelRuleTransferListener transferListener) {
        this.transferListener = transferListener;
    }

    /**
     * Processes the PII verification response and notifies the transfer listener about the status change (verification result).
     *
     * @param transferPublicId  Public ID of transfer.
     * @param verifyPiiResponse {@link GtrVerifyPiiResponse}
     */
    public void handleVerifyPiiResponse(String transferPublicId, GtrVerifyPiiResponse verifyPiiResponse) {
        if (transferListener == null) {
            log.error("GTR transfer listener is not initialized. Transfer status has not been updated. Request ID: {}",
                    verifyPiiResponse.getRequestId());
            return;
        }

        CompletableFuture.runAsync(() -> {
            ITravelRuleTransferStatusUpdateEvent statusUpdateEvent = createTransferStatusUpdateEvent(transferPublicId, verifyPiiResponse);
            transferListener.onTransferStatusUpdate(statusUpdateEvent);
        });
    }

    private ITravelRuleTransferStatusUpdateEvent createTransferStatusUpdateEvent(String transferPublicId,
                                                                                 GtrVerifyPiiResponse verifyPiiResponse
    ) {
        TravelRuleProviderTransferStatus transferStatus = getTransferStatus(verifyPiiResponse);
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

    /**
     * Converts the PII verification response to a transfer status.
     * For more information about the possible response results, see the documentation.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#initiator-api-5-pii-verification">Global Travel Rule (GTR) documentation</a>
     */
    private TravelRuleProviderTransferStatus getTransferStatus(GtrVerifyPiiResponse verifyPiiResponse) {
        if (verifyPiiResponse.isSuccess()) {
            log.info("GTR approved the transfer, request ID: {}", verifyPiiResponse.getRequestId());
            return TravelRuleProviderTransferStatus.APPROVED;
        }

        log.info("GTR rejected the transfer, request ID: {}, status: {}, message: {}",
                verifyPiiResponse.getRequestId(), verifyPiiResponse.getVerifyStatus(), verifyPiiResponse.getVerifyMessage());
        return TravelRuleProviderTransferStatus.REJECTED;
    }

    /**
     * Notifies the transfer listener about an incoming transfer.
     *
     * @param callbackData Callback data for the webhook used for PII verification.
     * @param ivms101      An object containing sensitive PII.
     * @param rawData      Raw data received on a webhook.
     */
    public void handleVerifyPiiWebhookMessage(GtrPiiVerifyWebhookPayload callbackData, GtrIvms101Payload ivms101, String rawData) {
        if (transferListener == null) {
            log.error("GTR transfer listener is not initialized. Incoming transfer not processed. Request ID: {}",
                    callbackData.getRequestId());
            return;
        }

        CompletableFuture.runAsync(() -> {
            ITravelRuleIncomingTransferEvent event = GtrVerifyPiiMapper.toIncomingTransferEvent(callbackData, ivms101, rawData);
            transferListener.onIncomingTransferReceived(event);
        });
    }

}
