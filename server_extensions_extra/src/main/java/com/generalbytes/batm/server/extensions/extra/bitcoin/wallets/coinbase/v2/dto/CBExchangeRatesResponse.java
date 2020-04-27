package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

public class CBExchangeRatesResponse extends CBResponse{
    private CBExchangeRates data;

    public CBExchangeRates getData() {
        return data;
    }

    public void setData(CBExchangeRates data) {
        this.data = data;
    }
}
