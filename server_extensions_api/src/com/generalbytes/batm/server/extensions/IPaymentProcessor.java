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
import java.util.Set;

/**
 * Classes implementing this interface are used by the server to obtain crypto address and crypto price from Payment Processor such as BitcoinPay.com based on fiat amount.

 */
public interface IPaymentProcessor {
    /**
     * Returns Response containing details for crypto payment
     * @param amount - fiat amount
     * @param currency - fiat currency
     * @param settledCurrency - in which currency you want to have payment settled
     * @param reference - reference such as order number
     * @return
     */
    public IPaymentProcessorPaymentResponse requestPayment(BigDecimal amount, String currency, String settledCurrency, String reference);
    public IPaymentProcessorPaymentStatus getPaymentStatus(String paymentId);
    /**
     * This method returns list of supported crypto currencies
     * @return
     */
    public Set<String> getCryptoCurrencies();

    /**
     * This method returns list of supported fiat currencies
     * @return
     */
    public Set<String> getFiatCurrencies();

}
