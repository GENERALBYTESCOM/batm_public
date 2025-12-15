package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Response object from Sumsub containing data about transaction.
 * Used in {@link SumsubTravelRuleApi#submitTransactionWithoutApplicant}.
 *
 * @see <a href="https://docs.sumsub.com/reference/get-transaction">Sumsub documentation</a>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubTransactionInformationResponse {

    private static final String SUMSUB_DATE_PATTERN = "yyyy-MM-dd HH:mm:ssZ";

    private String id;
    private String applicantId;
    private String externalUserId;
    private String clientId;
    private TransactionData data;
    private Integer score;
    private TransactionReview review;
    @JsonFormat(pattern = SUMSUB_DATE_PATTERN)
    private OffsetDateTime createdAt;
    private TransactionScoringResult scoringResult;
    private Boolean txnInactive;

    /**
     * Info about transaction data.
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionData {
        private String txnId;
        @JsonFormat(pattern = SUMSUB_DATE_PATTERN)
        private OffsetDateTime txnDate;
        private SumsubIdentity applicant;
        private SumsubIdentity counterparty;
        private SumsubTransactionInfo info;
    }

    /**
     * Info about transaction review.
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionReview {
        private String reviewId;
        private String attemptId;
        private Boolean confirmed;
    }

    /**
     * Info about transaction scoring result.
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionScoringResult {
        private Integer score;
        private Integer dryScore;
    }
}
