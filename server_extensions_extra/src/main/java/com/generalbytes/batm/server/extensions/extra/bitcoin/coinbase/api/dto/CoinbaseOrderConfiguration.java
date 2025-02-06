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
 * The configuration of the order (e.g., the order type, size, etc.).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoinbaseOrderConfiguration {

    @JsonProperty("market_market_ioc")
    private CoinbaseMarketOrderConfiguration marketOrderConfiguration;

    public CoinbaseMarketOrderConfiguration getMarketOrderConfiguration() {
        return marketOrderConfiguration;
    }

    public void setMarketOrderConfiguration(CoinbaseMarketOrderConfiguration marketOrderConfiguration) {
        this.marketOrderConfiguration = marketOrderConfiguration;
    }
}
