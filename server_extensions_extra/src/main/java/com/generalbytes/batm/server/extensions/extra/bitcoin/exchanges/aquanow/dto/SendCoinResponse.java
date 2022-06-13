package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class SendCoinResponse {
    public String address;
    public String txId;
    public String transactionType;
    public String adminApproval;
    public String message;
    public BigDecimal quantity;
    public BigDecimal networkFee;

    @Override
    public String toString() {
        return "SendCoinResponse{" +
            "address='" + address + '\'' +
            ", txId='" + txId + '\'' +
            ", transactionType='" + transactionType + '\'' +
            ", adminApproval='" + adminApproval + '\'' +
            ", quantity=" + quantity +
            ", networkFee=" + networkFee +
            '}';
    }

}
