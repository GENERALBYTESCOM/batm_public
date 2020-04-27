package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBSendResponse extends CBResponse{
    private CBSend data;

    public CBSend getData() {
        return data;
    }
    public void setData(CBSend data) {
        this.data = data;
    }
}
