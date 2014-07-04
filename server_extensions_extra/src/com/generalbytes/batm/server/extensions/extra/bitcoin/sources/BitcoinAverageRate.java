/*************************************************************************************
 * Copyright (C) 2014 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.sources;

import java.math.BigDecimal;

public class BitcoinAverageRate {
    private BigDecimal ask;
    private BigDecimal bid;
    private BigDecimal last;
    private String timestamp;
    private BigDecimal volume_btc;
    private BigDecimal volume_percent;

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getVolume_btc() {
        return volume_btc;
    }

    public void setVolume_btc(BigDecimal volume_btc) {
        this.volume_btc = volume_btc;
    }

    public BigDecimal getVolume_percent() {
        return volume_percent;
    }

    public void setVolume_percent(BigDecimal volume_percent) {
        this.volume_percent = volume_percent;
    }
}
