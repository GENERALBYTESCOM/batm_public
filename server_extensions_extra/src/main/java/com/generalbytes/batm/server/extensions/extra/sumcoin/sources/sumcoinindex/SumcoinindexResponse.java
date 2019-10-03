/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.sumcoin.sources.sumcoinindex;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SumcoinindexResponse {

    @JsonProperty("exch_rate")
    private BigDecimal exchRate;
    @JsonProperty("exch_rate_sell")
    private BigDecimal exchRateSell;
    @JsonProperty("exch_rate_buy")
    private BigDecimal exchRateBuy;
    private int error;
    @JsonProperty("error_msg")
    private String errorMsg;

    public BigDecimal getExchRate() {
        return exchRate;
    }

    public void setExchRate(BigDecimal exchRate) {
        this.exchRate = exchRate;
    }

    public BigDecimal getExchRateSell() {
        return exchRateSell;
    }

    public void setExchRateSell(BigDecimal exchRateSell) {
        this.exchRateSell = exchRateSell;
    }

    public BigDecimal getExchRateBuy() {
        return exchRateBuy;
    }

    public void setExchRateBuy(BigDecimal exchRateBuy) {
        this.exchRateBuy = exchRateBuy;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
