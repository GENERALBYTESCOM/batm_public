package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Request to create a new address.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoinbaseCreateAddressRequest {

    private String name;

    public String getName() {
        return name;
    }

    /**
     * @param name Name of the address. (Optional)
     */
    public void setName(String name) {
        this.name = name;
    }
}
