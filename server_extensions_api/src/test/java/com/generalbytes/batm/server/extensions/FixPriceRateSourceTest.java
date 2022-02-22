package com.generalbytes.batm.server.extensions;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class FixPriceRateSourceTest {

    @Test
    public void testRateIsValid() {
        doFixRateTest(new BigDecimal("10000000000"), false);
        doFixRateTest(new BigDecimal("9999999999.99999999991"), false);
        doFixRateTest(new BigDecimal("1000000000.123456789"), true);
        doFixRateTest(new BigDecimal("9999999999.9999999999"), true);
    }

    private void doFixRateTest(BigDecimal rate, boolean expected) {
        FixPriceRateSource rateSource = new FixPriceRateSource(rate, "USD");
        BigDecimal exchangeRateLast = rateSource.getExchangeRateLast("BTC", "USD");
        if (expected) {
            assertThat(exchangeRateLast).isEqualByComparingTo(rate);
        } else {
            assertNull(exchangeRateLast);
        }
    }
}