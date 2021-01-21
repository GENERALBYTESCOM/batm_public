package com.generalbytes.batm.server.extensions.extra.watchlists;

/**
 * Result wrapper for entries found in some watchlist.
 */
public class Match {
    private final String partyId;
    private final int score;

    public Match(String partyId, int score) {
        this.partyId = partyId;
        this.score = score;
    }

    public String getPartyId() {
        return partyId;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Match match = (Match) o;

        return partyId.equals(match.partyId);

    }

    @Override
    public int hashCode() {
        return partyId.hashCode();
    }
}
