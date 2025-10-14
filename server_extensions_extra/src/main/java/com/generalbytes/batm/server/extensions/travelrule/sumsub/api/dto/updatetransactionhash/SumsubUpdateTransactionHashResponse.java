package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import lombok.Getter;

/**
 * Response object from Sumsub containing data about updating the blockchain transaction hash
 * Used in {@link SumsubTravelRuleApi#updateTransactionHash}.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubUpdateTransactionHashResponse {
    private String id;
    private TransactionData data;

    /**
     * Info about transaction data.
     */
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionData {
        private String txnId;
    }
}
