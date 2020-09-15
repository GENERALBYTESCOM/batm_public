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

import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public interface ICryptoCurrencyDefinition {
    /**
     * Returns name of the cryptocurrency. For example: Bitcoin
     * @return
     */
    String getName();

    /**
     * Returns protocol of the cryptocurrency. Used on redeem tickets for example "bitcoin:address?amount=1". For example: bitcoin
     * @return
     */
    String getProtocol();

    /**
     * Link to the currency author website. For example https://en.wikipedia.org/wiki/Satoshi_Nakamoto
     * @return
     */
    String getAuthorWebsiteURL();

    /**
     * Currency symbol of cryptocurrency. For example: BTC
     * @return
     */
    String getSymbol();

    /**
     * Some of the currencies have different symbol for for itself and for its price - i.e. bitcoin lightning (LBTC + BTC)
     */
    String getRateSourceSymbol();

    /**
     * Optional implementation of support for Payment manager. IPaymentSupport is used for example in two way support for ATMs.
     * @return
     */
    IPaymentSupport getPaymentSupport();


    /**
     * Should return true if ATM should ask also for tag (for example this uses XRP). Default is false.
     * @return
     */
    boolean isRequiresTag();
}
