package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto;

public class BlockIOTransactionSignature {
    private Integer input_index;
    private String public_key;
    private String signature;

    public Integer getInput_index() {
        return input_index;
    }

    public void setInput_index(Integer input_index) {
        this.input_index = input_index;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
