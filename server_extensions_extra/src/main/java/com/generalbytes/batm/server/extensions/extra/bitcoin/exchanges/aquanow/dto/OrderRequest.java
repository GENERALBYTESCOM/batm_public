package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class OrderRequest {
    public BigDecimal quantity;
    public BigDecimal pricePerUnit;
    public String marketSymbol;
    public OrderSide orderSide;
    public OrderType orderType;

    public OrderRequest() {
    }

    public OrderRequest(BigDecimal quantity, BigDecimal pricePerUnit, String marketSymbol, OrderSide orderSide, OrderType orderType) {
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.marketSymbol = marketSymbol;
        this.orderSide = orderSide;
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
            "quantity=" + quantity +
            ", pricePerUnit=" + pricePerUnit +
            ", marketSymbol='" + marketSymbol + '\'' +
            ", orderSide=" + orderSide +
            ", orderType=" + orderType +
            '}';
    }
}