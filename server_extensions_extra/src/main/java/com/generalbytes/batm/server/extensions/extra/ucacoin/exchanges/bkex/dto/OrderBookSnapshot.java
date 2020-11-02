package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A wrapper for two different layout of orderbook snapshot responses depending on which level is queried.
 **/

 /*


"time": 1555049455783,
  "bids": [
   ["78.82", "0.526"],//[Price, Quantity]
   ["77.24", "1.22"],
   ["76.65", "1.043"],
   ["76.58", "1.34"],
   ["75.67", "1.52"],
   ["75.12", "0.635"],
   ["75.02", "0.72"],
   ["75.01", "0.672"],
   ["73.73", "1.282"],
   ["73.58", "1.116"],
   ["73.45", "0.471"],
   ["73.44", "0.483"],
   ["72.32", "0.383"],
   ["72.26", "1.283"],
   ["72.11", "0.703"],
   ["70.61", "0.454"]],
   "asks": [
     ["122.96", "0.381"],//[Price, Quantity]
     ["144.46", "1"],
     ["155.55", "0.065"],
     ["160.16", "0.052"],
     ["200", "0.775"],
     ["249", "0.17"],
     ["250", "1"],
     ["300", "1"],
     ["400", "1"],
     ["499", "1"]]
   }
   */
public class OrderBookSnapshot   {

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
            ", asks=" + asks + "}";
    }
}

