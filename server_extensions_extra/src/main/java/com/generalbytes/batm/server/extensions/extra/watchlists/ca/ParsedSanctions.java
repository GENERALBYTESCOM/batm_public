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

        Set<Match> matchedParties = new HashSet<>();

        if (firstName.isEmpty()) {
            if (!lastName.isEmpty()) {
                //search just against last names
                for (Record item : records) {
                    if (lastName.equalsIgnoreCase(getTrimmedNonNullString(item.getLastName()))) {
                        matchedParties.add(new Match(getPartyId(item), 100));
                        continue;
                    }
                    if (aliasContainsLastName(lastName, item.getAliases())) {
                        matchedParties.add(new Match(getPartyId(item), 50));
                    }
                }
            } else {
                //nothing to match
                return matchedParties;
            }
        } else {
            //search against lastname and firstname
            Set<Record> candidateRecords = new HashSet<>();
            for (Record item : records) {
                if (lastName.equalsIgnoreCase(getTrimmedNonNullString(item.getLastName()))) {
                    if (firstName.equalsIgnoreCase(item.getGivenName()) || containsSubstring(firstName, item.getGivenName())) {
                        matchedParties.add(new Match(getPartyId(item), 100));
                    } else {
                        candidateRecords.add(item);
                    }
                    continue;
                }
                if (aliasContainsLastName(lastName, item.getAliases())) {
                    candidateRecords.add(item);
                }
            }

            if (matchedParties.isEmpty()) {
                //neither first name nor last name matched
                //so lets report at least lastname matches with 50% score/confidence
                for (Record item : candidateRecords) {
                    matchedParties.add(new Match(getPartyId(item), 50));
                }
            }
        }
        return matchedParties;
    }

    @Override
    public String getPartyIndexByPartyId(String partyId) {
        return partyId;
    }

    @Override
    public String getTrimmedNonNullString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(CHAR_NON_BREAKING_SPACE, CHAR_SPACE).trim();
    }

    private String getPartyId(Record item) {
        String country = fixCountryName(item.getCountry());
        String partyId = country + "/";
        String schedule = item.getSchedule();
        if (schedule != null && !schedule.isEmpty()) {
            schedule = schedule.replaceAll("[^0-9]+", ".").replaceAll("\\.+$|^\\.+", "");
            if (!schedule.isEmpty()) {
                partyId += schedule + "-";
            }
        }
        partyId += item.getItem();
        return partyId;
    }

    private String fixCountryName(String countryRaw) {
        String country = countryRaw.split("/")[0].trim();
        int longestCountryNameLength = 57;
        if (country.length() > longestCountryNameLength) {
            country = country.replaceFirst("^[^(]*\\((.*)\\).*$", "$1");
        }
        return country;
    }

    private boolean containsSubstring(String substring, String input) {
        return input != null && input.toLowerCase().contains(substring.toLowerCase());
    }

    private boolean aliasContainsLastName(String lastName, String aliases) {
        if (aliases == null) {
            return false;
        }
        String[] aliasesArray = aliases.split(";");
        for (String alias : aliasesArray) {
            String[] aliasWords = alias.split(" ");
            String aliasLastName = aliasWords[aliasWords.length - 1];
            if (aliasLastName.equalsIgnoreCase(lastName)) {
                return true;
            }
        }
        return false;
    }
}
