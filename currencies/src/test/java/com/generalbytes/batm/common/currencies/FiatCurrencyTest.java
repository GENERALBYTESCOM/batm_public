package com.generalbytes.batm.common.currencies;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class FiatCurrencyTest {

    @Test
    public void getCurrencyName() {
        assertEquals("Czech koruna", FiatCurrency.CZK.getCurrencyName());
        for (FiatCurrency c : FiatCurrency.values()) {
            assertTrue(c.name() + " name", c.getCurrencyName() != null && !c.getCurrencyName().isEmpty());
        }
    }

    @Test
    public void getCode() {
        assertEquals("CZK", FiatCurrency.CZK.getCode());
        for (FiatCurrency c : FiatCurrency.values()) {
            assertTrue(c.name() + " code", c.getCode() != null && c.getCurrencyName().length() > 1);
            assertTrue(c.name() + " code should be uppercase", c.getCode().equals(c.getCode().toUpperCase()));
            assertTrue(c.name() + " enum should be uppercase", c.name().equals(c.name().toUpperCase()));
        }
    }

    @Test
    public void getCodes() {
        assertEquals(FiatCurrency.values().length, FiatCurrency.getCodes().size());
        assertTrue(FiatCurrency.getCodes().containsAll(Arrays.asList("CZK", "EUR", "USD")));
    }
}