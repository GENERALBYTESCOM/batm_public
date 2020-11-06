package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Statistics on market activity within the last 24 hours. Market ticks are updated on every trade of a market and use a sliding window with minutely granularity to calculate statistics on activity with the last 24 hours.
 **/
/*
{
  "msg": "success",
  "code": "0",
  "data": [
    {
      "symbol": "BTC_USDT",
      "close": 7145.0407,
      "open": 7145.0407,
      "high": 7145.0407,
      "low": 7145.0407,
      "volume": 3124.1231,
      "quoteVolume": 23131.847291,
      "change": 0.12,
      "ts": 1529739295000
    }
  ]
}
*/
public class MarketTick {


    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private String code;
    @JsonProperty("data")
    private List<Tick> ticks;

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Tick> getTicks() {
        return this.ticks;
    }

    public void setTicks(List<Tick> ticks) {
        this.ticks = ticks;
    }

    @Override public String toString() {
        return "MarketTick{" +
            "msg='" + msg + '\'' +
            ", code=" + code +
            ", ticks=" + ticks +
            '}';
    }
}

