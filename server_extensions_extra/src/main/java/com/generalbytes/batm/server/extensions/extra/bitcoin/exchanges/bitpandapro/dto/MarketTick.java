package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Statistics on market activity within the last 24 hours. Market ticks are updated on every trade of a market and use a sliding window with minutely granularity to calculate statistics on activity with the last 24 hours.
 **/
public class MarketTick {
    @JsonProperty("instrument_code")
    private String instrumentCode;
    @JsonProperty("last_price")
    private BigDecimal lastPrice;

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    @Override public String toString() {
        return "MarketTick{" +
            "instrumentCode='" + instrumentCode + '\'' +
            ", lastPrice=" + lastPrice +
            '}';
    }
}

