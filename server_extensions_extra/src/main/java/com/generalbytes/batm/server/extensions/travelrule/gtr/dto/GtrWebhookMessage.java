package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a message sent to a webhook by Global Travel Rule.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/callback-api-references/enum/CallbackTypeEnum">Global Travel Rule (GTR) documentation</a>
 * @see GtrWebhookPayload
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrWebhookMessage {

    private String requestId;
    /**
     * Invoker VASP Code.
     */
    private String invokeVaspCode;
    /**
     * Originator VASP Code.
     */
    private String originatorVasp;
    /**
     * Beneficiary VASP Code.
     */
    private String beneficiaryVasp;
    /**
     * The type of the callback message.
     *
     * @see GtrApiConstants.CallbackType#NETWORK_TEST
     * @see GtrApiConstants.CallbackType#PII_VERIFICATION
     * @see GtrApiConstants.CallbackType#ADDRESS_VERIFICATION
     * @see GtrApiConstants.CallbackType#RECEIVE_TX_ID
     * @see GtrApiConstants.CallbackType#TX_VERIFICATION
     */
    private int callbackType;
    /**
     * Contains the payload data associated with a callback message sent to the webhook.
     *
     * @see GtrWebhookPayload
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "callbackType")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = GtrNetworkTestWebhookPayload.class, name = GtrApiConstants.CallbackType.NETWORK_TEST + ""),
        @JsonSubTypes.Type(value = GtrAddressVerifyWebhookPayload.class, name = GtrApiConstants.CallbackType.ADDRESS_VERIFICATION + ""),
        @JsonSubTypes.Type(value = GtrTxVerifyWebhookPayload.class, name = GtrApiConstants.CallbackType.TX_VERIFICATION + ""),
        @JsonSubTypes.Type(value = GtrPiiVerifyWebhookPayload.class, name = GtrApiConstants.CallbackType.PII_VERIFICATION + ""),
        @JsonSubTypes.Type(value = GtrReceiveTxIdWebhookPayload.class, name = GtrApiConstants.CallbackType.RECEIVE_TX_ID + ""),
    })
    private GtrWebhookPayload callbackData;

}
