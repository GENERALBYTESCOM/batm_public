package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Statistics on market activity within the last 24 hours. Market ticks are updated on every trade of a market and use a sliding window with minutely granularity to calculate statistics on activity with the last 24 hours.
 **/
/*
{
    "ticker": [{
        "vol": 40717.4461,
        "change": -1.91,
        "base_vol": 392447999.65374,
        "sell": 9592.23,
        "last": 9592.22,
        "symbol": "btc_usdt",
        "low": 9476.24,
        "buy": 9592.03,
        "high": 9793.87
    }],
    "date": 1589874294,
    "code": 0
}
*/
public class MarketTick {
    @JsonProperty("symbol")
    private String instrumentCode;
    @JsonProperty("last")
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

