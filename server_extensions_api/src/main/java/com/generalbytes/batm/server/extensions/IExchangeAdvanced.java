/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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


public interface IExchangeAdvanced extends IExchange{
    /**
     * This method is used for instantiating task that is later called to purchase coins on the exchange using specified fiat currency to use.
     * If something fails this method should return NULL otherwise it should return orderId or any other identifier that exchange provides for later tracking
     *
     * @param amount
     * @param cryptoCurrency
     * @param fiatCurrencyToUse
     * @param description
     * @return
     */
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description);

    /**
     * This method is used for instantiating task that is later called to sell coins on the exchange for specified fiat currency.
     * If something fails this method should return NULL otherwise it should return orderId or any other identifier that exchange provides for later tracking
     *
     * @param amount
     * @param cryptoCurrency
     * @param fiatCurrencyToUse
     * @param description
     * @return
     */
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description);
}
