package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A wrapper for two different layout of orderbook snapshot responses depending on which level is queried.
 **/
public class OrderBookSnapshot   {

  @JsonProperty("instrument_code")
  private String instrumentCode;
  @JsonProperty("bids")
  private List<OrderBookEntry> bids;
  @JsonProperty("asks")
  private List<OrderBookEntry> asks;

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public List<OrderBookEntry> getBids() {
        return bids;
    }

    public void setBids(List<OrderBookEntry> bids) {
        this.bids = bids;
    }

    public List<OrderBookEntry> getAsks() {
        return asks;
    }

    public void setAsks(List<OrderBookEntry> asks) {
        this.asks = asks;
    }

    @Override public String toString() {
        return "OrderBookSnapshot{" +
            "instrumentCode='" + instrumentCode + '\'' +
            ", bids=" + bids +
            ", asks=" + asks +
            '}';
    }
}

