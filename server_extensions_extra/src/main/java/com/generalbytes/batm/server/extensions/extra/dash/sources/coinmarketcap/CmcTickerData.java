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
package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import java.util.Map;

public class CmcTickerData {
    private Map<String, CmcTickerQuote> quotes;

    public Map<String, CmcTickerQuote> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, CmcTickerQuote> quotes) {
        this.quotes = quotes;
    }

    @Override
    public String toString() {
        return "CmcTickerData{" +
            "quotes=" + quotes +
            '}';
    }
}


