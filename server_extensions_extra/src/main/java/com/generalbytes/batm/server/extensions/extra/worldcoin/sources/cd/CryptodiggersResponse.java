/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.worldcoin.sources.cd;

import com.generalbytes.batm.server.extensions.extra.worldcoin.sources.cd.*;
import java.math.BigDecimal;

public class CryptodiggersResponse {
    
    private BigDecimal exch_rate;
    private BigDecimal exch_rate_sell;
    private BigDecimal exch_rate_buy;
    private int error;
    String error_msg;
    
    public BigDecimal getexch_rate() {
        return exch_rate;
    }

    public void setexch_rate(BigDecimal exch_rate) {
        this.exch_rate = exch_rate;
    }
    
    public BigDecimal getexch_rate_sell() {
        return exch_rate_sell;
    }

    public void setexch_rate_sell(BigDecimal exch_rate_sell) {
        this.exch_rate_sell = exch_rate_sell;
    }
    
    public BigDecimal getexch_rate_buy() {
        return exch_rate_buy;
    }

    public void setexch_rate_buy(BigDecimal exch_rate_buy) {
        this.exch_rate_buy = exch_rate_buy;
    }
    
    public int geterror() {
        return error;
    }

    public void seterror(int error) {
        this.error = error;
    }
    
    public String geterror_msg() {
        return error_msg;
    }

    public void seterror_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
