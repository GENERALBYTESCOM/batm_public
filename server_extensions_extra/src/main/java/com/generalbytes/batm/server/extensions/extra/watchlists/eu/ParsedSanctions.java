/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.watchlists.eu;

import com.generalbytes.batm.server.extensions.extra.watchlists.IParsedSanctions;
import com.generalbytes.batm.server.extensions.extra.watchlists.Match;
import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.ExportType;
import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.NameAliasType;
import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.SanctionEntityType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParsedSanctions implements IParsedSanctions {
    private final List<NameAliasType> aliases;

    private ParsedSanctions(List<NameAliasType> aliases) {
        this.aliases = aliases;
    }

    public static ParsedSanctions parse(ExportType export) {
        List<NameAliasType> result = new ArrayList<>();
        List<SanctionEntityType> entities = export.getSanctionEntity();
        for (SanctionEntityType entity : entities) {
            result.addAll(entity.getNameAlias());
        }
        return new ParsedSanctions(result);
    }

    @Override
    public Set<Match> search(String firstName, String lastName) {
        firstName = getTrimmedNonNullString(firstName);
        lastName = getTrimmedNonNullString(lastName);

        if (firstName.isEmpty()) {
            return searchAgainstLastNameOnly(lastName);
        } else {
            return searchAgainstFullName(firstName, lastName);
        }
    }

    private Set<Match> searchAgainstFullName(String firstName, String lastName) {
        Set<String> candidateParties = new HashSet<>();
        Set<Match> matchedParties = new HashSet<>();
        for (NameAliasType alias : aliases) {
            String trimmedAliasWholeName = alias.getWholeName().trim();
            if (!trimmedAliasWholeName.isEmpty()) {
                int score = checkAgainstWholeName(firstName, lastName, trimmedAliasWholeName);
                if (score > 0) {
                    matchedParties.add(new Match(alias.getLogicalId() + "", score));
                    continue;
                }
            }
            checkAgainstFirstAndLastName(firstName, lastName, candidateParties, matchedParties, alias);
        }

        return finalizeMatches(candidateParties, matchedParties);
    }

    private int checkAgainstWholeName(String firstName, String lastName, String trimmedAliasWholeName) {
        int score = 0;
        if (trimmedAliasWholeName.equalsIgnoreCase(firstName + " " + lastName)) {
            score = 100;
        } else if (trimmedAliasWholeName.equalsIgnoreCase(lastName)) {
            score = 50;
        }
        return score;
    }

    private void checkAgainstFirstAndLastName(String firstName, String lastName, Set<String> candidateParties, Set<Match> matchedParties, NameAliasType alias) {
        String trimmedAliasLastName = alias.getLastName().trim();
        String trimmedAliasFirstName = alias.getFirstName().trim();
        if (
            !(trimmedAliasFirstName.isEmpty() && trimmedAliasLastName.isEmpty()) // if neither are empty
            && trimmedAliasLastName.equalsIgnoreCase(lastName)
        ) {
            if (trimmedAliasFirstName.equalsIgnoreCase(firstName)) {
                //ok seems like we have a winner
                matchedParties.add(new Match(alias.getLogicalId() + "", 100));
            } else {
                candidateParties.add(alias.getLogicalId() + "");
            }
        }
    }

    private Set<Match> finalizeMatches(Set<String> candidateParties, Set<Match> matchedParties) {
        if (matchedParties.isEmpty()) {
            //both first name and last name didn't match
            //so lets report at least lastname matches with 50% score/confidence
            for (String candidateParty : candidateParties) {
                matchedParties.add(new Match(candidateParty, 50));
            }
        }
        return matchedParties;
    }

    private Set<Match> searchAgainstLastNameOnly(String lastName) {
        Set<Match> matchedParties = new HashSet<>();
        if (lastName.isEmpty()) {
            return matchedParties;
        }
        //search just against last names
        for (NameAliasType alias : aliases) {
            if (
                alias.getLastName().trim().equalsIgnoreCase(lastName)
                || alias.getWholeName().trim().equalsIgnoreCase(lastName)
            ) {
                matchedParties.add(new Match(alias.getLogicalId() + "", 100));
            }
        }
        return matchedParties;
    }

    @Override
    public String getPartyIndexByPartyId(String partyId) {
        return partyId;
    }
}
