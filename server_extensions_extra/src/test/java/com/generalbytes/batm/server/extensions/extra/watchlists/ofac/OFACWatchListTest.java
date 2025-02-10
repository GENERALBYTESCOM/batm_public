package com.generalbytes.batm.server.extensions.extra.watchlists.ofac;

import com.generalbytes.batm.server.extensions.watchlist.WatchListMatch;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OFACWatchListTest {

    private static final String RESOURCE_PATH = "src/test/resources/";

    @Test
    void testWatchlistPositive() {
        final OFACWatchList ofacWatchList = new OFACWatchList();

        ofacWatchList.init(RESOURCE_PATH);
        ofacWatchList.parseSanctionsList();

        String identityPublicId = null;
        final WatchListResult emptyResult = ofacWatchList.search(new WatchListQuery("Unknown", "NotProvided", identityPublicId));
        assertTrue(emptyResult.getMatches().isEmpty());

        final WatchListResult result = ofacWatchList.search(new WatchListQuery("Saddam", "Hussein", identityPublicId));
        assertEquals(WatchListResult.RESULT_TYPE_WATCHLIST_SEARCHED, result.getResultType());
        assertEquals(1, result.getMatches().size());

        final WatchListMatch match = result.getMatches().iterator().next();
        assertEquals(ofacWatchList.getName(), match.getWatchlistName());
        assertEquals(100, match.getScore());
        assertTrue(match.getDetails().contains("Number: 7843"));
        assertTrue(match.getDetails().contains("partyIndex: 0"));
    }

}