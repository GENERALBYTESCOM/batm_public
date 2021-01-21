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
package com.generalbytes.batm.server.extensions.extra.watchlists.eu;

import com.generalbytes.batm.server.extensions.extra.watchlists.AbstractWatchList;
import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.ExportType;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class EUSanctionsList extends AbstractWatchList<ParsedSanctions> {
    private static final Logger log = LoggerFactory.getLogger("batm.master.watchlist.EUSanctionsList");

    private static final String DOWNLOAD_URL = "https://webgate.ec.europa.eu/europeaid/fsd/fsf/public/files/xmlFullSanctionsList_1_1/content";
    private static final String TOKEN = "n002frsg";
    private static final String SANCTIONS_FILENAME = "eu_sanctions.xml";
    private static final int UPDATE_PERIOD_IN_MINS = 60 * 24; //every 24 hours in mins

    // More information here: https://webgate.ec.europa.eu/europeaid/fsd/fsf
    // after login clink on: Show settings for crawler/robot

    @Override
    public String getName() {
        return "EU - Financial Sanctions";
    }

    @Override
    public String getId() {
        return "eu";
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
        return refreshWatchListFile(DOWNLOAD_URL + "?token=" + TOKEN, SANCTIONS_FILENAME);
    }

    @Override
    protected boolean checkParsing(File xmlFile) {
        log.debug("Parsing " + xmlFile.getAbsolutePath() + "...");
        try {
            JAXBContext jc = JAXBContext.newInstance(ExportType.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            ExportType sanctions = (ExportType) unmarshaller.unmarshal(xmlFile);
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
        return searchWatchList(query, "EU Sanctions", "https://www.sanctionsmap.eu");
    }

    @Override
    protected ParsedSanctions parseSanctionsList() {
        File watchlistsDir = new File(downloadDirectory);
        final File finalFile = new File(watchlistsDir, SANCTIONS_FILENAME);
        if (finalFile.exists()) {
            try {
                JAXBContext jc = JAXBContext.newInstance(ExportType.class);

                Unmarshaller unmarshaller = jc.createUnmarshaller();
                ExportType temporary = (ExportType) unmarshaller.unmarshal(finalFile);
                return ParsedSanctions.parse(temporary);

            } catch (JAXBException e) {
                log.error("Error", e);
            }
        }
        return null;
    }

}
