package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A wrapper for two different layout of orderbook snapshot responses depending on which level is queried.
 **/
public class OrderBookResponse   {

  @JsonProperty("code")
  private String code;
  @JsonProperty("msg")
  private String msg;
  @JsonProperty("data")
  private OrderBookSnapshot snapshot;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public OrderBookSnapshot getSnapshot() {
        return this.snapshot;
    }

    public void setSnapshot(OrderBookSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override public String toString() {
        return "OrderBookResponse{" +
            " code=" + code.toString() +
            ", msg=" + msg +
            "data=" + snapshot + " }";
    }
}

