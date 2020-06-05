package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * A wrapper for accepted order types which can be submitted for execution.
 **/
public class CreateOrder {

    @JsonProperty("instrument_code")
    private String instrumentCode;
    @JsonProperty("type")
    private Order.Type type;
    @JsonProperty("side")
    private Side side;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("client_id")
    private UUID clientId;

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public Order.Type getType() {
        return type;
    }

    public void setType(Order.Type type) {
        this.type = type;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    @Override public String toString() {
        return "CreateOrder{" +
            "instrumentCode='" + instrumentCode + '\'' +
            ", type=" + type +
            ", side=" + side +
            ", amount=" + amount +
            ", price=" + price +
            ", clientId=" + clientId +
            '}';
    }
}

