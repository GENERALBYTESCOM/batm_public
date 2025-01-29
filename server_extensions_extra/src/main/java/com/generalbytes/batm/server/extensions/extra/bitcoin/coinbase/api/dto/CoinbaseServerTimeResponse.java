package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response with the current time of a Coinbase API server.
 */
public class CoinbaseServerTimeResponse {

    @JsonProperty("data")
    private CoinbaseServerTime time;

    /**
     * @return The time of the Coinbase API server.
     */
    public CoinbaseServerTime getTime() {
        return time;
    }

    /**
     * @param time The time of the Coinbase API server.
     */
    public void setTime(CoinbaseServerTime time) {
        this.time = time;
    }
}
