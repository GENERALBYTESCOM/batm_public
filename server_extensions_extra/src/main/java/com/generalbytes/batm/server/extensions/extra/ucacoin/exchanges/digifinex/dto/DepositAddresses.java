package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Account details of a registered user, including balances.
 **/
public class DepositAddresses {

    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private List<DepositAddress> addresses = new ArrayList<>();

    
    public List<DepositAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(
        List<DepositAddress> addresses) {
        this.addresses = addresses;
    }

    @Override public String toString() {
        return "DepositAddresses{" +
            " addresses=" + addresses +
            ", code=" + code +"}";
    }
}

