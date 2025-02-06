package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Paginated response with multiple addresses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseAddressesResponse {

    private CoinbasePagination pagination;
    @JsonProperty("data")
    private List<CoinbaseAddress> addresses;

    public CoinbasePagination getPagination() {
        return pagination;
    }

    public void setPagination(CoinbasePagination pagination) {
        this.pagination = pagination;
    }

    public List<CoinbaseAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<CoinbaseAddress> addresses) {
        this.addresses = addresses;
    }
}
