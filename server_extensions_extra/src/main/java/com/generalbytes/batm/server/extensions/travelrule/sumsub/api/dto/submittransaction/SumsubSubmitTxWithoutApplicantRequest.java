package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubIdentity;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object for submit transaction for non-existing applicant.
 * Used in {@link SumsubTravelRuleApi#submitTransactionWithoutApplicant}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubSubmitTxWithoutApplicantRequest {
    private String txnId;
    private String type;
    private SumsubTransactionInfo info;
    private SumsubIdentity applicant;
    private SumsubIdentity counterparty;
}
