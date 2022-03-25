package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aqua.dto;

import java.math.BigDecimal;

public class QuoteRequest {
    public BigDecimal receiveQuantity;
    public BigDecimal deliverQuantity;
    public String ticker;
    public OrderSide tradeSide;

    public QuoteRequest() {
    }


    public QuoteRequest(BigDecimal receiveQuantity, String ticker, OrderSide tradeSide) {
        this.receiveQuantity = receiveQuantity;
        this.ticker = ticker;
        this.tradeSide = tradeSide;
    }

    @Override
    public String toString() {
        return "QuoteRequest{" +
            "receiveQuantity=" + receiveQuantity +
            ", ticker='" + ticker + '\'' +
            ", tradeSide=" + tradeSide +
            '}';
    }
}