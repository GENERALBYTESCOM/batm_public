package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class BinanceExchangeRoundingTest {
    BinanceUsExchange e = new BinanceUsExchange("USD");

    @Test
    public void testGetAmountRoundedToMinStep() {
        assertEquals("1.1", "0.00001", "1.1");
        assertEquals("1.12345789", "0.0001", "1.1234");
        assertEquals("1.12345789", "0.002", "1.122");
        assertEquals("6.6666", "1", "6");
        assertEquals("6.6666", "1.6", "6.4");
        assertEquals("0", "0.001", "0");
    }

    private void assertEquals(String amount, String minStep, String expected) {
        BigDecimal actual = e.getAmountRoundedToMinStep(new BigDecimal(amount), new BigDecimal(minStep));
        if(new BigDecimal(expected).compareTo(actual) != 0){ // BigDecimals need to be compared by compareTo, not equals
            Assert.assertEquals(new BigDecimal(expected), actual);
        }
    }

}