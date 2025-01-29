package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

/**
 * Represents the current time of the Coinbase API server.
 */
public class CoinbaseServerTime {

    private long epoch;

    /**
     * @return The server time in epoch format.
     */
    public long getEpoch() {
        return epoch;
    }

    /**
     * @param epoch The server time in epoch format.
     */
    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }
}