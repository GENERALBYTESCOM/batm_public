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
package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay;


import java.util.HashMap;

public class BitcoinPayPaymentResponseDTO {
    private String cryptoUri;
    private String emailInvoiceId;
    private String buttonInvoiceId;
    private BPAmount merchantAmount;
    private BPAmount settlementAmount;
    private String notifyEmail;
    private String notifyUrl;

    private long paidSince;
    private BPPaid paid;
    private BPProduct product;
    private HashMap settlement;

    private String reference;
    private String[] refunds;

    private long timeoutTime;
    private String[] cryptoTransactions;
    private boolean unhandledExceptions;
    private String url;
    private String refundLink;
    private String id;

    private int requiredConfirmations;
    private String returnUrl;
    private BPRate rate;
    private String status;
    private BPTakeInfo takeInfo;

    public String getCryptoUri() {
        return cryptoUri;
    }

    public void setCryptoUri(String cryptoUri) {
        this.cryptoUri = cryptoUri;
    }

    public String getEmailInvoiceId() {
        return emailInvoiceId;
    }

    public void setEmailInvoiceId(String emailInvoiceId) {
        this.emailInvoiceId = emailInvoiceId;
    }

    public String getButtonInvoiceId() {
        return buttonInvoiceId;
    }

    public void setButtonInvoiceId(String buttonInvoiceId) {
        this.buttonInvoiceId = buttonInvoiceId;
    }

    public BPAmount getMerchantAmount() {
        return merchantAmount;
    }

    public void setMerchantAmount(BPAmount merchantAmount) {
        this.merchantAmount = merchantAmount;
    }

    public BPAmount getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BPAmount settlementAmount) {
        this.settlementAmount = settlementAmount;
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

    public long getPaidSince() {
        return paidSince;
    }

    public void setPaidSince(long paidSince) {
        this.paidSince = paidSince;
    }

    public BPProduct getProduct() {
        return product;
    }

    public void setProduct(BPProduct product) {
        this.product = product;
    }

    public HashMap getSettlement() {
        return settlement;
    }

    public void setSettlement(HashMap settlement) {
        this.settlement = settlement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String[] getRefunds() {
        return refunds;
    }

    public void setRefunds(String[] refunds) {
        this.refunds = refunds;
    }

    public long getTimeoutTime() {
        return timeoutTime;
    }

    public void setTimeoutTime(long timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

    public String[] getCryptoTransactions() {
        return cryptoTransactions;
    }

    public void setCryptoTransactions(String[] cryptoTransactions) {
        this.cryptoTransactions = cryptoTransactions;
    }

    public boolean isUnhandledExceptions() {
        return unhandledExceptions;
    }

    public void setUnhandledExceptions(boolean unhandledExceptions) {
        this.unhandledExceptions = unhandledExceptions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRefundLink() {
        return refundLink;
    }

    public void setRefundLink(String refundLink) {
        this.refundLink = refundLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRequiredConfirmations() {
        return requiredConfirmations;
    }

    public void setRequiredConfirmations(int requiredConfirmations) {
        this.requiredConfirmations = requiredConfirmations;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public BPRate getRate() {
        return rate;
    }

    public void setRate(BPRate rate) {
        this.rate = rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BPTakeInfo getTakeInfo() {
        return takeInfo;
    }

    public void setTakeInfo(BPTakeInfo takeInfo) {
        this.takeInfo = takeInfo;
    }


}
