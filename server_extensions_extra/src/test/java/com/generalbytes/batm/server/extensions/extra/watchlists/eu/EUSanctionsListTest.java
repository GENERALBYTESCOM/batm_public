package com.generalbytes.batm.server.extensions.extra.watchlists.eu;

import com.generalbytes.batm.server.extensions.watchlist.WatchListMatch;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EUSanctionsListTest {

    private static final String RESOURCE_PATH = "src/test/resources/";

    @Test
    void testWatchlistPositive() {
        final EUSanctionsList euSanctionsList = new EUSanctionsList();

        euSanctionsList.init(RESOURCE_PATH);
        euSanctionsList.parseSanctionsList();

        String identityPublicId = null;
        final WatchListResult emptyResult = euSanctionsList.search(new WatchListQuery("Unknown", "NotProvided", identityPublicId));
        assertTrue(emptyResult.getMatches().isEmpty());

        final WatchListResult result = euSanctionsList.search(new WatchListQuery("Mohammed Hamza", "Zoubaïdi", identityPublicId));
        assertEquals(WatchListResult.RESULT_TYPE_WATCHLIST_SEARCHED, result.getResultType());
        assertEquals(1, result.getMatches().size());

        final WatchListMatch match = result.getMatches().iterator().next();
        assertEquals(euSanctionsList.getName(), match.getWatchlistName());
        assertEquals(100, match.getScore());
        assertTrue(match.getDetails().contains("Number: 391"));
        assertTrue(match.getDetails().contains("partyIndex: 391"));
    }

}