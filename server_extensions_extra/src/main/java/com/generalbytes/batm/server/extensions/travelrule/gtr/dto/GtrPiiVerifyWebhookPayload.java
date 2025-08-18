package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the request for PII verification received from Global Travel Rule as a webhook message.
 *
 * <p>The payload is associated with callback type {@link GtrApiConstants.CallbackType#PII_VERIFICATION}.</p>
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#receiver-callback-api-3-pii-verification">Global Travel Rule (GTR) documentation</a>
 * @see GtrWebhookPayload
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(GtrApiConstants.CallbackType.PII_VERIFICATION + "")
public class GtrPiiVerifyWebhookPayload implements GtrWebhookPayload {

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
     * Blockchain network.
     */
    private String network;
    /**
     * On-chain transaction hash. In before-on-chain, txId will be null.
     */
    private String txId;
    private String encryptedPayload;
    private BigDecimal amount;
    private BigDecimal fiatPrice;
    private String fiatName;
    private List<String> initiatorExpectVerifyFields;
    private int secretType;
    /**
     * Originator VASP Code.
     */
    private String originatorVasp;
    /**
     * Beneficiary VASP Code.
     */
    private String beneficiaryVasp;
    /**
     * Initiator VASP Code, who invoke transaction to you.
     */
    private String initiatorVasp;
    private String initiatorPublicKey;
    private String receiverVasp;
    private String receiverPublicKey;
    private String piiSpecVersion;
    /**
     * 1: RECEIVER TO SENDER (YOU-Originator).
     * 2: SENDER TO RECEIVER (YOU-Beneficiary).
     */
    private int verificationDirection;

}
