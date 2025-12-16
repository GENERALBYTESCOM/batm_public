package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Object containing info about Sumsub identity (applicant or counterparty).
 * Used in {@link SumsubSubmitTxWithoutApplicantRequest} and {@link SumsubTransactionInformationResponse.TransactionData}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubIdentity {
    private String type;
    private String externalUserId;
    private String firstName;
    private String lastName;
    private String fullName;
    private SumsubInstitutionInfo institutionInfo;
    private SumsubPaymentMethod paymentMethod;
}
