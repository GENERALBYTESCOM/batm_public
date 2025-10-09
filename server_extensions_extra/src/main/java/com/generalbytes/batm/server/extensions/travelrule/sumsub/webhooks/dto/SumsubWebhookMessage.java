package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a message sent to a webhook by Sumsub for transaction monitoring.
 *
 * @see <a href="https://docs.sumsub.com/docs/transaction-monitoring-webhooks">Sumsub documentation</a>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubWebhookMessage {
    private String applicantId;
    private String applicantType;
    private String correlationId;
    private boolean sandboxMode;
    private String externalUserId;
    private String type;
    private ReviewResult reviewResult;
    private String reviewStatus;
    private String createdAt;
    private String createdAtMs;
    private String clientId;
    private String kytTxnId;
    private String kytDataTxnId;
    private String kytTxnType;

    /**
     * Info about review result.
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReviewResult {
        private String reviewAnswer;
        private String reviewRejectType;
    }
}
