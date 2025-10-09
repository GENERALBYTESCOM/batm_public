package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactionownershipresolution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import lombok.Getter;

/**
 * Response object from Sumsub containing data about transaction resolution.
 * Used in {@link SumsubTravelRuleApi#confirmTransactionOwnership} and {@link SumsubTravelRuleApi#rejectTransactionOwnership}.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubTransactionOwnershipResolutionResponse {
    /**
     * Sumsub transaction ID.
     */
    private String id;
}
