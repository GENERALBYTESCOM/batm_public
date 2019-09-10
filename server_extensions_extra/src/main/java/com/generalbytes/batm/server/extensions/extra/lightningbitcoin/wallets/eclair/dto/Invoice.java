package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

public class Invoice {
    public String serialized;
    public Long amount;
    public String paymentHash;
    public String nodeId;
    public Long timestamp;
    public Long expiry;
    public String description;

    @Override
    public String toString() {
        return "Invoice{" +
            "amount=" + amount +
            ", paymentHash='" + paymentHash + '\'' +
            ", nodeId='" + nodeId + '\'' +
            ", timestamp=" + timestamp +
            ", expiry=" + expiry +
            ", description='" + description + '\'' +
            ", serialized='" + serialized + '\'' +
            '}';
    }
}
