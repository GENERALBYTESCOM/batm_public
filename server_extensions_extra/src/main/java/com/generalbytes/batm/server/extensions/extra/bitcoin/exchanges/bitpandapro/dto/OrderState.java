package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Active or Inactive order with status __FILLED__, __FILLED_FULLY__, __FILLED_CLOSED__ and __FILLED_REJECTED__.
 **/
public class OrderState {

    @JsonProperty("order")
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override public String toString() {
        return "OrderState{" +
            "order=" + order +
            '}';
    }
}

