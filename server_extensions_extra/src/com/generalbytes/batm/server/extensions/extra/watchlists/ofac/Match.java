package com.generalbytes.batm.server.extensions.extra.watchlists.ofac;

/**
 * Created by b00lean on 18.2.16.
 */
public class Match {
    private String partyId;
    private int score;

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
