/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay;

public class BitcoinPayPaymentRequestRequestDTO {

    private BPProduct product;
    private BPInvoice invoice;
    private BPSettlement settlement;

    private String notifyEmail;
    private String notifyUrl;
    private String returnUrl;
    private String reference;

    public BitcoinPayPaymentRequestRequestDTO(BPProduct product, BPInvoice invoice, BPSettlement settlement, String notifyEmail, String notifyUrl, String returnUrl, String reference) {
        this.product = product;
        this.invoice = invoice;
        this.settlement = settlement;
        this.notifyEmail = notifyEmail;
        this.notifyUrl = notifyUrl;
        this.returnUrl = returnUrl;
        this.reference = reference;
    }

    public BPProduct getProduct() {
        return product;
    }

    public void setProduct(BPProduct product) {
        this.product = product;
    }

    public BPInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(BPInvoice invoice) {
        this.invoice = invoice;
    }

    public BPSettlement getSettlement() {
        return settlement;
    }

    public void setSettlement(BPSettlement settlement) {
        this.settlement = settlement;
    }

    public String getNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(String notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
