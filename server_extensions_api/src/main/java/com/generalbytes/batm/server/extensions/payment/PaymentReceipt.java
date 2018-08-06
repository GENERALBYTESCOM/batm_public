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
 * This class contains information about the result of the payment
 * It allows to provides ability to indicate much more information about the payment than just paid and unpaid.
 * For example in DASH implementation extras field is used to indicate payment received using InstantSend
 *
 */
public class PaymentReceipt {
    /**
     * Initial state
     */
    public static final int STATUS_NOT_PAID = 0;
    /**
     * Invalid amount was received or other mistake
     */
    public static final int STATUS_INVALID_PAYMENT = 1;
    /**
     * Payment received
     */
    public static final int STATUS_PAID = 2;

    /**
     * Default = Not sure = It is not guaranteed that payment will make it into block
     */
    public static final int CONFIDENCE_NONE = 0;
    /**
     * Sure = 100% sure that payment will make it into the block or is in the block
     */
    public static final int CONFIDENCE_SURE = 1; //Instant send

    /**
     * Almost 100% certainty that payment will be received
     */
    public static final int CONFIDENCE_ALMOST_SURE = 2;

    private String cryptoCurrency;
    private String paymentAddress;
    private String transactionId;
    private BigDecimal amount;
    private int status = STATUS_NOT_PAID;
    private int confidence = CONFIDENCE_NONE;
    private String extras;

    public PaymentReceipt(String cryptoCurrency, String paymentAddress) {
        this.cryptoCurrency = cryptoCurrency;
        this.paymentAddress = paymentAddress;
    }

    /**
     * Returns cryptocurrency of this payment
     * @return
     */
    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    /**
     * Returns payment address on which the payment is or will be received
     * @return
     */
    public String getPaymentAddress() {
        return paymentAddress;
    }

    /**
     * Returns Servers internal transaction id for this payment
     * @return
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Returns amount that was or should be received
     * @return
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns status of the payment
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns confidence of this payment
     * @return
     */
    public int getConfidence() {
        return confidence;
    }

    /**
     * Property used for holding extra information about payment
     * @return
     */
    public String getExtras() {
        return extras;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public void setPaymentAddress(String paymentAddress) {
        this.paymentAddress = paymentAddress;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }
}
