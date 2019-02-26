package com.generalbytes.batm.common.currencies;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CryptoCurrencyTest {

    @Test
    public void getCurrencyName() {
        assertEquals("Bitcoin", CryptoCurrency.BTC.getCurrencyName());
        assertEquals("Litecoin", CryptoCurrency.LTC.getCurrencyName());
        assertEquals("Bitcoin Cash", CryptoCurrency.BCH.getCurrencyName());
        for (CryptoCurrency c : CryptoCurrency.values()) {
            assertTrue(c.name() + " name", c.getCurrencyName() != null && !c.getCurrencyName().isEmpty());
        }
    }

    @Test
    public void getCode() {
        assertEquals("BTC", CryptoCurrency.BTC.getCode());
        assertEquals("$PAC", CryptoCurrency.PAC.getCode());
        for (CryptoCurrency c : CryptoCurrency.values()) {
            assertTrue(c.name() + " code", c.getCode() != null && c.getCurrencyName().length() > 1);
            assertEquals(c.name() + " code should be uppercase", c.getCode(), c.getCode().toUpperCase());
            assertEquals(c.name() + " enum should be uppercase", c.name(), c.name().toUpperCase());
        }
    }

    @Test
    public void getCodes() {
        assertEquals(CryptoCurrency.values().length, CryptoCurrency.getCodes().size());
        assertTrue(CryptoCurrency.getCodes().containsAll(Arrays.asList("BTC", "$PAC", "LTC")));
    }

    @Test
    public void valueOfCode() {
        assertEquals(CryptoCurrency.BTC, CryptoCurrency.valueOfCode("btc"));
        assertEquals(CryptoCurrency.BTC, CryptoCurrency.valueOfCode("BTC"));
        assertEquals(CryptoCurrency.PAC, CryptoCurrency.valueOfCode("$pAC"));
        assertEquals(CryptoCurrency.PAC, CryptoCurrency.valueOfCode("$PAC"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfException() {
        CryptoCurrency.valueOf("$PAC");
    }
}
