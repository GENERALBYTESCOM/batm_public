package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class BuyCoinRequest {
    public BigDecimal receiveQuantity;
    public String ticker;
    public String tradeSide;

    public BuyCoinRequest() {
    }

    public BuyCoinRequest(BigDecimal receiveQuantity, String ticker, String tradeSide) {
        this.receiveQuantity = receiveQuantity;
        this.ticker = ticker;
        this.tradeSide = tradeSide;
    }

    @Override
    public String toString() {
        return "BuyCoinRequest{" +
            "receiveQuantity=" + receiveQuantity +
            ", ticker='" + ticker + '\'' +
            ", tradeSide='" + tradeSide + '\'' +
            '}';
    }


}