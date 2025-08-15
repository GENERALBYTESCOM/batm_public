package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;

/**
 * Represents a handler responsible for handling webhooks from Global Travel Rule.
 */
public interface GtrWebhookHandler {

    /**
     * Handler a webhook message.
     *
     * @param message The webhook message to handle.
     * @return A {@link GtrWebhookMessageResponse} as the response to the webhook message.
     */
    GtrWebhookMessageResponse handle(GtrWebhookMessage message);

}
