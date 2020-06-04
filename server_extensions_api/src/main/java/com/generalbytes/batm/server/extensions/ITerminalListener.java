/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
     * @param serialNumber terminal serial number
     * @param cryptoCurrency
     * @param profitBuy the percentage value configured in admin
     * @return Return null or the original profit to use the configured value or return a value that will be used instead.
     */
    BigDecimal overrideProfitBuy(String serialNumber, String cryptoCurrency, BigDecimal profitBuy);

    /**
     * @param serialNumber terminal serial number
     * @param cryptoCurrency
     * @param profitSell the percentage value configured in admin
     * @return Return null or the original profit to use the configured value or return a value that will be used instead.
     */
    BigDecimal overrideProfitSell(String serialNumber, String cryptoCurrency, BigDecimal profitSell);
}
