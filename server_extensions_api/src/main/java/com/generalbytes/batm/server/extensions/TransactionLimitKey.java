package com.generalbytes.batm.server.extensions;

import lombok.Getter;

/**
 * Identifies a customer limit that can be overridden in
 * {@link ITransactionListener#overrideTransactionLimits(LimitOverrideContext)}.
 *
 * <p>Each constant has three keys:
 * <ul>
 *     <li>{@link #key()} overrides the limit for both buy and sell transactions,</li>
 *     <li>{@link #buyKey()} only for buy transactions,</li>
 *     <li>{@link #sellKey()} only for sell transactions.</li>
 * </ul>
 * If both a direction-specific and a general key are returned for the same limit, the direction-specific one is used.
 *
 * <pre>{@code
 * Map.of("USD", Map.of(
 *         TransactionLimitKey.PER_DAY.key(), new BigDecimal("500"),
 *         TransactionLimitKey.PER_MONTH.buyKey(), new BigDecimal("2000")))
 * }</pre>
 *
 * <p>The abbreviations are the same as the limit shortcuts in {@link ILimitExtended#getLimitType()}.
 */
@Getter
public enum TransactionLimitKey {

    /** Cash per transaction. */
    PER_TRANSACTION("cptr"),
    /** Cash per hour. */
    PER_HOUR("cph"),
    /** Cash per day. */
    PER_DAY("cpd"),
    /** Cash per week. */
    PER_WEEK("cpw"),
    /** Cash per month. */
    PER_MONTH("cpm"),
    /** Cash per 3 floating months. */
    PER_3_MONTHS("cp3m"),
    /** Cash per 6 floating months. */
    PER_6_MONTHS("cp6m"),
    /** Cash per 12 floating months. */
    PER_12_MONTHS("cp12m"),
    /** Cash per calendar quarter. */
    PER_QUARTER("cpq"),
    /** Cash per calendar year. */
    PER_YEAR("cpy"),
    /** Total cash identity limit. */
    TOTAL("ct");

    private static final String BUY_PREFIX = "buy_";
    private static final String SELL_PREFIX = "sell_";

    /**
     * The limit abbreviation without a direction prefix, e.g. {@code cpd}.
     */
    private final String abbreviation;

    TransactionLimitKey(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * The key that overrides the limit for both buy and sell transactions, e.g. {@code cpd}.
     */
    public String key() {
        return abbreviation;
    }

    /**
     * The key that overrides the limit for buy transactions only, e.g. {@code buy_cpd}.
     */
    public String buyKey() {
        return BUY_PREFIX + abbreviation;
    }

    /**
     * The key that overrides the limit for sell transactions only, e.g. {@code sell_cpd}.
     */
    public String sellKey() {
        return SELL_PREFIX + abbreviation;
    }
}
