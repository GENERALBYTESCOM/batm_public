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

package com.generalbytes.batm.server.extensions.extra.watchlists;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.CaWatchList;
import com.generalbytes.batm.server.extensions.extra.watchlists.czech.CzechSanctionList;
import com.generalbytes.batm.server.extensions.extra.watchlists.eu.EUSanctionsList;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.OFACWatchList;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.util.HashSet;
import java.util.Set;

public class BasicWatchlistsExtension extends AbstractExtension{
    private final IWatchList ofacWatchList = new OFACWatchList();
    private final IWatchList czechSanctionList = new CzechSanctionList();
    private final IWatchList euSanctionList = new EUSanctionsList();
    private final IWatchList caWatchList = new CaWatchList();

    @Override
    public String getName() {
        return "BasicWatchlistsExtension";
    }

    @Override
    public Set<String> getSupportedWatchListsNames() {
        final HashSet<String> watchListNames = new HashSet<>();
        watchListNames.add(ofacWatchList.getName());
        watchListNames.add(czechSanctionList.getName());
        watchListNames.add(euSanctionList.getName());
        watchListNames.add(caWatchList.getName());
        return watchListNames;
    }

    @Override
    public IWatchList getWatchList(String name) {
        if (ofacWatchList.getName().equals(name)) {
            return ofacWatchList;
        }
        if (czechSanctionList.getName().equals(name)) {
            return czechSanctionList;
        }
        if (euSanctionList.getName().equals(name)) {
            return euSanctionList;
        }
        if (caWatchList.getName().equals(name)) {
            return caWatchList;
        }
        return null;
    }
}
