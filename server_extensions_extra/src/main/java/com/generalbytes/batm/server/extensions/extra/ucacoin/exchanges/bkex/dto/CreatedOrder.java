package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account details of a registered user, including balances.
 **/
public class CreatedOrder {

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private String orderId;

    @JsonProperty("status")
    private String status;

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

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "{" +
            " msg='" + msg + "'" +
            ", code='" + code + "'" +
            ", orderId='" + orderId + "'" +
            ", status='" + status + "'" +
            "}";
    }

}

