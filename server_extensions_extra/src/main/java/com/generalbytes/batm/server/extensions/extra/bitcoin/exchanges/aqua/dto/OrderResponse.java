package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aqua.dto;

import java.math.BigDecimal;

public class OrderResponse {
    // not an enum because all possible values not documented

    public String orderId;
    public String receiveCurrency;
    public String deliverCurrency;
    public BigDecimal receiveQuantity;
    public BigDecimal deliverQuantity;
    public BigDecimal fee;

    @Override
    public String toString() {
        return "OrderResponse{" +
            "orderId='" + orderId + '\'' +
            ", fee='" + fee + '\'' +
            ", receiveCurrency='" + receiveCurrency + '\'' +
            ", deliverCurrency='" + deliverCurrency + '\'' +
            ", deliverQuantity='" + deliverQuantity + '\'' +
            ", receiveCurrency='" + receiveCurrency + '\'' +
            ", receiveQuantity=" + receiveQuantity +
            '}';
    }

}