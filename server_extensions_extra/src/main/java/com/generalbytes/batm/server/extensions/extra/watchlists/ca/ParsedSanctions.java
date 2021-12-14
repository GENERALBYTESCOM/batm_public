package com.generalbytes.batm.server.extensions.extra.watchlists.ca;

import com.generalbytes.batm.server.extensions.extra.watchlists.IParsedSanctions;
import com.generalbytes.batm.server.extensions.extra.watchlists.Match;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags.DataSet;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags.Record;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParsedSanctions implements IParsedSanctions {

    private final List<Record> records;

    private ParsedSanctions(List<Record> records) {
        this.records = records;
    }

    public static ParsedSanctions parse(DataSet dataSet) {
        List<Record> records = dataSet.getRecords().stream()
            .filter(recordElement -> recordElement.getItem() != null)
            .filter(recordElement -> recordElement.getCountry() != null)
            .map(original -> {
                Record modified = new Record();
                modified.setCountry(fixCountryName(original.getCountry()));
                modified.setGivenName(makeTrimmedNonNullString(original.getGivenName()));
                modified.setLastName(makeTrimmedNonNullString(original.getLastName()));
                modified.setItem(original.getItem());
                modified.setSchedule(original.getSchedule());
                modified.setAliases(original.getAliases());

                return modified;
            })
            .collect(Collectors.toList());

        return new ParsedSanctions(records);
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
                    if (lastName.equalsIgnoreCase(item.getLastName())) {
                        matchedParties.add(new Match(getPartyId(item), 100));
                        continue;
                    }
                    if (containsSubstring(lastName, item.getAliases())) {
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
                if (lastName.equalsIgnoreCase(item.getLastName())) {
                    if (firstName.equalsIgnoreCase(item.getGivenName()) || containsSubstring(firstName, item.getGivenName())) {
                        matchedParties.add(new Match(getPartyId(item), 100));
                    } else {
                        candidateRecords.add(item);
                    }
                    continue;
                }
                if (containsSubstring(lastName, item.getAliases())) {
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
        return ParsedSanctions.makeTrimmedNonNullString(input);
    }

    private static String makeTrimmedNonNullString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(CHAR_NON_BREAKING_SPACE, CHAR_SPACE).trim();
    }

    private String getPartyId(Record item) {
        String partyId = item.getCountry() + "/";
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

    private static String fixCountryName(String countryRaw) {
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
}
