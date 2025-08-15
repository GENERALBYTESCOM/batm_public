package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrReceiveTxIdWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler processing notifications about on-chain transaction hashes from Global Travel Rule.
 */
@Slf4j
public class GtrReceiveTxIdWebhookHandler implements GtrWebhookHandler {

    private static final String SUCCESSFUL_RESPONSE_MESSAGE = "TX ID Received";

    /**
     * Success is always returned as a result (see documentation).
     *
     * @param message The webhook message to handle.
     * @return Successful {@link GtrWebhookMessageResponse}.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#receiver-callback-api-4-receive-tx-id">Global Travel Rule (GTR) Documentation</a>
     */
    @Override
    public GtrWebhookMessageResponse handle(GtrWebhookMessage message) {
        if (message.getCallbackData() instanceof GtrReceiveTxIdWebhookPayload callbackData) {
            log.info("A notification with a transaction hash was received from GTR."
                            + " Transaction hash: {}, travel rule ID: {}, originator VASP: {}, beneficiary VASP: {}",
                    callbackData.getTxId(), callbackData.getTravelruleId(), message.getOriginatorVasp(), message.getBeneficiaryVasp());
        } else {
            log.warn("Invalid callbackData for receiving transaction hash. Originator VASP: {}, beneficiary VASP: {}",
                    message.getOriginatorVasp(), message.getBeneficiaryVasp());
        }

        return new GtrWebhookMessageResponse(SUCCESSFUL_RESPONSE_MESSAGE, GtrApiConstants.VerifyStatus.SUCCESS);
    }

}
