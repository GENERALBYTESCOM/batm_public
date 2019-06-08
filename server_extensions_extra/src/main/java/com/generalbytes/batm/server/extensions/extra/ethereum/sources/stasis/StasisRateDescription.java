package com.generalbytes.batm.server.extensions.extra.ethereum.sources.stasis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class StasisRateDescription {
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("limit")
    private BigDecimal limit;
    @JsonProperty("rate")
    private BigDecimal rate;

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = new BigDecimal(rate);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = Long.valueOf(timestamp);
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = new BigDecimal(limit);
    }
}
