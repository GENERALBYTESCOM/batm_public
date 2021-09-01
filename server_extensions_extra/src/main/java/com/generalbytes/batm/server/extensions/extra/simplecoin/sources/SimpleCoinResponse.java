package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

import java.math.BigDecimal;

public class SimpleCoinResponse {

    private BigDecimal rate;

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

}