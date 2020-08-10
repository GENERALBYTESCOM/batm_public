package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Either a price level or a single order in the order book.
 **/
public class OrderBookEntry {

    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("number_of_orders")
    private Integer numberOfOrders;
    @JsonProperty("order_id")
    private UUID orderId;

    public boolean isPriceLevel() {
        return numberOfOrders != null;
    }

    public boolean isOrder() {
        return orderId != null;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(Integer numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    @Override public String toString() {
        return "OrderBookEntry{" +
            "price=" + price +
            ", amount=" + amount +
            ", numberOfOrders=" + numberOfOrders +
            ", orderId=" + orderId +
            '}';
    }
}

