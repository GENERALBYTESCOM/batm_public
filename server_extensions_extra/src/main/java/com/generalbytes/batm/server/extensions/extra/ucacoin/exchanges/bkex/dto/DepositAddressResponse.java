package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account details of a registered user, including balances.
 **/
public class DepositAddressResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private List<DepositAddress> addresses = new ArrayList<>();

    
    public List<DepositAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(
        List<DepositAddress> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "{" +
            " code='" + code + "'" +
            ", msg='" + msg + "'" +
            ", addresses='" + addresses + "'" +
            "}";
    }

}

