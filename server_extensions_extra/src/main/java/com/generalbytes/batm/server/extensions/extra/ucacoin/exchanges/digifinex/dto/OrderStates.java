package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderStates {

    @JsonProperty("data")
    private List<OrderState> orderStates;
    
    public List<OrderState> getOrderStates() {
        return orderStates;
    }
}
