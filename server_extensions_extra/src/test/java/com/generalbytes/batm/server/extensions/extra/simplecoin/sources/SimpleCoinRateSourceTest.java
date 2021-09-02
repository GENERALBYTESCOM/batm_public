package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

import org.junit.Test;

import java.math.BigDecimal;

public class SimpleCoinRateSourceTest {

    @Test
    public void getExchangeRateLast() {
        SimpleCoinRateSource rateSource = new SimpleCoinRateSource("USD");
        try {
            BigDecimal source = rateSource.getExchangeRateLast("BAKL", "USD");
            if (source == null) {
                throw new Exception();
            }
            System.out.println("Current USD to BTC rate: " + source);

        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }
}