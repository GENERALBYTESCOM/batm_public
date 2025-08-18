package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrVerifyPiiWebhookHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Validator for {@link GtrService}.
 */
@Slf4j
public class GtrValidator {

    /**
     * Validate VASP code.
     *
     * @param vaspCode VASP code.
     */
    public void validateVaspCode(String vaspCode) {
        if (StringUtils.isBlank(vaspCode)) {
            throw new TravelRuleProviderException("invalid input, VASP code is blank");
        }
    }

    /**
     * Validate response from {@link GtrApi#registerTravelRuleRequest(String, GtrRegisterTravelRuleRequest)}.
     *
     * @param response  Response.
     * @param requestId Request ID.
     */
    public void validateRegisterTravelRuleResponse(GtrRegisterTravelRuleResponse response, String requestId) {
        if (!requestId.equals(response.getRequestId())) {
            log.warn("Failed to register GTR request, invalid response. Request ID: {}, Request ID in response: {}",
                    requestId, response.getRequestId());
            throw new TravelRuleProviderException("invalid response");
        }
    }

    /**
     * Validate request for {@link GtrApi#verifyAddress(String, GtrVerifyAddressRequest)}.
     *
     * @param request Request.
     */
    public void validateWalletEvaluationRequest(IIdentityWalletEvaluationRequest request) {
        if (StringUtils.isBlank(request.getCryptoAddress())) {
            throw new TravelRuleProviderException("GTR data for address verification is not valid - crypto address is blank");
        }
        if (StringUtils.isBlank(request.getCryptocurrency())) {
            throw new TravelRuleProviderException("GTR data for address verification is not valid - cryptocurrency is blank");
        }
        if (StringUtils.isBlank(request.getDidOfVaspHostingCustodialWallet())) {
            throw new TravelRuleProviderException(
                    "GTR data for address verification is not valid - DID of VASP hosting custodial wallet is null"
            );
        }
    }

    /**
     * Validate message for PII verification for {@link GtrVerifyPiiWebhookHandler#handle(GtrWebhookMessage)}.
     *
     * @param message GTR webhook message.
     */
    public void validateGtrWebhookMessage(GtrWebhookMessage message) {
        if (StringUtils.isBlank(message.getInvokeVaspCode())) {
            throwExceptionForVerifyPiiIncomingMessage("invoke VASP code is blank");
        }
    }

    /**
     * Validate payload for PII verification for {@link GtrVerifyPiiWebhookHandler#handle(GtrWebhookMessage)}.
     *
     * @param payload Verify PII payload.
     */
    public void validateVerifyPiiIncomingMessage(GtrPiiVerifyWebhookPayload payload) {
        if (StringUtils.isBlank(payload.getRequestId())) {
            throwExceptionForVerifyPiiIncomingMessage("request ID is blank");
        }
        if (StringUtils.isBlank(payload.getAddress())) {
            throwExceptionForVerifyPiiIncomingMessage("address is blank");
        }
        if (StringUtils.isBlank(payload.getEncryptedPayload())) {
            throwExceptionForVerifyPiiIncomingMessage("encrypted payload is blank");
        }
        if (StringUtils.isBlank(payload.getOriginatorVasp())) {
            throwExceptionForVerifyPiiIncomingMessage("originator VASP is blank");
        }
        if (StringUtils.isBlank(payload.getBeneficiaryVasp())) {
            throwExceptionForVerifyPiiIncomingMessage("beneficiary VASP is blank");
        }
        if (StringUtils.isBlank(payload.getInitiatorPublicKey())) {
            throwExceptionForVerifyPiiIncomingMessage("initiator public key is blank");
        }
        if (StringUtils.isBlank(payload.getReceiverPublicKey())) {
            throwExceptionForVerifyPiiIncomingMessage("receiver public key is blank");
        }
    }

    private void throwExceptionForVerifyPiiIncomingMessage(String reason) {
        throw new TravelRuleProviderException("GTR webhook payload for PII verification is not valid - " + reason);
    }

}
