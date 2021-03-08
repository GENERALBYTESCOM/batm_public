package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account details of a registered user, including balances.
 **/
public class Account {

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private Balances balances;

    public Balances getWallet() {
        return balances;
    }

    public void setBalances(
        Balances balances) {
        this.balances = balances;
    }

    @Override public String toString() {
        return "Account{" +
            " balances=" + balances.toString()+
            ", code=" + code +"}";
    }
}

