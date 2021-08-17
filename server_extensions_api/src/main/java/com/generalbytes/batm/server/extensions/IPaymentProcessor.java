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
import java.util.Set;

/**
 * Classes implementing this interface are used by the server to obtain crypto address and crypto price from Payment Processor such as BitcoinPay.com based on fiat amount.

 */
public interface IPaymentProcessor {
    /**
     * Returns Response containing details for crypto payment
     * @param fiatAmount - fiat amount
     * @param fiatCurrency - fiat currency
     * @param cryptoCurrency - the cryptocurrency user indicated to pay in
     * @param settledCurrency - in which currency you want to have payment settled
     * @param reference - reference such as order number
     * @return
     */
    IPaymentProcessorPaymentResponse requestPayment(BigDecimal fiatAmount, String fiatCurrency, String cryptoCurrency, String settledCurrency, String reference);
    IPaymentProcessorPaymentStatus getPaymentStatus(String paymentId);
    /**
     * This method returns list of supported cryptocurrencies
     * @return
     */
    Set<String> getCryptoCurrencies();

    /**
     * This method returns list of supported fiat currencies
     * @return
     */
    Set<String> getFiatCurrencies();

}
