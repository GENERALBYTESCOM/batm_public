package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aqua.dto;

import java.math.BigDecimal;

public class OrderRequest {
    public BigDecimal receiveQuantity;
    public BigDecimal deliverQuantity;
    public String ticker;
    public OrderSide tradeSide;

    public OrderRequest() {
    }

    public OrderRequest(BigDecimal receiveQuantity, String ticker, OrderSide tradeSide) {
        this.receiveQuantity = receiveQuantity;
        this.ticker = ticker;
        this.tradeSide = tradeSide;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
            "receiveQuantity=" + receiveQuantity +
            ", ticker='" + ticker + '\'' +
            ", tradeSide=" + tradeSide +
            '}';
    }
}