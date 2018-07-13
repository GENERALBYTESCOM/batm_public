package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BtTickerDto {

    @JsonProperty("Ask")
    private Double ask;

    @JsonProperty("Bid")
    private Double bid;

    @JsonProperty("Last")
    private Double last;

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public Double getLast() {
        return last;
    }

    public void setLast(Double last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return "BtTickerDto{" +
            "ask=" + ask +
            ", bid=" + bid +
            ", last=" + last +
            '}';
    }
}
