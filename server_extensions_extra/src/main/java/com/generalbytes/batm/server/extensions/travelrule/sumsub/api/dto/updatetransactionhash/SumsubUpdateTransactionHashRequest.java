package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object for update blockchain transaction hash.
 * Used in {@link SumsubTravelRuleApi#updateTransactionHash}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubUpdateTransactionHashRequest {
    /**
     * Blockchain transaction hash.
     */
    private String paymentTxnId;
}
