package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateDepositAddress {
    @JsonProperty("currency")
    private String currency;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override public String toString() {
        return "CreateDepositAddress{" +
            "currency='" + currency + '\'' +
            '}';
    }
}
