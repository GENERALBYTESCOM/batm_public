package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrWebhookHandler;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Servlet responsible for handling incoming webhook messages from Global Travel Rule.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#receiver-callback-api-1-address-verification">Global Travel Rule (GTR) Documentation</a>
 * @see GtrWebhookMessage
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class GtrWebhookRestService implements IRestService {

    private final GtrWebhookHandlerService handlerService;

    public GtrWebhookRestService() {
        this.handlerService = GtrWebhookHandlerService.getInstance();
    }

    /**
     * Handles incoming webhook messages from Global Travel Rule.
     *
     * @param message The incoming webhook message.
     * @return A {@link GtrWebhookMessageResponse} object or null if the message
     */
    @POST
    @Path("/webhooks")
    public GtrWebhookMessageResponse handleWebhookMessage(GtrWebhookMessage message) {
        log.info("Received webhook message from Global Travel Rule: {}", message);
        GtrWebhookHandler requestHandler = handlerService.getHandler(message.getCallbackType());
        if (requestHandler == null) {
            log.warn("Received webhook message with unknown callback type: {}", message.getCallbackType());
            return new GtrWebhookMessageResponse("unknown callbackType", GtrApiConstants.VerifyStatus.CLIENT_BAD_PARAMETERS);
        }

        try {
            return requestHandler.handle(message);
        } catch (Exception e) {
            log.error("Unexpected error while handling webhook message from GTR.", e);
            return new GtrWebhookMessageResponse("unexpected error occurred",
                    GtrApiConstants.VerifyStatus.BENEFICIARY_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getPrefixPath() {
        return "gtr";
    }

    @Override
    public Class<?> getImplementation() {
        return getClass();
    }
}
