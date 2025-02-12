/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to create an order.
 *
 * @see <a href="https://docs.cdp.coinbase.com/advanced-trade/reference/retailbrokerageapi_postorder">Coinbase Documentation</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoinbaseCreateOrderRequest {

    /**
     * A unique ID provided for the order (used for identification). (required)
     * If the ID provided is not unique, the order will not be created,
     * and the order corresponding with that ID will be returned instead.
     */
    @JsonProperty("client_order_id")
    private String clientOrderId;
    /**
     * The trading pair (e.g. 'BTC-USD'). (required)
     */
    @JsonProperty("product_id")
    private String productId;
    /**
     * The side of the market that the order is on (e.g. 'BUY', 'SELL'). (required)
     */
    private CoinbaseOrderSide side;
    /**
     * The configuration of the order (e.g., the order type, size, etc.). (required)
     */
    @JsonProperty("order_configuration")
    private CoinbaseOrderConfiguration orderConfiguration;

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public CoinbaseOrderSide getSide() {
        return side;
    }

    public void setSide(CoinbaseOrderSide side) {
        this.side = side;
    }

    public CoinbaseOrderConfiguration getOrderConfiguration() {
        return orderConfiguration;
    }

    public void setOrderConfiguration(CoinbaseOrderConfiguration orderConfiguration) {
        this.orderConfiguration = orderConfiguration;
    }
}
