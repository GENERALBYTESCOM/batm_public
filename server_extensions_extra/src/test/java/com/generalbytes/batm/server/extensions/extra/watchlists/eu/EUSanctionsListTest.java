package com.generalbytes.batm.server.extensions.extra.watchlists.eu;

import com.generalbytes.batm.server.extensions.watchlist.WatchListMatch;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class EUSanctionsListTest {

    private static final String RESOURCE_PATH = "src/test/resources/";

    @Test
    public void testWatchlistPositive() {
        final EUSanctionsList euSanctionsList = new EUSanctionsList();

        euSanctionsList.init(RESOURCE_PATH);
        euSanctionsList.parseSanctionsList();

        final WatchListResult emptyResult = euSanctionsList.search(new WatchListQuery("Unknown", "NotProvided"));
        assertTrue(emptyResult.getMatches().isEmpty());

        final WatchListResult result = euSanctionsList.search(new WatchListQuery("Mohammed Hamza", "Zoubaïdi"));
        assertEquals(WatchListResult.RESULT_TYPE_WATCHLIST_SEARCHED, result.getResultType());
        assertEquals(1, result.getMatches().size());

        final WatchListMatch match = result.getMatches().iterator().next();
        assertEquals(euSanctionsList.getName(), match.getMatchedWatchListName());
        assertEquals(100, match.getScore());
        assertTrue(match.getDetails().contains("Number: 391"));
        assertTrue(match.getDetails().contains("partyIndex: 391"));
    }

}