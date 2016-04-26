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

package com.generalbytes.batm.server.extensions.watchlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WatchListResult implements Serializable{
    public static final int RESULT_TYPE_WATCHLIST_SEARCHED = 0;
    public static final int RESULT_TYPE_WATCHLIST_NOT_READY = 1;

    private int resultType = RESULT_TYPE_WATCHLIST_NOT_READY;
    private List<WatchListMatch> matches = new ArrayList<WatchListMatch>();


    public WatchListResult(int resultType) {
        this.resultType = resultType;
    }

    public WatchListResult(List<WatchListMatch> matches) {
        this.matches = matches;
        this.resultType=RESULT_TYPE_WATCHLIST_SEARCHED;
    }

    public List<WatchListMatch> getMatches() {
        return matches;
    }

    public int getResultType() {
        return resultType;
    }

    @Override
    public String toString() {
        return "WatchListResult{" +
                "resultType=" + resultType +
                ", matches=" + matches +
                '}';
    }
}
