package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

/**
 * Response containing a single {@link CoinbaseOrder}.
 */
public class CoinbaseOrderResponse {

    private CoinbaseOrder order;

    /**
     * @return The requested order.
     */
    public CoinbaseOrder getOrder() {
        return order;
    }

    public void setOrder(CoinbaseOrder order) {
        this.order = order;
    }
}
