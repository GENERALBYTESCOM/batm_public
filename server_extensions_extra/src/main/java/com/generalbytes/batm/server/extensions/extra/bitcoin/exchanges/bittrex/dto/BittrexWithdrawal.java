package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto;

/**
 * Response DTO matching only ID from https://bittrex.github.io/api/v3#/definitions/Withdrawal
 */
//TODO: BATM-2327 remove this class
public class BittrexWithdrawal {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
