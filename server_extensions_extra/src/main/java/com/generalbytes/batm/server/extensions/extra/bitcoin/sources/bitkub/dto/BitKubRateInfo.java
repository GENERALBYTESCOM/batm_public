/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
 * Author   :  pawel.nowacki@teleit.pl / +48.600100825 - wanda.exchange
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bitkub.dto;

import java.math.BigDecimal;

public class BitKubRateInfo {
    private int id;
    private BigDecimal last;
    private BigDecimal lowestAsk;
    private BigDecimal highestBid;
    private BigDecimal percentChange;
    private BigDecimal baseVolume;
    private BigDecimal quoteVolume;
    private boolean isFrozen;
    private BigDecimal high24hr;
    private BigDecimal low24hr;

    public int getId() {
        return id;
    }

    public BigDecimal getLast() {
        return last;
    }

    public BigDecimal getLowestAsk() {
        return lowestAsk;
    }

    public BigDecimal getHighestBid() {
        return highestBid;
    }

    public BigDecimal getPercentChange() {
        return percentChange;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public BigDecimal getHigh24hr() {
        return high24hr;
    }

    public BigDecimal getLow24hr() {
        return low24hr;
    }

}