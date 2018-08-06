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

import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;

import java.io.File;

/**
 * {@link IPaymentSupport} is used to handle more complex interactions with coin network for BATM server.
 * For example generate address on which payment can be sent notify server when payment is received and how many confirmations has.
 * Each cryptocurrency can have only one {@link IPaymentSupport} implementation @see {@link ICryptoCurrencyDefinition}
 */
public interface IPaymentSupport {

    /**
     * BATM server calls this method to initialize your payment support.
     * @param seedMnemonic server provides you with a seed that is shared among all crypto-currencies to simplify the backup
     * @param password password for seed
     * @param file file storage which is free to be used by cryptocurrency support. Typically it is used to store block headers
     * @param paymentIndex initial index for generating receiving addresses from seed. For more read @{@link IPaymentSupportListener}
     * @return
     */
    boolean init(String seedMnemonic, String password, File file, int paymentIndex);

    /**
     * Method used by server to listen to your @{@link IPaymentSupport} implementation
     * @param listener
     */
    void addListener(IPaymentSupportListener listener);

    /**
     * Server calls this method to create request for payment.
     * Payment
     * When this method is called your {@link IPaymentSupport} implementation should start listening for payments on address
     * @param spec
     * @return
     */
    PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec);

    /**
     * This is a simple method to find out id payment was received on receiving address
     * @param paymentAddress
     * @return
     */
    boolean isPaymentReceived(String paymentAddress);

    /**
     * Same as method isPaymentReceived but has ability to provide more information.
     * For example it can signal that payment was made using InstantSend
     * @param paymentAddress
     * @return
     */
    PaymentReceipt getPaymentReceipt(String paymentAddress);
}
