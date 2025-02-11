package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the payload of a webhook message.
 *
 * @see NotabeneWebhookMessage
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneWebhookMessagePayload {

    /**
     * Information about the affected transaction.
     *
     * @see NotabeneTransferInfo
     */
    private NotabeneTransferInfo transaction;

}
