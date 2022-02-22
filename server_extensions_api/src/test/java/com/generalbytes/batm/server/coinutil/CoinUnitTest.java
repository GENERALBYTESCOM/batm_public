package com.generalbytes.batm.server.coinutil;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class CoinUnitTest {

    /// satToBitcoin

    @Test
    public void satToBitcoin() {
        assertThat(CoinUnit.satToBitcoin(10_00000000L)).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    public void satToBitcoin123() {
        assertThat(CoinUnit.satToBitcoin(123L)).isEqualByComparingTo("0.00000123");
    }

    @Test
    public void satToBitcoinZero() {
        assertThat(CoinUnit.satToBitcoin(0L)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    /// mSatToBitcoin

    @Test
    public void mSatToBitcoin() {
        assertThat(CoinUnit.mSatToBitcoin(10_00000000_000L)).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    public void mSatToBitcoin123() {
        assertThat(CoinUnit.mSatToBitcoin(123L)).isEqualByComparingTo("0.00000000123");
    }

    @Test
    public void mSatToBitcoinZero() {
        assertThat(CoinUnit.mSatToBitcoin(0L)).isEqualByComparingTo(BigDecimal.ZERO);
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