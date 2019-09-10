package com.generalbytes.batm.server.extensions.extra.potcoin.wallets.potwallet;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class PotwalletSendRequest {
    @JsonProperty("address")
    String address;

    @JsonProperty("amount")
    BigDecimal amount;

    public PotwalletSendRequest(String address, BigDecimal amount) {
        this.address = address;
        this.amount = amount;
    }
}
