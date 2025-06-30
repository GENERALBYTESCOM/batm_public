package com.generalbytes.batm.server.extensions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

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

    @Test
    void testGetByIso2() {
        Country country = Country.getByIso2("CA");
        assertEquals(Country.CA, country);
    }

    @Test
    void testGetByIso3() {
        Country country = Country.getByIso3("CAN");
        assertEquals(Country.CA, country);
    }

    @ParameterizedTest
    @EnumSource(value = Country.class, names = {"AU", "CA", "HK", "IT", "NZ", "US"})
    void testCountriesWithRegions(Country country) {
        assertTrue(country.hasRegions());
    }

    @ParameterizedTest
    @EnumSource(value = Country.class, names = {"AU", "CA", "HK", "IT", "NZ", "US"}, mode = EnumSource.Mode.EXCLUDE)
    void testCountriesWithoutRegions(Country country) {
        assertFalse(country.hasRegions());
    }

    static Object[] countryRegionsSource() {
        return new Object[]{
                new Object[]{Country.AU, CountryAustralia.values()},
        };
    }

    @ParameterizedTest
    @MethodSource("countryRegionsSource")
    void testRegions(Country country, CountryRegion[] regions) {
        assertArrayEquals(regions, country.getRegions());
    }
}