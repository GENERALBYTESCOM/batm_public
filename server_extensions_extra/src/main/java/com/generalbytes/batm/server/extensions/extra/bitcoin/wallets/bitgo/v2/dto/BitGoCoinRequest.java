package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

public class BitGoCoinRequest {
    private String address;
    private Integer amount;
    private String walletPassphrase;

    public BitGoCoinRequest(String address, Integer amount, String walletPassphrase) {
        this.address = address;
        this.amount = amount;
        this.walletPassphrase = walletPassphrase;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getWalletPassphrase() {
        return walletPassphrase;
    }

    public void setWalletPassphrase(String walletPassphrase) {
        this.walletPassphrase = walletPassphrase;
    }
}
