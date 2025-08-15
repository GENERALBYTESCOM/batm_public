package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the response we send as a reaction to a received webhook message.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#receiver-callback-api-1-address-verification">Global Travel Rule (GTR) documentation</a>
 * @see GtrWebhookMessage
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GtrWebhookMessageResponse {
    private String verifyMessage;
    private int verifyStatus;
}
