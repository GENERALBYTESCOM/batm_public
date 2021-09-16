/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

import java.math.BigDecimal;

public class SimpleCoinResponse {
    private BigDecimal rate;
    private BigDecimal rate_inverse;

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getRate_inverse() {
        return rate_inverse;
    }

    public void setRate_inverse(BigDecimal rate_inverse) {
        this.rate_inverse = rate_inverse;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}