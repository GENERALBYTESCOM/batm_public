package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BinanceExchangeRoundingTest {
    BinanceUsExchange e = new BinanceUsExchange("USD");

    @Test
    void testGetAmountRoundedToMinStep() {
        assertEquals("1.1", "0.00001", "1.1");
        assertEquals("1.12345789", "0.0001", "1.1234");
        assertEquals("1.12345789", "0.002", "1.122");
        assertEquals("6.6666", "1", "6");
        assertEquals("6.6666", "1.6", "6.4");
        assertEquals("0", "0.001", "0");
    }

    private void assertEquals(String amount, String minStep, String expected) {
        assertThat(e.getAmountRoundedToMinStep(new BigDecimal(amount), new BigDecimal(minStep)))
            .isEqualByComparingTo(expected);
    }

}