package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

public class BitGoCreateAddressResponse {
    private String id;
    private String address;
    private Integer index;
    private String coin;
    private String wallet;
    private String label;

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public Integer getIndex() {
        return index;
    }

    public String getCoin() {
        return coin;
    }

    public String getWallet() {
        return wallet;
    }

    public String getLabel() {
        return label;
    }
}
