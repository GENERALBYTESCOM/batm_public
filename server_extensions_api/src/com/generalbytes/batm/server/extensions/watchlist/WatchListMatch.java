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


public class WatchListMatch implements Serializable{
    private int score;
    private String details;
    private String matchedWatchListName;


    public WatchListMatch(int score, String details, String matchedWatchListName) {
        this.score = score;
        this.details = details;
        this.matchedWatchListName = matchedWatchListName;
    }

    public int getScore() {
        return score;
    }

    public String getDetails() {
        return details;
    }

    public String getMatchedWatchListName() {
        return matchedWatchListName;
    }

    @Override
    public String toString() {
        return "WatchlistMatch{" +
                "score=" + score +
                ", details='" + details + '\'' +
                ", matchedWatchListName=" + matchedWatchListName +
                '}';
    }
}
