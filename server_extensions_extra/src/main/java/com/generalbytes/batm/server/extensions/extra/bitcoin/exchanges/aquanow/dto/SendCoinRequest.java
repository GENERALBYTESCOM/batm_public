package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class SendCoinRequest {
    public BigDecimal quantity;
    public String symbol;
    public String address;
    public String transactionType;

    public SendCoinRequest() {
        this.transactionType = "WITHDRAW";
    }

    public SendCoinRequest(BigDecimal quantity, String symbol, String address) {
        this.quantity = quantity;
        this.symbol = symbol;
        this.address = address;
        this.transactionType = "WITHDRAW";
    }

    @Override
    public String toString() {
        return "SendCoinRequest{" +
            "quantity=" + quantity +
            ", symbol='" + symbol + '\'' +
            ", address='" + address + '\'' +
            ", transactionType='" + transactionType + '\'' +
            '}';
    }



}
