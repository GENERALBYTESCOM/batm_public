/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.watchlists.czech;

import com.generalbytes.batm.server.extensions.extra.watchlists.Match;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;
import com.generalbytes.batm.server.extensions.watchlist.WatchListMatch;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.generalbytes.batm.server.extensions.watchlist.WatchListQuery.*;

public class CzechSanctionList implements IWatchList{

    private Sanctions sanctions;

    @Override
    public void init(String downloadDirectory) {
        //Nothing to do
    }

    @Override
    public String getName() {
        return "Czech - Sanction List";
    }

    @Override
    public String getId() {
        return "cz";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getDescription() {
        return "Hardcoded, never downloaded.";
    }

    @Override
    public int recommendedRefreshPeriodInMins() {
        return Integer.MAX_VALUE; //Never
    }

    @Override
    public int refresh() {
        return LIST_NOT_CHANGED;
    }


    @Override
    public WatchListResult search(WatchListQuery query) {
        synchronized (this) {
            if (sanctions == null) {
                sanctions = new Sanctions();
            }
        }

        //do the actual matching
        Set<Match> result = new HashSet<>();

        if (query.getType() == TYPE_INDIVIDUAL) {
            result = sanctions.search(query.getFirstName(), query.getLastName());
        }else if (query.getType() == TYPE_ENTITY){
            result = sanctions.search(query.getName());
        }

        if (result.isEmpty()) {
            return new WatchListResult(WatchListResult.RESULT_TYPE_WATCHLIST_SEARCHED);
        }else{
            final ArrayList<WatchListMatch> matches = new ArrayList<>();
            for (Match match : result) {
                final String partyIndex = sanctions.getPartyIndexByPartyId(match.getPartyId());
                matches.add(new WatchListMatch(match.getScore(), "Matched Czech Sanction list. PartyIndex: " + partyIndex + ".", getId(), getName(), match.getPartyId()));
            }
            return new WatchListResult(matches);
        }
    }
}
