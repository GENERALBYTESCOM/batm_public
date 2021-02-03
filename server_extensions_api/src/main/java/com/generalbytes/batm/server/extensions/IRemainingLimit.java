package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Class that represents identity's remaining limit
 */
public interface IRemainingLimit {
    /**
     * Type of the limit
     * Limit type has following structure: buy/sell_limitshortcut
     * Limit shortcuts are:
     * cptr - cash per transaction
     * cph - cash per hour
     * cpd - cash per day
     * cpdt - cash per day per terminal
     * cpw - cash per week
     * cpm - cash per month
     * cp3m - cash per 3 floating months
     * cp12m - cash per 12 floating months
     * cpq - cash per calendar quarter
     * cpy - cash per calendar year
     * ct - total cash identity limit
     * cpta - total cash limit per crypto address
     * resulting_limit - effective current limit
     *
     * @return
     */
    String getLimitType();

    /**
     * Limit amount
     * @return
     */
    BigDecimal getAmount();
}
