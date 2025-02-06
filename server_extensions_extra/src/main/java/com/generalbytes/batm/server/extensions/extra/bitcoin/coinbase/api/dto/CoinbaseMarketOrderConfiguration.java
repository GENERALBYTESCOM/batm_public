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
 * Buy or sell a specified quantity of an Asset at the current best available market price.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoinbaseMarketOrderConfiguration {

    /**
     * The amount of the second Asset in the Trading Pair.
     * For example, on the BTC/USD Order Book, USD is the Quote Asset.
     * If set, {@code baseSize} cannot be set.
     */
    @JsonProperty("quote_size")
    private String quoteSize;
    /**
     * The amount of the first Asset in the Trading Pair.
     * For example, on the BTC-USD Order Book, BTC is the Base Asset.
     * If set, {@code quoteSize} cannot be set.
     */
    @JsonProperty("base_size")
    private String baseSize;

    public String getQuoteSize() {
        return quoteSize;
    }

    public void setQuoteSize(String quoteSize) {
        this.quoteSize = quoteSize;
    }

    public String getBaseSize() {
        return baseSize;
    }

    public void setBaseSize(String baseSize) {
        this.baseSize = baseSize;
    }
}
