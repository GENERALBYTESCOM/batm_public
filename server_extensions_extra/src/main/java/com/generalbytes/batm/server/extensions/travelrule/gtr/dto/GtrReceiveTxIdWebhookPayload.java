package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a notification about TX ID received from Global Travel Rule as a webhook message.
 *
 * <p>The payload is associated with callback type {@link GtrApiConstants.CallbackType#RECEIVE_TX_ID}.</p>
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#receiver-callback-api-4-receive-tx-id">Global Travel Rule (GTR) documentation</a>
 * @see GtrWebhookPayload
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(GtrApiConstants.CallbackType.RECEIVE_TX_ID + "")
public class GtrReceiveTxIdWebhookPayload implements GtrWebhookPayload {

    /**
     * Unique request ID, recommended UUID.
     */
    private String requestId;
    private String travelruleId;
    /**
     * On-chain transaction hash.
     */
    private String txId;

}
