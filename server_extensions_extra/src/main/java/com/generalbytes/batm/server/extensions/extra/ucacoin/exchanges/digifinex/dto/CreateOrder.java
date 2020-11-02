package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * A wrapper for accepted order types which can be submitted for execution.
 **/
public class CreateOrder {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("type")
    private String type;
    @JsonProperty("amount")
    private float amount;
    @JsonProperty("price")
    private float price;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override public String toString() {
        return "CreateOrder{" +
            "symbol='" + symbol + '\'' +
            ", type=" + type +
            ", amount=" + amount +
            ", price=" + price +
            '}';
    }
}

