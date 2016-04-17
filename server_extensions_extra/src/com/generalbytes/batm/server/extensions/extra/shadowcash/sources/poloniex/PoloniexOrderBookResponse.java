/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.shadowcash.sources.poloniex;

import java.math.BigDecimal;

public class PoloniexOrderBookResponse {
    private BigDecimal[][] asks;
    private BigDecimal[][] bids;
    private String isFrozen;


    public BigDecimal[][] getAsks() {
        return asks;
    }

    public void setAsks(BigDecimal[][] asks) {
        this.asks = asks;
    }

    public BigDecimal[][] getBids() {
        return bids;
    }

    public void setBids(BigDecimal[][] bids) {
        this.bids = bids;
    }

    public String getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
    }
}
