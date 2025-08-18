package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrAddressVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * This handler is responsible for handling Verify Address requests from Global Travel Rule.
 */
@Slf4j
public class GtrVerifyAddressWebhookHandler implements GtrWebhookHandler {

    private final TravelRuleExtensionContext ctx;

    public GtrVerifyAddressWebhookHandler(TravelRuleExtensionContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Handle an address verification request.
     *
     * @param message The webhook message of type {@link GtrApiConstants.CallbackType#ADDRESS_VERIFICATION}.
     * @return A {@link GtrWebhookMessageResponse} as the response to the verification request.
     * @see GtrAddressVerifyWebhookPayload
     */
    @Override
    public GtrWebhookMessageResponse handle(GtrWebhookMessage message) {
        if (!(message.getCallbackData() instanceof GtrAddressVerifyWebhookPayload addressVerifyPayload)) {
            return new GtrWebhookMessageResponse("Invalid callbackData for address verification.",
                    GtrApiConstants.VerifyStatus.CLIENT_BAD_PARAMETERS);
        }
        log.debug("Received address verification webhook message. Verifying address {}", addressVerifyPayload.getAddress());
        ITravelRuleTransferData transferData = ctx.findTravelRuleTransferByAddress(addressVerifyPayload.getAddress());
        if (transferData == null) {
            log.debug("Could not find address {} in the system. Returning 'address not found' response.",
                    addressVerifyPayload.getAddress());
            return new GtrWebhookMessageResponse("address not found", GtrApiConstants.VerifyStatus.ADDRESS_NOT_FOUND);
        }
        log.debug("Address {} found in this system. Returning 'success' response.", addressVerifyPayload.getAddress());
        return new GtrWebhookMessageResponse("success", GtrApiConstants.VerifyStatus.SUCCESS);
    }
}
