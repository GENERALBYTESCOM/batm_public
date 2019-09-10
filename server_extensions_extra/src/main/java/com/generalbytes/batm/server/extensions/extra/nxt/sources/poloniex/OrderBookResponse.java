package com.generalbytes.batm.server.extensions.extra.nxt.sources.poloniex;

import java.math.BigDecimal;

/**
 * Created by b00lean on 3/8/15.
 */
public class OrderBookResponse {
    private BigDecimal[][] asks;
    private BigDecimal[][] bids;
    private String isFrozen;


    public BigDecimal[][] getAsks() {
        return asks;
    }

    public void setAsks(BigDecimal[][] asks) {
        this.asks = asks;
    }

    public BigDecimal[][] getBids() {
        return bids;
    }

    public void setBids(BigDecimal[][] bids) {
        this.bids = bids;
    }

    public String getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
    }
}
