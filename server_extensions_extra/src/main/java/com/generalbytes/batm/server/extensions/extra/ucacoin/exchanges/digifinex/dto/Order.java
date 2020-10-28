package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {

    @JsonProperty("code")
    private String code;

    @JsonProperty("order_id")
    private String orderId;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "ORDER{} code= " + code + 
        ", orderId=" +  orderId;
    }
    
}
