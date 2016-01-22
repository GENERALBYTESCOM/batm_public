/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto;

import java.math.BigDecimal;

public class AccountResponse {
    private String publicKey;
    private long  requestProcessingTime;
    private String guaranteedBalanceNQT;
    private String unconfirmedBalanceNQT;
    private String forgedBalanceNQT;
    private String balanceNQT;
    private String accountRS;
    private String account;
    private BigDecimal effectiveBalanceNXT;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public long getRequestProcessingTime() {
        return requestProcessingTime;
    }

    public void setRequestProcessingTime(long requestProcessingTime) {
        this.requestProcessingTime = requestProcessingTime;
    }

    public String getGuaranteedBalanceNQT() {
        return guaranteedBalanceNQT;
    }

    public void setGuaranteedBalanceNQT(String guaranteedBalanceNQT) {
        this.guaranteedBalanceNQT = guaranteedBalanceNQT;
    }

    public String getUnconfirmedBalanceNQT() {
        return unconfirmedBalanceNQT;
    }

    public void setUnconfirmedBalanceNQT(String unconfirmedBalanceNQT) {
        this.unconfirmedBalanceNQT = unconfirmedBalanceNQT;
    }

    public String getForgedBalanceNQT() {
        return forgedBalanceNQT;
    }

    public void setForgedBalanceNQT(String forgedBalanceNQT) {
        this.forgedBalanceNQT = forgedBalanceNQT;
    }

    public String getBalanceNQT() {
        return balanceNQT;
    }

    public void setBalanceNQT(String balanceNQT) {
        this.balanceNQT = balanceNQT;
    }

    public String getAccountRS() {
        return accountRS;
    }

    public void setAccountRS(String accountRS) {
        this.accountRS = accountRS;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getEffectiveBalanceNXT() {
        return effectiveBalanceNXT;
    }

    public void setEffectiveBalanceNXT(BigDecimal effectiveBalanceNXT) {
        this.effectiveBalanceNXT = effectiveBalanceNXT;
    }
}
