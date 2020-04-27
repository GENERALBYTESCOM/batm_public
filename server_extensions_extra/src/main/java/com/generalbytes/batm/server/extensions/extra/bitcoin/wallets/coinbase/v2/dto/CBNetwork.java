package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

/**
 * Created by b00lean on 30.7.17.
 */

public class CBNetwork {
    private String status;
    private String hash;
    private String name;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
