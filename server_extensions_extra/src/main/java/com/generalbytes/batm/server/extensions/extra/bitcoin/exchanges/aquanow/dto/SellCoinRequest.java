package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class SellCoinRequest {
    public BigDecimal deliverQuantity;
    public String ticker;
    public String tradeSide;

    public SellCoinRequest() {
    }

    public SellCoinRequest(BigDecimal deliverQuantity, String ticker, String tradeSide) {
        this.deliverQuantity = deliverQuantity;
        this.ticker = ticker;
        this.tradeSide = tradeSide;
    }

    @Override
    public String toString() {
        return "BuyCoinRequest{" +
            "deliverQuantity=" + deliverQuantity +
            ", ticker='" + ticker + '\'' +
            ", tradeSide='" + tradeSide + '\'' +
            '}';
    }
}