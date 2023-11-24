package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

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
