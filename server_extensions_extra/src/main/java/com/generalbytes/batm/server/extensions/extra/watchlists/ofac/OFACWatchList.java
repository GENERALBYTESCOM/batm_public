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

package com.generalbytes.batm.server.extensions.extra.watchlists.ofac;

import com.generalbytes.batm.server.extensions.extra.watchlists.AbstractWatchList;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.Sanctions;
import com.generalbytes.batm.server.extensions.watchlist.BlacklistedAddress;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class OFACWatchList extends AbstractWatchList<ParsedSanctions> {
    private static final Logger log = LoggerFactory.getLogger("batm.master.watchlist.OFAC");
    private static final String DOWNLOAD_URL = "https://www.treasury.gov/ofac/downloads/sanctions/1.0/sdn_advanced.xml";
    private static final String SANCTIONS_FILENAME = "ofac_sdn_advanced.xml";
    private static final int UPDATE_PERIOD_IN_MINS = 60 * 24; //every 24 hours in mins
    private static final String WATCHLIST_ID = "ofac";

    // more information here: https://www.treasury.gov/resource-center/sanctions/SDN-List/Pages/default.aspx
    // and here: https://www.treasury.gov/resource-center/sanctions/SDN-List/Pages/sdn_advanced.aspx

    @Override
    public String getName() {
        return "OFAC - Specially Designated Nationals List";
    }

    @Override
    public String getId() {
        return WATCHLIST_ID;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getDescription() {
        return "Downloaded every 24 hours from " + DOWNLOAD_URL;
    }

    @Override
    public void init(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public int recommendedRefreshPeriodInMins() {
        return UPDATE_PERIOD_IN_MINS;
    }

    @Override
    public int refresh() {
        return refreshWatchListFile(DOWNLOAD_URL, SANCTIONS_FILENAME);
    }

    @Override
    protected boolean checkParsing(File xmlFile) {
        log.debug("Parsing " + xmlFile.getAbsolutePath() + "...");
        try {
            JAXBContext jc = JAXBContext.newInstance(Sanctions.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            Sanctions sanctions = (Sanctions) unmarshaller.unmarshal(xmlFile);
            if (sanctions != null) {
                return true;
            }
        } catch (JAXBException e) {
            log.error("Error", e);
        }

        return false;
    }

    @Override
    public WatchListResult search(WatchListQuery query) {
        return searchWatchList(query, "OFAC SDN", "https://sanctionssearch.ofac.treas.gov");
    }

    @Override
    protected ParsedSanctions parseSanctionsList() {
        File watchlistsDir = new File(downloadDirectory);
        final File finalFile = new File(watchlistsDir, SANCTIONS_FILENAME);
        if (finalFile.exists()) {
            try {
                JAXBContext jc = JAXBContext.newInstance(Sanctions.class);

                Unmarshaller unmarshaller = jc.createUnmarshaller();
                Sanctions temporary = (Sanctions) unmarshaller.unmarshal(finalFile);
                return ParsedSanctions.parse(temporary);

            } catch (JAXBException e) {
                log.error("Error", e);
            }
        }
        return null;
    }

    @Override
    public Set<BlacklistedAddress> getBlacklistedCryptoAddresses() {
        synchronized (this) {
            if (sanctions == null) {
                sanctions = parseSanctionsList();
            }
        }

        if (sanctions == null) {
            return new HashSet<>();
        }

        Set<BlacklistedAddress> result = new HashSet<>();
        Set<String> blackSet = sanctions.getBlacklistedCryptoAddresses();
        for (String address : blackSet) {
            result.add(new BlacklistedAddress(WATCHLIST_ID, address));
        }
        return result;
    }
}
