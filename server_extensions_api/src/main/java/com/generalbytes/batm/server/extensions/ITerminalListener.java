/*************************************************************************************
 * Copyright (C) 2014-2026 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

public interface ITerminalListener {

    /**
     * @param serialNumber   terminal serial number
     * @param cryptoCurrency
     * @param profitBuy      the percentage value configured in admin
     * @return Return null or the original profit to use the configured value or return a value that will be used instead.
     * @deprecated Use {@link #overrideBuyProfit(ProfitOverrideContext)} instead. The new method provides richer context
     * via {@link ProfitOverrideContext} and takes priority over this one when both are implemented.
     */
    @Deprecated
    default BigDecimal overrideProfitBuy(String serialNumber, String cryptoCurrency, BigDecimal profitBuy) {
        return null;
    }

    /**
     * @param serialNumber   terminal serial number
     * @param cryptoCurrency
     * @param profitSell     the percentage value configured in admin
     * @return Return null or the original profit to use the configured value or return a value that will be used instead.
     * @deprecated Use {@link #overrideSellProfit(ProfitOverrideContext)} instead. The new method provides richer context
     * via {@link ProfitOverrideContext} and takes priority over this one when both are implemented.
     */
    @Deprecated
    default BigDecimal overrideProfitSell(String serialNumber, String cryptoCurrency, BigDecimal profitSell) {
        return null;
    }

    /**
     * Allows extensions to override the evaluated buy profit percentage for a transaction.
     *
     * <p>This method replaces the deprecated {@link #overrideProfitBuy(String, String, BigDecimal)} and has
     * higher priority — if this method returns a non-null value, the result of the deprecated method is ignored.</p>
     *
     * <p>The {@link ProfitOverrideContext} provides full context about the terminal, cryptocurrency, fiat currency,
     * and the currently evaluated profit percentage.</p>
     *
     * <p>If this method returns {@code null}, the value from
     * {@link ProfitOverrideContext#getEvaluatedProfitPercentage()} will be used.</p>
     *
     * @param context contains the profit evaluation context and the current evaluated profit percentage
     * @return a new profit percentage to use, or {@code null} to keep {@link ProfitOverrideContext#getEvaluatedProfitPercentage()}
     */
    default BigDecimal overrideBuyProfit(ProfitOverrideContext context) {
        return null;
    }

    /**
     * Allows extensions to override the evaluated sell profit percentage for a transaction.
     *
     * <p>This method replaces the deprecated {@link #overrideProfitSell(String, String, BigDecimal)} and has
     * higher priority — if this method returns a non-null value, the result of the deprecated method is ignored.</p>
     *
     * <p>The {@link ProfitOverrideContext} provides full context about the terminal, cryptocurrency, fiat currency,
     * and the currently evaluated profit percentage.</p>
     *
     * <p>If this method returns {@code null}, the value from
     * {@link ProfitOverrideContext#getEvaluatedProfitPercentage()} will be used.</p>
     *
     * @param context contains the profit evaluation context and the current evaluated profit percentage
     * @return a new profit percentage to use, or {@code null} to keep {@link ProfitOverrideContext#getEvaluatedProfitPercentage()}
     */
    default BigDecimal overrideSellProfit(ProfitOverrideContext context) {
        return null;
    }
}
