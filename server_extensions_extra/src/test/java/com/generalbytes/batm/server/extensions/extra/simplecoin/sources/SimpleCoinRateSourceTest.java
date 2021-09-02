package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class SimpleCoinRateSourceTest {

    @Test
    public void getExchangeRateLast() {
        SimpleCoinRateSource rateSource = new SimpleCoinRateSource("USD");
        BigDecimal sources = rateSource.getExchangeRateLast("BTC", "USD");
        System.out.println(sources);
    }
}