package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tick {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("close")
    private BigDecimal close;

    @JsonProperty("open")
    private BigDecimal open;

    @JsonProperty("high")
    private BigDecimal high;

    @JsonProperty("low")
    private BigDecimal low;

    @JsonProperty("volume")
    private BigDecimal volume;

    @JsonProperty("quoteVolume")
    private BigDecimal quoteVolume;

    @JsonProperty("change")
    private BigDecimal change;

    @JsonProperty("ts")
    private int ts;

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getClose() {
        return this.close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getOpen() {
        return this.open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return this.high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return this.low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getVolume() {
        return this.volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getQuoteVolume() {
        return this.quoteVolume;
    }

    public void setQuoteVolume(BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public BigDecimal getChange() {
        return this.change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public int getTs() {
        return this.ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }
}
