package com.generalbytes.batm.server.extensions.extra.watchlists;

import java.util.Set;

/**
 * Common methods used to get relevant sanctions.
 */
public interface IParsedSanctions {

    char CHAR_NON_BREAKING_SPACE = 0x00a0;
    char CHAR_SPACE = 0x0020;

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

    default String getTrimmedNonNullString(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }
}
