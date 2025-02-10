package com.generalbytes.batm.common.currencies;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FiatCurrencyTest {

    @Test
    void getCurrencyName() {
        assertEquals("Czech koruna", FiatCurrency.CZK.getCurrencyName());
        for (FiatCurrency c : FiatCurrency.values()) {
            assertTrue(c.getCurrencyName() != null && !c.getCurrencyName().isEmpty(), c.name() + " name");
        }
    }

    @Test
    void getCode() {
        assertEquals("CZK", FiatCurrency.CZK.getCode());
        for (FiatCurrency c : FiatCurrency.values()) {
            assertTrue(c.getCurrencyName().length() > 1, c.name() + " code");
            assertEquals(c.getCode(), c.getCode().toUpperCase(), c.name() + " code should be uppercase");
            assertEquals(c.name(), c.name().toUpperCase(), c.name() + " enum should be uppercase");
        }
    }

    @Test
    void getCodes() {
        assertEquals(FiatCurrency.values().length, FiatCurrency.getCodes().size());
        assertTrue(FiatCurrency.getCodes().containsAll(Arrays.asList("CZK", "EUR", "USD")));
    }
}