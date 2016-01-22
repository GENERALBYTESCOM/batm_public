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
package com.generalbytes.batm.server.extensions.extra.maxcoin.sources;

import java.math.BigDecimal;

public class MaxcoinTickerResponse {
    private BigDecimal btcusd;
    private BigDecimal btceuro;
    private BigDecimal btcgbp;
    private BigDecimal btccny;
    private BigDecimal mpbtc;
    private BigDecimal mpusd;
    private BigDecimal mpeuro;
    private BigDecimal mpgbp;
    private BigDecimal mpcny;

    public BigDecimal getBtcusd() {
        return btcusd;
    }

    public void setBtcusd(BigDecimal btcusd) {
        this.btcusd = btcusd;
    }

    public BigDecimal getBtceuro() {
        return btceuro;
    }

    public void setBtceuro(BigDecimal btceuro) {
        this.btceuro = btceuro;
    }

    public BigDecimal getBtcgbp() {
        return btcgbp;
    }

    public void setBtcgbp(BigDecimal btcgbp) {
        this.btcgbp = btcgbp;
    }

    public BigDecimal getBtccny() {
        return btccny;
    }

    public void setBtccny(BigDecimal btccny) {
        this.btccny = btccny;
    }

    public BigDecimal getMpbtc() {
        return mpbtc;
    }

    public void setMpbtc(BigDecimal mpbtc) {
        this.mpbtc = mpbtc;
    }

    public BigDecimal getMpusd() {
        return mpusd;
    }

    public void setMpusd(BigDecimal mpusd) {
        this.mpusd = mpusd;
    }

    public BigDecimal getMpeuro() {
        return mpeuro;
    }

    public void setMpeuro(BigDecimal mpeuro) {
        this.mpeuro = mpeuro;
    }

    public BigDecimal getMpgbp() {
        return mpgbp;
    }

    public void setMpgbp(BigDecimal mpgbp) {
        this.mpgbp = mpgbp;
    }

    public BigDecimal getMpcny() {
        return mpcny;
    }

    public void setMpcny(BigDecimal mpcny) {
        this.mpcny = mpcny;
    }
}
