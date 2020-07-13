package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload for crypto withdrawal.
 **/
public class WithdrawCrypto {

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("recipient")
    private Recipient recipient;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(
        Recipient recipient) {
        this.recipient = recipient;
    }

    @Override public String toString() {
        return "WithdrawCrypto{" +
            "currency='" + currency + '\'' +
            ", amount=" + amount +
            ", recipient=" + recipient +
            '}';
    }

    public static class Recipient {

        @JsonProperty("address")
        private String address;
        @JsonProperty("destination_tag")
        private String destinationTag;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDestinationTag() {
            return destinationTag;
        }

        public void setDestinationTag(String destinationTag) {
            this.destinationTag = destinationTag;
        }

        @Override public String toString() {
            return "Recipient{" +
                "address='" + address + '\'' +
                ", destinationTag='" + destinationTag + '\'' +
                '}';
        }
    }
}

