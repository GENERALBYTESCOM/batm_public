package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account details of a registered user, including balances.
 **/
public class Account {

    @JsonProperty("list")
    private List<Balance> balances = new ArrayList<>();

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(
        List<Balance> balances) {
        this.balances = balances;
    }

    @Override public String toString() {
        return "Account{" +
            ", balances=" + balances +
            '}';
    }
}

