package com.generalbytes.batm.server.extensions.util;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class CoinUnitTest {

    private void assertEquals(BigDecimal expected, BigDecimal actual) {
        Assert.assertTrue("expected: " + expected + ", actual: " + actual, expected.compareTo(actual) == 0);
    }

    /// satToBitcoin

    @Test
    public void satToBitcoin() {
        assertEquals(BigDecimal.TEN, CoinUnit.satToBitcoin(10_00000000L));
    }

    @Test
    public void satToBitcoin123() {
        assertEquals(new BigDecimal("0.00000123"), CoinUnit.satToBitcoin(123L));
    }

    @Test
    public void satToBitcoinZero() {
        assertEquals(BigDecimal.ZERO, CoinUnit.satToBitcoin(0L));
    }

    /// mSatToBitcoin

    @Test
    public void mSatToBitcoin() {
        assertEquals(BigDecimal.TEN, CoinUnit.mSatToBitcoin(10_00000000_000L));
    }

    @Test
    public void mSatToBitcoin123() {
        assertEquals(new BigDecimal("0.00000000123"), CoinUnit.mSatToBitcoin(123L));
    }

    @Test
    public void mSatToBitcoinZero() {
        assertEquals(BigDecimal.ZERO, CoinUnit.mSatToBitcoin(0L));
    }

    /// bitcoinToSat

    @Test
    public void bitcoinToSat() {
        Assert.assertEquals((Long) 10_00000000L, CoinUnit.bitcoinToSat(BigDecimal.TEN));
    }

    @Test
    public void bitcoinToSat123() {
        Assert.assertEquals((Long) 123L, CoinUnit.bitcoinToSat(new BigDecimal("0.00000123")));
    }

    @Test
    public void bitcoinToSatRound1() {
        Assert.assertEquals((Long) 1L, CoinUnit.bitcoinToSat(new BigDecimal("0.000000011")));
    }

    @Test
    public void bitcoinToSatRound9() {
        Assert.assertEquals((Long) 1L, CoinUnit.bitcoinToSat(new BigDecimal("0.000000019")));
    }

    @Test
    public void bitcoinToSatZero() {
        Assert.assertEquals((Long) 0L, CoinUnit.bitcoinToSat(BigDecimal.ZERO));
    }

    /// bitcoinToMSat

    @Test
    public void bitcoinToMSat() {
        Assert.assertEquals((Long) 10_00000000_000L, CoinUnit.bitcoinToMSat(BigDecimal.TEN));
    }

    @Test
    public void bitcoinToMSat123() {
        Assert.assertEquals((Long) 123L, CoinUnit.bitcoinToMSat(new BigDecimal("0.00000000123")));
    }

    @Test
    public void bitcoinToMSatRound1() {
        Assert.assertEquals((Long) 1L, CoinUnit.bitcoinToMSat(new BigDecimal("0.000000000011")));
    }

    @Test
    public void bitcoinToMSatRound9() {
        Assert.assertEquals((Long) 1L, CoinUnit.bitcoinToMSat(new BigDecimal("0.000000000019")));
    }

    @Test
    public void bitcoinToMSatZero() {
        Assert.assertEquals((Long) 0L, CoinUnit.bitcoinToMSat(BigDecimal.ZERO));
    }

    /// null

    @Test(expected = NullPointerException.class)
    public void bitcoinToMSatNull() {
        CoinUnit.bitcoinToMSat(null);
    }

    @Test(expected = NullPointerException.class)
    public void bitcoinToSatNull() {
        CoinUnit.bitcoinToSat(null);
    }

    @Test(expected = NullPointerException.class)
    public void mSatToBitcoinNull() {
        CoinUnit.mSatToBitcoin(null);
    }

    @Test(expected = NullPointerException.class)
    public void satToBitcoinNull() {
        CoinUnit.satToBitcoin(null);
    }
}