package com.generalbytes.batm.server.extensions.extra.watchlists.ca;

import com.generalbytes.batm.server.extensions.extra.watchlists.IParsedSanctions;
import com.generalbytes.batm.server.extensions.extra.watchlists.Match;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags.DataSet;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags.Record;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParsedSanctions implements IParsedSanctions {

    private final List<Record> records;

    private ParsedSanctions(List<Record> records) {
        this.records = records;
    }

    public static ParsedSanctions parse(DataSet dataSet) {
        return new ParsedSanctions(dataSet.getRecords());
    }

    @Override
    public Set<Match> search(String firstName, String lastName) {
        firstName = getTrimmedNonNullString(firstName);
        lastName = getTrimmedNonNullString(lastName);

        Set<String> candidateParties = new HashSet<>();
        Set<Match> matchedParties = new HashSet<>();

        if (firstName.isEmpty()) {
            if (!lastName.isEmpty()) {
                //search just against last names
                for (Record record : records) {
                    if (lastName.equalsIgnoreCase(getTrimmedNonNullString(record.getLastName()))) {
                        matchedParties.add(new Match(getPartyId(record), 100));
                    }
                    if (containsSubstring(lastName, record.getAliases())) {
                        matchedParties.add(new Match(getPartyId(record), 50));
                    }
                }
            } else {
                //nothing to match
                return matchedParties;
            }
        } else {
            //search against lastname and firstname
            for (Record record : records) {
                if (lastName.equalsIgnoreCase(getTrimmedNonNullString(record.getLastName()))) {
                    if (firstName.equalsIgnoreCase(record.getGivenName()) || containsSubstring(firstName, record.getGivenName())) {
                        matchedParties.add(new Match(getPartyId(record), 100));
                    } else {
                        candidateParties.add(getPartyId(record));
                    }
                }
                if (containsSubstring(lastName, record.getAliases())) {
                    candidateParties.add(getPartyId(record));
                }
            }

            if (matchedParties.isEmpty()) {
                //neither first name nor last name matched
                //so lets report at least lastname matches with 50% score/confidence
                for (String candidateParty : candidateParties) {
                    matchedParties.add(new Match(candidateParty, 50));
                }
            }
        }
        return matchedParties;
    }

    @Override
    public String getPartyIndexByPartyId(String partyId) {
        return partyId;
    }

    private String getTrimmedNonNullString(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("\u00a0", "").trim();
    }

    private String getPartyId(Record record) {
        return record.getCountry() + "/" + record.getItem();
    }

    private boolean containsSubstring(String substring, String input) {
        return input != null && input.toLowerCase().contains(substring.toLowerCase());
    }
}
