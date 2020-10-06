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

import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.ExportType;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;
import com.generalbytes.batm.server.extensions.watchlist.WatchListMatch;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class EUSanctionsList implements IWatchList {
    private static final Logger log = LoggerFactory.getLogger("batm.master.watchlist.EUSanctionsList");

    private static final String DOWNLOAD_URL = "https://webgate.ec.europa.eu/europeaid/fsd/fsf/public/files/xmlFullSanctionsList_1_1/content";
    private static final String TOKEN = "n002frsg";
    private String downloadDirectory;

    // More information here: https://webgate.ec.europa.eu/europeaid/fsd/fsf
    // after login clink on: Show settings for crawler/robot


    private ParsedSanctions sanctions;

    private static final int UPDATE_PERIOD_IN_MINS = 60 * 24; //every 24 hours in mins

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
        File watchlistsDir = new File(downloadDirectory);
        if (!watchlistsDir.exists()) {
            watchlistsDir.mkdirs();
        }
        log.debug("Downloading EU Financial Sanctions watch list...");
        final File finalFile = new File(watchlistsDir, "eu_sanctions.xml");
        final File downloadToFile = new File(watchlistsDir, "eu_sanctions.xml.download");
        final boolean res = downloadFile(DOWNLOAD_URL + "?token=" +TOKEN, downloadToFile);
        if (res) {
            boolean changed = true;
            if (downloadToFile.exists() && finalFile.exists()) {
                changed = downloadToFile.length() != finalFile.length();
            }
            if (changed) {
                if (checkParsing(downloadToFile) && switchFiles(finalFile, downloadToFile)) {
                    sanctions = parseSanctionsList();
                    return LIST_CHANGED;
                } else {
                    return LIST_REFRESH_FAILED;
                }
            }else{
                downloadToFile.delete();
                return LIST_NOT_CHANGED;
            }
        }
        return LIST_REFRESH_FAILED;
    }

    private synchronized boolean switchFiles(File finalFile, File downloadToFile) {
        return downloadToFile.renameTo(finalFile);
    }

    private boolean checkParsing(File downloadToFile) {
        log.debug("Parsing " + downloadToFile.getAbsolutePath() + "...");
        try {
            JAXBContext jc = JAXBContext.newInstance(ExportType.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            ExportType sanctions = (ExportType) unmarshaller.unmarshal(downloadToFile);
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
        synchronized (this) {
            if (sanctions == null) {
                sanctions = parseSanctionsList();
            }
        }

        if (sanctions == null) {
            return new WatchListResult(WatchListResult.RESULT_TYPE_WATCHLIST_NOT_READY);
        }

        //do the actual matching
        final Set<Match> result = sanctions.search(query.getFirstName(), query.getLastName());


        if (result.isEmpty()) {
            return new WatchListResult(WatchListResult.RESULT_TYPE_WATCHLIST_SEARCHED);
        }else{
            final ArrayList<WatchListMatch> matches = new ArrayList<WatchListMatch>();
            for (Match match : result) {
                final String partyIndex = sanctions.getPartyIndexByPartyId(match.getPartyId());
                matches.add(new WatchListMatch(match.getScore(),"Matched EU Sanctions Number: " + match.getPartyId() + " partyIndex: "+ partyIndex + ". For more details click <a href=\'https://www.sanctionsmap.eu\'>here</a>.",getName()));
            }
            return new WatchListResult(matches);
        }
    }

    private ParsedSanctions parseSanctionsList() {
        final String watchlistsLocation = downloadDirectory;
        File watchlistsDir = new File(watchlistsLocation);
        final File finalFile = new File(watchlistsDir, "eu_sanctions.xml");
        if (finalFile.exists()) {
            try {
                JAXBContext jc = JAXBContext.newInstance(ExportType.class);

                Unmarshaller unmarshaller = jc.createUnmarshaller();
                ExportType temporary  = (ExportType) unmarshaller.unmarshal(finalFile);
                return ParsedSanctions.parse(temporary);

            } catch (JAXBException e) {
                log.error("Error", e);
            }
        }
        return null;
    }

    public static boolean downloadFile(String fileURL, File downloadToFile) {
        try {
            URL website = new URL(fileURL);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(downloadToFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            return true;
        } catch (IOException e) {
            log.error("Error", e);
        }
        return false;
    }
}
