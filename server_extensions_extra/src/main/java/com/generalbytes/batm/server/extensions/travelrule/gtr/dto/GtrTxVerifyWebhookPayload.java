package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the request for TX verification received from Global Travel Rule as a webhook message.
 *
 * <p>The payload is associated with callback type {@link GtrApiConstants.CallbackType#TX_VERIFICATION}.</p>
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#receiver-callback-api-2-tx-verification">Global Travel Rule (GTR) documentation</a>
 * @see GtrWebhookPayload
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(GtrApiConstants.CallbackType.TX_VERIFICATION + "")
public class GtrTxVerifyWebhookPayload implements GtrWebhookPayload {

    /**
     * Unique request ID, recommended UUID.
     */
    private String requestId;
    private String travelruleId;
    /**
     * Token Name.
     */
    private String ticker;
    /**
     * Target address to verify.
     */
    private String address;
    /**
     * Tag/Memo of the transaction.
     */
    private String tag;
    /**
     * On-chain transaction hash.
     */
    private String txId;
    /**
     * Blockchain network.
     */
    private String network;
    /**
     * Initiator VASP Code, who invoke transaction to you.
     */
    private String initiatorVasp;

}
