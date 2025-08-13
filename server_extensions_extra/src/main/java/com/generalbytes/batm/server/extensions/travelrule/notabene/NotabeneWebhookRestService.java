package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneWebhookMessage;
import lombok.extern.slf4j.Slf4j;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

/**
 * Servlet responsible for handling incoming webhook messages from Notabene.
 *
 * @see <a href="https://devx.notabene.id/docs/webhook-enhancements">Notabene Documentation</a>
 * @see NotabeneWebhookMessage
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class NotabeneWebhookRestService implements IRestService {

    private final NotabeneTransferPublisher transferPublisher;

    public NotabeneWebhookRestService() {
        this.transferPublisher = NotabeneTransferPublisher.getInstance();
    }

    @POST
    @Path("/webhooks")
    public void handleWebhookMessage(NotabeneWebhookMessage message) {
        if (NotabeneWebhookMessage.TYPE_TRANSACTION_UPDATED.equals(message.getMessage())) {
            transferPublisher.publishEvent(message.getPayload().getTransaction());
        }
    }

    @Override
    public String getPrefixPath() {
        return "notabene";
    }

    @Override
    public Class getImplementation() {
        return getClass();
    }
}
