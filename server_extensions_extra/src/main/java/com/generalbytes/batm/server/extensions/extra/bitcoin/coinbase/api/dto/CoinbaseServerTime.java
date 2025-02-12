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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the current time of the Coinbase API server.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseServerTime {

    private String iso;
    private long epoch;

    /**
     * @return The server time in iso format.
     */
    public String getIso() {
        return iso;
    }

    /**
     * @param iso The server time in iso format.
     */
    public void setIso(String iso) {
        this.iso = iso;
    }

    /**
     * @return The server time in epoch format.
     */
    public long getEpoch() {
        return epoch;
    }

    /**
     * @param epoch The server time in epoch format.
     */
    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }
}