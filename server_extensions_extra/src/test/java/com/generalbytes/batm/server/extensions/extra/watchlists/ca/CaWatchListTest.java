package com.generalbytes.batm.server.extensions.extra.watchlists.ca;

import com.generalbytes.batm.server.extensions.watchlist.WatchListMatch;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;
import org.junit.Test;

import static org.junit.Assert.*;

public class CaWatchListTest {

    private static final String RESOURCE_PATH = "src/test/resources/";

    @Test
    public void testWatchlistPositive() {
        final CaWatchList caWatchList = new CaWatchList();

        caWatchList.init(RESOURCE_PATH);
        caWatchList.parseSanctionsList();

        String identityPublicId = null;
        final WatchListResult emptyResult = caWatchList.search(new WatchListQuery("Unknown", "NotProvided", identityPublicId));
        assertTrue(emptyResult.getMatches().isEmpty());

        final WatchListResult result = caWatchList.search(new WatchListQuery("Samuel Creighton", "Mumbengegwi", identityPublicId));
        assertEquals(WatchListResult.RESULT_TYPE_WATCHLIST_SEARCHED, result.getResultType());
        assertEquals(1, result.getMatches().size());

        final WatchListMatch match = result.getMatches().iterator().next();
        assertEquals(caWatchList.getName(), match.getWatchlistName());
        assertEquals(caWatchList.getId(), match.getWatchlistCode());
        assertEquals(100, match.getScore());
        assertEquals("Zimbabwe/26", match.getPartyId());
        assertTrue(match.getDetails().contains("Number: Zimbabwe/26"));
        assertTrue(match.getDetails().contains("partyIndex: Zimbabwe/26"));

        final WatchListResult scheduleNumberPartyIdResult = caWatchList.search(new WatchListQuery("Jumah", "Al-Ahmad", identityPublicId));
        final WatchListMatch scheduleNumberPartyIdMatch = scheduleNumberPartyIdResult.getMatches().iterator().next();
        assertEquals("Syria/2-60", scheduleNumberPartyIdMatch.getPartyId());
    }
}