package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Object containing info about Sumsub transaction.
 * Used in {@link SumsubSubmitTxWithoutApplicantRequest} and {@link SumsubTransactionInformationResponse.TransactionData}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubTransactionInfo {
    private String currencyType;
    private String direction;
    private String currencyCode;
    private BigDecimal amount;
}
