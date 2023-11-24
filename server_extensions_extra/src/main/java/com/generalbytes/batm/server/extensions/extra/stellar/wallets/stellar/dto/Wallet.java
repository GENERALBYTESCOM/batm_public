package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

public class Wallet {
	private String id;
    private Balance balance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}
