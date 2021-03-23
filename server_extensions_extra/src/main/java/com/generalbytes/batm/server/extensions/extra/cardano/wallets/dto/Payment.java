package com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto;

import java.math.BigDecimal;

public class Payment {
    private String address;
    private Amount amount;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
