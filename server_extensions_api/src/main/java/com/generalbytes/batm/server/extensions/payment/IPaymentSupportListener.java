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
package com.generalbytes.batm.server.extensions.payment;

/**
 * BATM Server's Payment Manager typically implements this interface.
 */
public interface IPaymentSupportListener {
    /**
     * IPaymentSupport implementation calls this method to tell server to store its payment index.
     * By index is meant index inside xpub for receive addresses.
     * @param cryptocurrency
     * @param index
     */
    void paymentIndexChanged(String cryptocurrency, int index);
}
