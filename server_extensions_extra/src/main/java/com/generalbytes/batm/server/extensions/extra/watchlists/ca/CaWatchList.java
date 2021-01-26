package com.generalbytes.batm.server.extensions.extra.watchlists.ca;

import com.generalbytes.batm.server.extensions.extra.watchlists.AbstractWatchList;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags.DataSet;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class CaWatchList extends AbstractWatchList<ParsedSanctions> {

    private static final Logger log = LoggerFactory.getLogger(CaWatchList.class);
    private static final String DOWNLOAD_URL = "https://www.international.gc.ca/world-monde/assets/office_docs/international_relations-relations_internationales/sanctions/sema-lmes.xml";
    private static final String SANCTIONS_FILENAME = "ca-sanctions.xml";
    private static final int UPDATE_PERIOD_IN_MINS = 60 * 24; //every 24 hours in mins

    // more information here: https://www.international.gc.ca/world-monde/international_relations-relations_internationales/sanctions/consolidated-consolide.aspx
    // and here: http://www.osfi-bsif.gc.ca/Eng/fi-if/amlc-clrpc/atf-fat/Pages/default.aspx

    @Override
    public void init(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public String getName() {
        return "Canadian Autonomous Sanctions List";
    }

    @Override
    public String getId() {
        return "ca";
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
    public int refresh() {
        return refreshWatchListFile(DOWNLOAD_URL, SANCTIONS_FILENAME);
    }

    @Override
    public int recommendedRefreshPeriodInMins() {
        return UPDATE_PERIOD_IN_MINS;
    }

    @Override
    public WatchListResult search(WatchListQuery query) {
        return searchWatchList(query, "Canadian Sanctions", "https://www.international.gc.ca/world-monde/international_relations-relations_internationales/sanctions/consolidated-consolide.aspx");
    }

    @Override
    protected boolean checkParsing(File xmlFile) {
        log.debug("Parsing " + xmlFile.getAbsolutePath() + "...");
        try {
            JAXBContext jc = JAXBContext.newInstance(DataSet.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            DataSet sanctions = (DataSet) unmarshaller.unmarshal(xmlFile);
            if (sanctions != null) {
                return true;
            }
        } catch (JAXBException e) {
            log.error("Error", e);
        }

        return false;
    }

    @Override
    protected ParsedSanctions parseSanctionsList() {
        final String watchlistsLocation = downloadDirectory;
        File watchlistsDir = new File(watchlistsLocation);
        final File finalFile = new File(watchlistsDir, SANCTIONS_FILENAME);
        if (finalFile.exists()) {
            try {
                JAXBContext jc = JAXBContext.newInstance(DataSet.class);

                Unmarshaller unmarshaller = jc.createUnmarshaller();
                DataSet dataSet = (DataSet) unmarshaller.unmarshal(finalFile);
                return ParsedSanctions.parse(dataSet);

            } catch (JAXBException e) {
                log.error("Error", e);
            }
        }
        return null;
    }
}
