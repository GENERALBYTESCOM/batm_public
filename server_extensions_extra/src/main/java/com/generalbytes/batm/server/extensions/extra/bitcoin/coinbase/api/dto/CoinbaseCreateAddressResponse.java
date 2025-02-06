package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response holding a newly created address.
 */
public class CoinbaseCreateAddressResponse {

    @JsonProperty("data")
    private CoinbaseAddress address;

    /**
     * @return The newly created address.
     */
    public CoinbaseAddress getAddress() {
        return address;
    }

    public void setAddress(CoinbaseAddress address) {
        this.address = address;
    }
}
