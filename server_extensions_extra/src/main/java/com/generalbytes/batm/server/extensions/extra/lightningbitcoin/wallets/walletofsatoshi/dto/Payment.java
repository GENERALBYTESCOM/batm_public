package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto;

import java.math.BigDecimal;

public class Payment {
    public String id;
    public String time;
    public String type;
    public Status status;

    public enum Status {PAID, PENDING, QUEUED, FAILED}

    public BigDecimal amount;
    public String address;
    public String fees;
    public String currency;
    public String transactionId;
    public String description;
    public String paymentGroupId;

    @Override
    public String toString() {
        return "Payment{" +
            "id='" + id + '\'' +
            ", time='" + time + '\'' +
            ", type='" + type + '\'' +
            ", status='" + status + '\'' +
            ", amount=" + amount +
            ", address=" + address +
            ", fees='" + fees + '\'' +
            ", currency='" + currency + '\'' +
            ", transactionId='" + transactionId + '\'' +
            ", description='" + description + '\'' +
            ", paymentGroupId='" + paymentGroupId + '\'' +
            '}';
    }
}
