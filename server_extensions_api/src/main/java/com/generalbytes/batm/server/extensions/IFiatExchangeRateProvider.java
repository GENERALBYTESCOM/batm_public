/*************************************************************************************
 * Copyright (C) 2015 GENERAL BYTES s.r.o. All rights reserved.
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
import java.util.Set;


public interface IFiatExchangeRateProvider {
    public Set<String> getFiatCurrenciesFrom();

    public Set<String> getFiatCurrenciesTo();

    public BigDecimal getRate(String fromCurrency, String toCurrency);

    /**
     * Determines the priority of this exchange rate provider in relation to other providers.
     * Higher weight value indicates higher priority when multiple providers are available.
     * Built-in providers have a weight value of {@code 0}.
     *
     * @return Weight value indicating the provider's priority. Returns {@code 0} if no specific priority is set.
     */
    default int getPriorityWeight() {
        return 0;
    }
}
