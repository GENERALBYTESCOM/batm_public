package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBError {
    private String id;
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
