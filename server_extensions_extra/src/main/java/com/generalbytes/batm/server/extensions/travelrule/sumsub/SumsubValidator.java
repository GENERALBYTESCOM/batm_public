package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactionownershipresolution.SumsubTransactionOwnershipResolutionResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Validator for {@link SumsubService}.
 */
@Slf4j
public class SumsubValidator {

    private static final String INVALID_RESPONSE_MESSAGE = "invalid response";

    /**
     * Validate response from {@link SumsubTravelRuleApi#submitTransactionWithoutApplicant}.
     *
     * @param request  Request.
     * @param response Response.
     */
    public void validateSubmitTxWithoutApplicantResponse(SumsubSubmitTxWithoutApplicantRequest request,
                                                         SumsubTransactionInformationResponse response
    ) {
        if (StringUtils.isBlank(response.getId())) {
            log.warn("Failed to submit Sumsub transaction for non-existing applicant, Sumsub ID in response is blank."
                            + " Txn ID in request: {}", request.getTxnId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (response.getData() == null) {
            log.warn("Failed to submit Sumsub transaction for non-existing applicant, data object in response is null."
                    + " Txn ID in request: {}", request.getTxnId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (!request.getTxnId().equals(response.getData().getTxnId())) {
            log.warn("Failed to submit Sumsub transaction for non-existing applicant, invalid response."
                    + " Txn ID in request: {}, Txn ID in response: {}", request.getTxnId(), response.getData().getTxnId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }
    }

    /**
     * Validate response from {@link SumsubTravelRuleApi#updateTransactionHash}.
     *
     * @param updateRequest Update request.
     * @param response      Response.
     */
    public void validateSumsubUpdateTransactionHashResponse(ITravelRuleTransferUpdateRequest updateRequest,
                                                            SumsubUpdateTransactionHashResponse response
    ) {
        if (StringUtils.isBlank(response.getId())) {
            log.warn("Invalid response from Sumsub when updating blockchain transaction hash, Sumsub ID in response is blank."
                    + " Txn ID in request: {}", updateRequest.getPublicId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (!updateRequest.getId().equals(response.getId())) {
            log.warn("Invalid response from Sumsub when updating blockchain transaction hash."
                    + " Sumsub ID in request: {}, Sumsub ID in response: {}", updateRequest.getId(), response.getId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (response.getData() == null) {
            log.warn("Invalid response from Sumsub when updating blockchain transaction hash, data object in response is null."
                    + " Txn ID in request: {}", updateRequest.getPublicId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (!updateRequest.getPublicId().equals(response.getData().getTxnId())) {
            log.warn("Invalid response from Sumsub when updating blockchain transaction hash."
                    + " Txn ID in request: {}, Txn ID in response: {}", updateRequest.getPublicId(), response.getData().getTxnId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }
    }

    /**
     * Validate response from {@link SumsubTravelRuleApi#getTransactionInformation}.
     *
     * @param txnId    Sumsub transaction ID.
     * @param response Response.
     */
    public void validateSumsubTransactionInformationResponse(String txnId, SumsubTransactionInformationResponse response) {
        if (StringUtils.isBlank(response.getId())) {
            log.warn("Invalid response from Sumsub when obtained transaction information, Sumsub ID in response is blank."
                    + " Sumsub transaction ID in request: {}", txnId);
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (response.getData() == null) {
            log.warn("Invalid response from Sumsub when obtained transaction information, 'data' object in response is null."
                    + " Sumsub transaction ID in request: {}", txnId);
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (!txnId.equals(response.getId())) {
            log.warn("Invalid response from Sumsub when obtained transaction information."
                    + " Sumsub transaction ID in request: {}, Sumsub transaction ID in response: {}", txnId, response.getId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }
    }

    /**
     * Validate response from {@link SumsubTravelRuleApi#confirmTransactionOwnership}
     * and {@link SumsubTravelRuleApi#rejectTransactionOwnership}.
     *
     * @param event    {@link ITravelRuleTransferResolvedEvent}
     * @param response Response.
     */
    public void validateSumsubTransactionOwnershipResolutionResponse(ITravelRuleTransferResolvedEvent event,
                                                                     SumsubTransactionOwnershipResolutionResponse response
    ) {
        if (StringUtils.isBlank(response.getId())) {
            log.warn("Invalid response from Sumsub when inform about transaction resolution, Sumsub transaction ID in response is blank."
                    + " Sumsub transaction ID in request: {}", event.getTransferExternalId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (!event.getTransferExternalId().equals(response.getId())) {
            log.warn("Invalid response from Sumsub when inform about transaction resolution. Sumsub transaction ID in request: {},"
                    + " Sumsub transaction ID in response: {}", event.getTransferExternalId(), response.getId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }
    }

    /**
     * Validate response from {@link SumsubTravelRuleApi#confirmWalletOwnership}.
     *
     * @param event    {@link ITravelRuleTransferResolvedEvent}
     * @param response Response.
     */
    public void validateSumsubConfirmWalletOwnershipResponse(ITravelRuleTransferResolvedEvent event,
                                                             SumsubTransactionInformationResponse response
    ) {
        if (StringUtils.isBlank(response.getId())) {
            log.warn("Invalid response from Sumsub when confirming wallet ownership, Sumsub transaction ID in response is blank."
                    + " Sumsub transaction ID in request: {}", event.getTransferExternalId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }

        if (!event.getTransferExternalId().equals(response.getId())) {
            log.warn("Invalid response from Sumsub when confirming wallet ownership. Sumsub transaction ID in request: {},"
                    + " Sumsub transaction ID in response: {}", event.getTransferExternalId(), response.getId());
            throw new TravelRuleProviderException(INVALID_RESPONSE_MESSAGE);
        }
    }

}
