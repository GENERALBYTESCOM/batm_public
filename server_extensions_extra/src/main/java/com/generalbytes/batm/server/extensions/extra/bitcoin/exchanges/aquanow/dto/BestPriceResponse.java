package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class BestPriceResponse {
    public BigDecimal bestBid;
    public BigDecimal bestAsk;
    public BigDecimal spread;
    public String symbol;
    public Long lastUpdated;

    @Override
    public String toString() {
        return "BestPriceResponse{" +
            "bestBid=" + bestBid +
            ", bestAsk=" + bestAsk +
            ", spread=" + spread +
            ", symbol='" + symbol + '\'' +
            ", lastUpdated=" + lastUpdated +
            '}';
    }
}
