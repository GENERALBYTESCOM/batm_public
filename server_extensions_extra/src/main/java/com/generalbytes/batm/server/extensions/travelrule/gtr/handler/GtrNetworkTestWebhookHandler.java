package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for network test verifying.
 */
@Slf4j
public class GtrNetworkTestWebhookHandler implements GtrWebhookHandler {

    private static final String SUCCESSFUL_RESPONSE_MESSAGE = "Network Test Successful";

    /**
     * Success is always returned.
     *
     * @param message The webhook message to handle.
     * @return Successful {@link GtrWebhookMessageResponse}.
     */
    @Override
    public GtrWebhookMessageResponse handle(GtrWebhookMessage message) {
        return new GtrWebhookMessageResponse(SUCCESSFUL_RESPONSE_MESSAGE, GtrApiConstants.VerifyStatus.SUCCESS);
    }

}
