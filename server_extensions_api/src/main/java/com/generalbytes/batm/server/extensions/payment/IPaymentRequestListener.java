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


import java.math.BigDecimal;

/**
 * This interface is typically implemented by server to listen to each Payment Request
 */
public interface IPaymentRequestListener {
    /**
     * Direction of transaction - going out or in
     */
    enum Direction { INCOMING, OUTGOING}

    /**
     * @see {@link IPaymentSupport} notifies server that state of payment request has changed. To see multiple different states support ed @see {@link PaymentRequest}
     * @param request
     * @param previousState
     * @param newState
     */
    void stateChanged(PaymentRequest request, int previousState, int newState);

    /**
     * @see {@link IPaymentSupport} notifies server that number of confirmations for this request has changed.
     * Based on that server may for example allow customer to withdraw cash from machine or start selling coins on exchange
     *
     * @param request
     * @param numberOfConfirmations
     * @param direction
     */
    void numberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations, Direction direction);

    /**
     * @see {@link IPaymentSupport} notifies server that it sent refund to customer. Server based on that for example sends email
     * @param request
     * @param toAddress
     * @param cryptoCurrency
     * @param amount
     */
    void refundSent(PaymentRequest request, String toAddress, String cryptoCurrency, BigDecimal amount);
}
