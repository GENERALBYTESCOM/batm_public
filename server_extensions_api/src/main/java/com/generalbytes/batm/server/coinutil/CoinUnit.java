package com.generalbytes.batm.server.coinutil;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Converts between bitcoin, satoshi and millisatoshi units
 */
public class CoinUnit {
    private static final int SATOSHI = 8;
    private static final int MILLI = 3;

    /**
     * Converts satoshi to bitcoin
     */
    public static BigDecimal satToBitcoin(Long amountMsat) {
        Objects.requireNonNull(amountMsat, "amountMsat cannot be null");
        return new BigDecimal(amountMsat).movePointLeft(SATOSHI);
    }

    /**
     * Converts bitcoin to satoshi
     */
    public static Long bitcoinToSat(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount cannot be null");
        return amount.movePointRight(SATOSHI).longValue();
    }

    /**
     * Converts milli satoshi to bitcoin
     */
    public static BigDecimal mSatToBitcoin(Long amountMsat) {
        Objects.requireNonNull(amountMsat, "amountMsat cannot be null");
        return new BigDecimal(amountMsat).movePointLeft(MILLI + SATOSHI);
    }

    /**
     * Converts bitcoin to milli satoshi
     */
    public static Long bitcoinToMSat(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount cannot be null");
        return amount.movePointRight(MILLI + SATOSHI).longValue();
    }
}
