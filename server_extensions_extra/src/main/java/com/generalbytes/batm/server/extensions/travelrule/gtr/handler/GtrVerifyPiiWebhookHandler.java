package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrVerifyPiiMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.GtrValidator;
import com.generalbytes.batm.server.extensions.travelrule.gtr.GtrVerifyPiiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Handler processing PII verification messages from Global Travel Rule.
 */
@Slf4j
@AllArgsConstructor
public class GtrVerifyPiiWebhookHandler implements GtrWebhookHandler {

    private static final int PII_VERIFICATION_TIMEOUT_IN_SECONDS = 10;
    private final GtrVerifyPiiService verifyPiiService;
    private final GtrValidator validator;
    private final ConcurrentHashMap<String, CompletableFuture<TravelRuleProviderTransferStatus>> messagesInProcess
            = new ConcurrentHashMap<>();

    /**
     * Processes the PII verification message from GTR.
     *
     * @param message The webhook message to handle.
     * @return Response with processing result.
     */
    @Override
    public GtrWebhookMessageResponse handle(GtrWebhookMessage message) {
        if (!(message.getCallbackData() instanceof GtrPiiVerifyWebhookPayload piiVerifyPayload)) {
            return new GtrWebhookMessageResponse("Invalid callbackData for PII verification.",
                    GtrApiConstants.VerifyStatus.CLIENT_BAD_PARAMETERS);
        }

        try {
            validator.validateGtrWebhookMessage(message);
            validator.validateVerifyPiiIncomingMessage(piiVerifyPayload);

            CompletableFuture<TravelRuleProviderTransferStatus> future = new CompletableFuture<>();
            messagesInProcess.put(piiVerifyPayload.getRequestId(), future);

            verifyPiiService.processVerifyPiiWebhookMessage(message, piiVerifyPayload);

            TravelRuleProviderTransferStatus transferStatus = future.get(PII_VERIFICATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            if (transferStatus == TravelRuleProviderTransferStatus.APPROVED) {
                return GtrVerifyPiiMapper.toSuccessVerifyPiiWebhookResponse(piiVerifyPayload);
            }
        } catch (TimeoutException e) {
            log.error("Failed to PII verification for incoming transfer within timeout, request ID: {}", piiVerifyPayload.getRequestId());
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for PII verification for incoming transfer, request ID: {}",
                    piiVerifyPayload.getRequestId(), e);
            Thread.currentThread().interrupt();
        } catch (TravelRuleProviderException e) {
            log.error("Failed to PII verification for incoming transfer, request ID: {}, reason: {}",
                    piiVerifyPayload.getRequestId(), e.getMessage());
        } catch (Exception e) {
            log.error("Failed to PII verification for incoming transfer, request ID: {}", piiVerifyPayload.getRequestId(), e);
        } finally {
            messagesInProcess.remove(piiVerifyPayload.getRequestId());
        }

        return GtrVerifyPiiMapper.toFailedVerifyPiiWebhookResponse(piiVerifyPayload);
    }

    /**
     * Processes the result of PII verification received from the server.
     *
     * @param event {@link ITravelRuleTransferResolvedEvent} with the result of PII verification.
     * @return {@code true} if the event was successfully processed, otherwise {@code false}.
     */
    public boolean onTransferResolved(ITravelRuleTransferResolvedEvent event) {
        CompletableFuture<TravelRuleProviderTransferStatus> future = messagesInProcess.get(event.getTransferExternalId());
        if (future == null) {
            log.warn("The GTR provider must be informed of the PII verification result immediately after receiving"
                    + " the transfer (synchronously). The GTR provider cannot be informed subsequently.");
            return false;
        }

        future.complete(event.getResolvedStatus());
        return true;
    }

}
