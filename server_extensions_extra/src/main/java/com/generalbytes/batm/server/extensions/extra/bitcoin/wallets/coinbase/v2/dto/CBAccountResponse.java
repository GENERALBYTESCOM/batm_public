package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBAccountResponse extends CBResponse{
    private CBAccount data;


    public CBAccount getData() {
        return data;
    }

    public void setData(CBAccount data) {
        this.data = data;
    }
}
