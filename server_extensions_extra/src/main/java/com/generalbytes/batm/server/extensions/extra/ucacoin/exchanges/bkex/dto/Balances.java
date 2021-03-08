package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account balance for one single currency
 **/
public class Balances   {

    @JsonProperty("WALLET")
    private List<Balance> balances;

    public List<Balance> getBalances() {
        return this.balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    @Override
    public String toString() {
        return "{" +
            " balances='" + balances + "'" +
            "}";
    }

}

