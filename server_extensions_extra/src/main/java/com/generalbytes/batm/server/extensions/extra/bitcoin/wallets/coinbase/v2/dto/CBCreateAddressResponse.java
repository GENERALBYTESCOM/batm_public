package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

public class CBCreateAddressResponse extends CBResponse{
    private CBAddress data;

    public CBAddress getData() {
        return data;
    }

    public void setData(CBAddress data) {
        this.data = data;
    }
}
