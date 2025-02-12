package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a message sent to a webhook by Notabene.
 *
 * @see <a href="https://devx.notabene.id/docs/webhook-enhancements">Notabene Documentation</a>
 * @see NotabeneWebhookMessagePayload
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneWebhookMessage {

    /**
     * Indicates that a blockchain transaction must be halted.
     */
    public static final String TYPE_HALT_BLOCKCHAIN_TRANSACTION = "notification.haltBlockchainTransaction";
    /**
     * Indicates that a blockchain transaction can be completed.
     */
    public static final String TYPE_PROCESS_BLOCKCHAIN_TRANSACTION = "notification.processBlockchainTransaction";
    /**
     * Notifies us about any changes to transfers.
     */
    public static final String TYPE_TRANSACTION_UPDATED = "notification.transactionUpdated";

    /**
     * A message specifying the type of the payload.
     *
     * @see NotabeneWebhookMessage#TYPE_HALT_BLOCKCHAIN_TRANSACTION
     * @see NotabeneWebhookMessage#TYPE_PROCESS_BLOCKCHAIN_TRANSACTION
     * @see NotabeneWebhookMessage#TYPE_TRANSACTION_UPDATED
     */
    private String message;
    private NotabeneWebhookMessagePayload payload;
    private String version;

}
