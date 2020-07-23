package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account details of a registered user, including balances.
 **/
public class Account {

    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("balances")
    private List<Balance> balances = new ArrayList<>();

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(
        List<Balance> balances) {
        this.balances = balances;
    }

    @Override public String toString() {
        return "Account{" +
            "accountId=" + accountId +
            ", balances=" + balances +
            '}';
    }
}

