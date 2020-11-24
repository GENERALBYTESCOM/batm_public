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

import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.ExportType;
import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.NameAliasType;
import com.generalbytes.batm.server.extensions.extra.watchlists.eu.tags.SanctionEntityType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParsedSanctions {
    private List<NameAliasType> aliases;

    private ParsedSanctions(List<NameAliasType> aliases) {
        this.aliases = aliases;
    }

    public static ParsedSanctions parse(ExportType export) {
        List<NameAliasType> result = new ArrayList<>();
        List<SanctionEntityType> enities = export.getSanctionEntity();
        for (int i = 0; i < enities.size(); i++) {
            result.addAll(enities.get(i).getNameAlias());
        }
        return new ParsedSanctions(result);
    }

    public Set<Match> search(String firstName, String lastName) {
        if (firstName == null) {
            firstName = "";
        }
        if (lastName == null) {
            lastName = "";
        }

        lastName = lastName.trim();
        firstName = firstName.trim();

        Set<String> candidateParties = new HashSet<String>();
        Set<Match> matchedParties = new HashSet<>();

        if (firstName.isEmpty()) {
            if (!lastName.isEmpty()) {
                //search just against last names
                for (int j = 0; j < aliases.size(); j++) {
                    NameAliasType alias = aliases.get(j);
                    if (alias.getLastName().trim().equalsIgnoreCase(lastName)) {
                        matchedParties.add(new Match(alias.getLogicalId() + "", 100));
                    } else if (alias.getWholeName().trim().equalsIgnoreCase(lastName)) {
                        matchedParties.add(new Match(alias.getLogicalId() + "", 100));
                    }
                }
            }else {
                //only entity name
                return matchedParties;
            }
        }else {
            //search against lastname and firstname
            for (int j = 0; j < aliases.size(); j++) {
                NameAliasType alias = aliases.get(j);
                boolean addedMatch = false;
                if (!alias.getWholeName().trim().isEmpty()) {
                    if (alias.getWholeName().equalsIgnoreCase(lastName) || alias.getWholeName().equalsIgnoreCase(firstName + " " + lastName) ) {
                        matchedParties.add(new Match(alias.getLogicalId() + "", 100));
                        addedMatch = true;
                    }
                }
                if (!addedMatch) {
                    if (alias.getLastName().trim().equalsIgnoreCase(lastName)) {
                        if (alias.getFirstName().trim().equalsIgnoreCase(firstName)) {
                            //ok seems like we have a winner
                            matchedParties.add(new Match(alias.getLogicalId() + "", 100));
                        } else {
                            candidateParties.add(alias.getLogicalId() + "");
                        }
                    }
                }
            }

            if (matchedParties.size() == 0) {
                //both first name and last name didn't match
                //so lets report at least lastname matches with 50% score/confidence
                for (String candidateParty : candidateParties) {
                    matchedParties.add(new Match(candidateParty,50));
                }
            }
        }
        return matchedParties;
    }

    public String getPartyIndexByPartyId(String partyId) {
        return partyId;
    }

}
