package com.generalbytes.batm.server.extensions.extra.watchlists.ca;

import com.generalbytes.batm.server.extensions.extra.watchlists.Match;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags.DataSet;
import com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags.Record;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

public class ParsedSanctionsTest {

    @Test
    public void testSearchOk() {
        final Record record = createRecord("FirstName MiddleName ", " LastName ");
        final ParsedSanctions parsedSanctions = initializeParsedSanctions(record);
        assertMatch(parsedSanctions.search("fIrStNaMe", "lastNAME"), 100);
    }

    @Test
    public void testSearchLastNameMatch() {
        final Record record = createRecord("Unknown", " LastName ");
        final ParsedSanctions parsedSanctions = initializeParsedSanctions(record);
        assertMatch(parsedSanctions.search("fIrStNaMe", "lastNAME"), 50);
    }

    @Test
    public void testSearchLastNameMatchNoFirstName() {
        final Record record = createRecord("Unknown", " LastName ");
        final ParsedSanctions parsedSanctions = initializeParsedSanctions(record);
        assertMatch(parsedSanctions.search(null, "lastNAME"), 100);
    }

    @Test
    public void testSearchAliasMatch() {
        final Record record = createRecord("Unknown", "Stranger");
        record.setAliases("Someone with lastname");
        final ParsedSanctions parsedSanctions = initializeParsedSanctions(record);
        assertMatch(parsedSanctions.search("fIrStNaMe", "lastNAME"), 50);
    }

    @Test
    public void testSearchAliasMatchNoFirstName() {
        final Record record = createRecord("Unknown", "Stranger");
        record.setAliases("Someone with lastname");
        final ParsedSanctions parsedSanctions = initializeParsedSanctions(record);
        assertMatch(parsedSanctions.search(null, "lastNAME"), 50);
    }

    @Test
    public void testSearchNotFound() {
        final Record record = createRecord("Unknown", " LastName ");

        final ParsedSanctions parsedSanctions = initializeParsedSanctions(record);
        final Set<Match> result = parsedSanctions.search("totally", "different");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchAliasNoMatch() {
        final Record record = createRecord("Vladimir Rudolfovich", "SOLOVYOV");
        record.setAliases("Владимир Рудольфович Соловьёв; Vladimir Solovev; Soloviev");
        final ParsedSanctions parsedSanctions = initializeParsedSanctions(record);
        Set<Match> result = parsedSanctions.search("Unknown", "Vlad");
        assertTrue(result.isEmpty());
    }

    private void assertMatch(Set<Match> result, int i) {
        assertEquals(1, result.size());
        final Match foundMatch = result.iterator().next();
        assertEquals("TestCountry/13", foundMatch.getPartyId());
        assertEquals(i, foundMatch.getScore());
    }

    private ParsedSanctions initializeParsedSanctions(Record record) {
        final DataSet dataSet = new DataSet();
        dataSet.setRecords(Collections.singletonList(record));
        return ParsedSanctions.parse(dataSet);
    }

    private Record createRecord(String givenName, String lastName) {
        final Record record = new Record();
        record.setCountry("TestCountry");
        record.setItem("13");
        record.setLastName(lastName);
        record.setGivenName(givenName);
        return record;
    }
}