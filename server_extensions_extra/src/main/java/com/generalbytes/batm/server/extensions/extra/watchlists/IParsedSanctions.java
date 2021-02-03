package com.generalbytes.batm.server.extensions.extra.watchlists;

import java.util.Set;

/**
 * Common methods used to get relevant sanctions.
 */
public interface IParsedSanctions {

    /**
     * Search for match in provided watch list by subject's name.
     *
     * @param firstName Subject's first name
     * @param lastName  Subject's last name
     * @return Set of {@link Match}
     */
    Set<Match> search(String firstName, String lastName);

    /**
     * Get index of given entry if possible. Depends on actual implementation.
     *
     * @param partyId Value of {@link Match#getPartyId()}}
     * @return partyIndex according to implementation
     */
    String getPartyIndexByPartyId(String partyId);
}
