package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class QuoteRequest {
    public BigDecimal quantity;
    public String marketSymbol;
    public OrderSide orderSide;
    public OrderType orderType;

    public QuoteRequest() {
    }

    public QuoteRequest(BigDecimal quantity, String marketSymbol, OrderSide orderSide, OrderType orderType) {
        this.quantity = quantity;
        this.marketSymbol = marketSymbol;
        this.orderSide = orderSide;
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
            "quantity=" + quantity +
            ", marketSymbol='" + marketSymbol + '\'' +
            ", orderSide=" + orderSide +
            ", orderType=" + orderType +
            '}';
    }
}