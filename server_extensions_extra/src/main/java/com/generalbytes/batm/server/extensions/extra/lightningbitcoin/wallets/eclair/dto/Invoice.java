package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

import java.math.BigInteger;

public class Invoice {
    public BigInteger amount;
    public String paymentHash;
    public String nodeId;
    public Integer timestamp;
    public Integer expiry;
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
            '}';
    }
}
