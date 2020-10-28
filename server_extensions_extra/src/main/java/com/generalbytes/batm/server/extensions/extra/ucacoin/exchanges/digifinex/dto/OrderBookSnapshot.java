package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A wrapper for two different layout of orderbook snapshot responses depending on which level is queried.
 **/
public class OrderBookSnapshot   {

  @JsonProperty("code")
  private String code;
  @JsonProperty("bids")
  private Object bids;
  @JsonProperty("asks")
  private Object asks;

    public List<List<Float>> getBids() {
        return (List<List<Float>>)bids;
    }

    public void setBids(List<List<Float>> bids) {
        this.bids = bids;
    }

    public List<List<Float>> getAsks() {
        return (List<List<Float>>)asks;
    }

    public void setAsks(List<List<Float>> asks) {
        this.asks = asks;
    }

    @Override public String toString() {
        return "OrderBookSnapshot{" +
            " bids=" + bids.toString() +
            ", asks=" + asks +
            "code=" + code + " }";
    }
}

