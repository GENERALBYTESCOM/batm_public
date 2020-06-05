package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Order {

    @JsonProperty("order_id")
    private UUID orderId;
    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("instrument_code")
    private String instrumentCode;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("filled_amount")
    private BigDecimal filledAmount;
    @JsonProperty("side")
    private Side side;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("reason")
    private String reason;
    @JsonProperty("time_in_force")
    private String timeInForce;

    public enum Type {

        LIMIT("LIMIT"), MARKET("MARKET"), STOP("STOP");

        private final String value;

        Type(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static Type fromValue(String value) {
            for (Type b : Type.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum Status {

        OPEN("OPEN"), STOP_TRIGGERED("STOP_TRIGGERED"), FILLED(
            "FILLED"), FILLED_FULLY("FILLED_FULLY"), FILLED_CLOSED(
            "FILLED_CLOSED"), FILLED_REJECTED("FILLED_REJECTED"), REJECTED(
            "REJECTED"), CLOSED("CLOSED"), FAILED("FAILED");

        private final String value;

        Status(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static Status fromValue(String value) {
            for (Status b : Status.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            return null;
        }
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFilledAmount() {
        return filledAmount;
    }

    public void setFilledAmount(BigDecimal filledAmount) {
        this.filledAmount = filledAmount;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    @Override public String toString() {
        return "Order{" +
            "orderId=" + orderId +
            ", accountId=" + accountId +
            ", instrumentCode='" + instrumentCode + '\'' +
            ", amount=" + amount +
            ", filledAmount=" + filledAmount +
            ", side=" + side +
            ", type=" + type +
            ", status=" + status +
            ", price=" + price +
            ", reason='" + reason + '\'' +
            ", timeInForce='" + timeInForce + '\'' +
            '}';
    }
}

