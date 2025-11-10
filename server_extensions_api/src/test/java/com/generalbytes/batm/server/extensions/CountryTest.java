package com.generalbytes.batm.server.extensions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    @Test
    void testIdentificationGetters() {
        Country country = Country.US;

        assertEquals("US", country.getIso2());
        assertEquals("USA", country.getIso3());
        assertEquals("United States of America", country.getCountryName());
    }

    @Test
    void testCountryValue() {
        assertEquals(Country.CZ, Country.value("CZ"));
    }
}