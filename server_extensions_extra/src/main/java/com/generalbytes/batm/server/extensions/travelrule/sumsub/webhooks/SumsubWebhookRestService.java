package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks;

import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Servlet responsible for handling incoming webhook messages from Sumsub Travel Rule for transaction monitoring.
 *
 * @see <a href="https://docs.sumsub.com/docs/transaction-monitoring-webhooks">Sumsub documentation</a>
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class SumsubWebhookRestService implements IRestService {

    private final SumsubTransferHandler transferHandler;

    /**
     * Constructor.
     */
    public SumsubWebhookRestService() {
        transferHandler = SumsubTransferHandler.getInstance();
    }

    @Override
    public String getPrefixPath() {
        return "sumsub";
    }

    @Override
    public Class<?> getImplementation() {
        return getClass();
    }

    /**
     * Handles incoming webhook messages from Sumsub Travel Rule for transaction monitoring.
     *
     * @param message The incoming webhook message.
     */
    @POST
    @Path("/webhooks")
    public void handleWebhookMessage(@HeaderParam(SumsubTravelRuleApiConstants.HttpHeaderParam.VASP_DID) String vaspDid,
                                     @HeaderParam(SumsubTravelRuleApiConstants.HttpHeaderParam.DIGEST_ALGORITHM) String digestAlgorithm,
                                     @HeaderParam(SumsubTravelRuleApiConstants.HttpHeaderParam.PAYLOAD_DIGEST) String payloadDigest,
                                     String message
    ) {
        log.info("Received webhook message from Sumsub for VASP: {}, message: {}", vaspDid, message);

        SumsubWebhookRequest webhookRequest = new SumsubWebhookRequest(vaspDid, digestAlgorithm, payloadDigest, message);
        handleMessageAsynchronously(webhookRequest);
    }

    /**
     * Sumsub expects an HTTP 2xx response from the webhook within 5 seconds.
     * Therefore, the payload should be acknowledged immediately and any heavy processing should be performed asynchronously.
     *
     * @see <a href="https://docs.sumsub.com/docs/webhook-manager">Sumsub documentation</a>
     */
    private void handleMessageAsynchronously(SumsubWebhookRequest webhookRequest) {
        CompletableFuture.runAsync(() -> {
            try {
                transferHandler.handleIncomingMessage(webhookRequest);
            } catch (TravelRuleProviderException e) {
                log.warn("The incoming webhook message from Sumsub could not be processed. Reason: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error while handling webhook message from Sumsub.", e);
            }
        });
    }

}
