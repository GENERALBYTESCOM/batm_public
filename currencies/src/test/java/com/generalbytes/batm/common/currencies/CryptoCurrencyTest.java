package com.generalbytes.batm.common.currencies;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CryptoCurrencyTest {

    @Test
    void getCurrencyName() {
        assertEquals("Bitcoin", CryptoCurrency.BTC.getCurrencyName());
        assertEquals("Litecoin", CryptoCurrency.LTC.getCurrencyName());
        assertEquals("Bitcoin Cash", CryptoCurrency.BCH.getCurrencyName());
        for (CryptoCurrency c : CryptoCurrency.values()) {
            assertTrue(c.getCurrencyName() != null && !c.getCurrencyName().isEmpty(), c.name() + " name");
        }
    }

    @Test
    void getCode() {
        assertEquals("BTC", CryptoCurrency.BTC.getCode());
        assertEquals("$PAC", CryptoCurrency.PAC.getCode());
        for (CryptoCurrency c : CryptoCurrency.values()) {
            assertTrue(c.getCode() != null && c.getCurrencyName().length() > 1, c.name() + " code");
            assertEquals(c.getCode(), c.getCode().toUpperCase(), c.name() + " code should be uppercase");
            assertEquals(c.name(), c.name().toUpperCase(), c.name() + " enum should be uppercase");
        }
    }

    @Test
    void getCodes() {
        assertEquals(CryptoCurrency.values().length, CryptoCurrency.getCodes().size());
        assertTrue(CryptoCurrency.getCodes().containsAll(Arrays.asList("BTC", "$PAC", "LTC")));
    }

    @Test
    void valueOfCode() {
        assertEquals(CryptoCurrency.BTC, CryptoCurrency.valueOfCode("btc"));
        assertEquals(CryptoCurrency.BTC, CryptoCurrency.valueOfCode("BTC"));
        assertEquals(CryptoCurrency.PAC, CryptoCurrency.valueOfCode("$pAC"));
        assertEquals(CryptoCurrency.PAC, CryptoCurrency.valueOfCode("$PAC"));
    }

    @Test
    void valueOfException() {
        assertThrows(IllegalArgumentException.class, () -> CryptoCurrency.valueOf("$PAC"));

    }
}
