/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.luno;

import java.math.BigDecimal; 
import com.fasterxml.jackson.annotation.JsonProperty;

public class LunoTickerData {
    
    @JsonProperty("pair")
    private String pair;
    
    @JsonProperty("timestamp")
    private BigDecimal timestamp;

    @JsonProperty("bid")
    private BigDecimal bid;

    @JsonProperty("ask")
    private BigDecimal ask;

    @JsonProperty("last_trade")
    private BigDecimal last_trade;

    @JsonProperty("rolling_24_hour_volume")
    private BigDecimal rolling_24_hour_volume;

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public BigDecimal getPrice() {
        return bid;
    }

    public void setPrice(BigDecimal bid) {
        this.bid = bid;
    }
}
