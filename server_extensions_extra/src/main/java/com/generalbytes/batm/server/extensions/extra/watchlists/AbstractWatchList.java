package com.generalbytes.batm.server.extensions.extra.watchlists;

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

/**
 * Abstract class to provide common methods used when implementing watch lists.
 * @param <T> Sanctions wrapper defined by {@link IParsedSanctions}
 */
public abstract class AbstractWatchList<T extends IParsedSanctions> implements IWatchList {

    private static final Logger log = LoggerFactory.getLogger(AbstractWatchList.class);

    protected T sanctions;
    protected String downloadDirectory;

    /**
     * Search watchlist for any entries accorting to provided query.
     *
     * @param query        {@link WatchListQuery}
     * @param sanctionDesc Short sanction source description used in log entry
     * @param detailUrl    URL containing more information about watchlist containing matched result
     * @return {@link WatchListResult}
     */
    protected WatchListResult searchWatchList(WatchListQuery query, final String sanctionDesc, final String detailUrl) {
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
        } else {
            final ArrayList<WatchListMatch> matches = new ArrayList<>();
            for (Match match : result) {
                final String partyIndex = sanctions.getPartyIndexByPartyId(match.getPartyId());
                matches.add(new WatchListMatch(match.getScore(), "Matched " + sanctionDesc + " Number: " + match.getPartyId() + " partyIndex: " + partyIndex + ". For more details click <a href='" + detailUrl + "'>here</a>.", getId(), getName(), match.getPartyId()));
            }
            return new WatchListResult(matches);
        }
    }

    /**
     * Performs the re-download of the watchlist from the remote side.
     *
     * @param fileUrl  URL leading to xml sanctions file
     * @param filename sanctions filename
     * @return List refresh state @see LIST_NOT_CHANGED or LIST_CHANGED or LIST_REFRESH_FAILED in {@link IWatchList}
     */
    protected int refreshWatchListFile(String fileUrl, String filename) {
        File watchlistsDir = new File(downloadDirectory);
        if (!watchlistsDir.exists()) {
            watchlistsDir.mkdirs();
        }
        log.debug("Downloading {} watchlist...", getName());
        final File finalFile = new File(watchlistsDir, filename);
        final File downloadToFile = new File(watchlistsDir, filename + ".download");
        final boolean res = downloadFile(fileUrl, downloadToFile);
        if (res) {
            boolean changed = true;
            if (downloadToFile.exists() && finalFile.exists()) {
                changed = downloadToFile.length() != finalFile.length();
            }
            if (changed) {
                if (checkParsing(downloadToFile) && switchFiles(finalFile, downloadToFile)) {
                    sanctions = parseSanctionsList();
                    return IWatchList.LIST_CHANGED;
                } else {
                    return IWatchList.LIST_REFRESH_FAILED;
                }
            } else {
                downloadToFile.delete();
                return IWatchList.LIST_NOT_CHANGED;
            }
        }
        return IWatchList.LIST_REFRESH_FAILED;
    }

    /**
     * Validation method. Used to check xml file can be successfully unmarshalled to java object.
     *
     * @param xmlFile File to be checked
     * @return {@code true} if success {@code false} otherwise
     */
    protected abstract boolean checkParsing(File xmlFile);

    /**
     * Parse downloaded xml file to java object and initialize ParsedSanctions implementing {@link IParsedSanctions} interface.
     *
     * @return ParsedSanctions
     */
    protected abstract T parseSanctionsList();

    private boolean downloadFile(String fileURL, File downloadToFile) {
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

    private synchronized boolean switchFiles(File finalFile, File downloadToFile) {
        return downloadToFile.renameTo(finalFile);
    }

}
