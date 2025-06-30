package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryRegionNameParserTest {

    @Test
    void testGetRegionCodeFromCountry() {
        String state = "New South Wales";
        String country = "AUS";

        assertEquals("NSW", CountryRegionNameParser.getRegionCodeFromCountry(state, country));
    }

    @Test
    void testGetRegionCodeFromCountry_unknownState() {
        String state = "Unknown State";
        String country = "AUS";

        assertEquals("Unknown State", CountryRegionNameParser.getRegionCodeFromCountry(state, country));
    }

    @Test
    void testGetRegionCodeFromCountry_invalidCountry() {
        String state = "New South Wales";
        String country = "invalidCountry";
        assertEquals("New South Wales", CountryRegionNameParser.getRegionCodeFromCountry(state, country));
    }

    @Test
    void testGetRegionCodeFromCountry_noRegions() {
        String state = "Bavaria";
        String country = "DEU";
        assertEquals("Bavaria", CountryRegionNameParser.getRegionCodeFromCountry(state, country));
    }
}