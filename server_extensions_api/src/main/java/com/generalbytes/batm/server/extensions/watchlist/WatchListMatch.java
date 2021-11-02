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
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.watchlist;

import java.io.Serializable;

public class WatchListMatch implements Serializable {
    private int score;
    private String details;
    private String watchlistCode;
    private String watchlistName;
    private String partyId;

    /**
     * @deprecated Will be removed in future. Use
     *   WatchListMatch(int score, String details, String watchlistCode, String watchlistName, String partyId) instead.
     */
    @Deprecated
    public WatchListMatch(int score, String details, String watchlistName) {
        this(score, details, null, watchlistName, null);
    }

    public WatchListMatch(int score, String details, String watchlistCode, String watchlistName, String partyId) {
        this.score = score;
        this.details = details;
        this.watchlistCode = watchlistCode;
        this.watchlistName = watchlistName;
        this.partyId = partyId;
    }

    public int getScore() {
        return score;
    }

    public String getDetails() {
        return details;
    }

    public String getWatchlistCode() {
        return watchlistCode;
    }

    public String getWatchlistName() {
        return watchlistName;
    }

    /**
     * @deprecated Will be removed in future. Direct replacement is getWatchlistName().
     */
    @Deprecated
    public String getMatchedWatchListName() {
        return watchlistName;
    }

    public String getPartyId() {
        return partyId;
    }

    @Override
    public String toString() {
        return "WatchlistMatch{" +
                "score=" + score +
                ", details='" + details + '\'' +
                ", watchlistCode=" + watchlistCode +
                ", matchedWatchListName=" + watchlistName +
                ", partyId=" + partyId +
                '}';
    }
}
