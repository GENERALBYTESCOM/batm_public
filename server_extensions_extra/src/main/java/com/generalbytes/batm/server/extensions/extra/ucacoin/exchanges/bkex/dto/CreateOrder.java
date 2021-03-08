package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import javax.ws.rs.FormParam;


/**
 * A wrapper for accepted order types which can be submitted for execution.
 * {
  "volume": 0.1,
  "price": 7000.12,
  "direction": "ASK",
  "symbol": "BTC_USDT",
  "source": "WALLET",
  "type": "STOP_LIMIT",
  "stopPrice": 6900,
  "operator": "gte",
}
 **/
public class CreateOrder {

    @FormParam("direction")
    private String direction;
    @FormParam("price")
    private float price;
    @FormParam("source")
    private String source = "WALLET";
    @FormParam("symbol")
    private String symbol;
    @FormParam("type")
    private String type = "LIMIT";
    @FormParam("volume")
    private float volume;

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override public String toString() {
        return "CreateOrder{" +
            "symbol='" + symbol + '\'' +
            ", type=" + type +
            ", volume=" + volume +
            ", price=" + price +
            '}';
    }
}

