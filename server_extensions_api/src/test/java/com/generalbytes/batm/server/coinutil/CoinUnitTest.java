package com.generalbytes.batm.server.coinutil;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoinUnitTest {

    /// satToBitcoin

    @Test
   void satToBitcoin() {
        assertThat(CoinUnit.satToBitcoin(10_00000000L)).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void satToBitcoin123() {
        assertThat(CoinUnit.satToBitcoin(123L)).isEqualByComparingTo("0.00000123");
    }

    @Test
    void satToBitcoinZero() {
        assertThat(CoinUnit.satToBitcoin(0L)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    /// mSatToBitcoin

    @Test
    void mSatToBitcoin() {
        assertThat(CoinUnit.mSatToBitcoin(10_00000000_000L)).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void mSatToBitcoin123() {
        assertThat(CoinUnit.mSatToBitcoin(123L)).isEqualByComparingTo("0.00000000123");
    }

    @Test
    void mSatToBitcoinZero() {
        assertThat(CoinUnit.mSatToBitcoin(0L)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    /// bitcoinToSat

    @Test
    void bitcoinToSat() {
        assertEquals((Long) 10_00000000L, CoinUnit.bitcoinToSat(BigDecimal.TEN));
    }

    @Test
    void bitcoinToSat123() {
        assertEquals((Long) 123L, CoinUnit.bitcoinToSat(new BigDecimal("0.00000123")));
    }

    @Test
    void bitcoinToSatRound1() {
        assertEquals((Long) 1L, CoinUnit.bitcoinToSat(new BigDecimal("0.000000011")));
    }

    @Test
    void bitcoinToSatRound9() {
        assertEquals((Long) 1L, CoinUnit.bitcoinToSat(new BigDecimal("0.000000019")));
    }

    @Test
    void bitcoinToSatZero() {
        assertEquals((Long) 0L, CoinUnit.bitcoinToSat(BigDecimal.ZERO));
    }

    /// bitcoinToMSat

    @Test
    void bitcoinToMSat() {
        assertEquals((Long) 10_00000000_000L, CoinUnit.bitcoinToMSat(BigDecimal.TEN));
    }

    @Test
    void bitcoinToMSat123() {
        assertEquals((Long) 123L, CoinUnit.bitcoinToMSat(new BigDecimal("0.00000000123")));
    }

    @Test
    void bitcoinToMSatRound1() {
        assertEquals((Long) 1L, CoinUnit.bitcoinToMSat(new BigDecimal("0.000000000011")));
    }

    @Test
    void bitcoinToMSatRound9() {
        assertEquals((Long) 1L, CoinUnit.bitcoinToMSat(new BigDecimal("0.000000000019")));
    }

    @Test
    void bitcoinToMSatZero() {
        assertEquals((Long) 0L, CoinUnit.bitcoinToMSat(BigDecimal.ZERO));
    }

    /// null

    @Test
    void bitcoinToMSatNull() {
        assertThrows(NullPointerException.class, () -> CoinUnit.bitcoinToMSat(null));
    }

    @Test
    void bitcoinToSatNull() {
        assertThrows(NullPointerException.class, () -> CoinUnit.bitcoinToSat(null));
    }

    @Test
    void mSatToBitcoinNull() {
        assertThrows(NullPointerException.class, () -> CoinUnit.mSatToBitcoin(null));
    }

    @Test
    void satToBitcoinNull() {
        assertThrows(NullPointerException.class, () -> CoinUnit.satToBitcoin(null));
    }
}