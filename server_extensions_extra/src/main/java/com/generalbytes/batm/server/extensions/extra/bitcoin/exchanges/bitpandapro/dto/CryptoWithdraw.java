package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response for an crypto withdrawal request.
 **/
public class CryptoWithdraw   {

    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("fee")
    private BigDecimal fee;
    @JsonProperty("recipient")
    private String recipient;
    @JsonProperty("destination_tag")
    private String destinationTag;
    @JsonProperty("transaction_id")
    private UUID transactionId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDestinationTag() {
        return destinationTag;
    }

    public void setDestinationTag(String destinationTag) {
        this.destinationTag = destinationTag;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    @Override public String toString() {
        return "CryptoWithdraw{" +
            "amount=" + amount +
            ", fee=" + fee +
            ", recipient='" + recipient + '\'' +
            ", destinationTag='" + destinationTag + '\'' +
            ", transactionId=" + transactionId +
            '}';
    }
}

