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

import java.math.BigDecimal;

public class BPTakeInfo {
    private boolean taken;
    private long takenAt;
    private long expiration;
    private long expirationDuration;
    private BigDecimal originalSettlementAmount;
    private BigDecimal offeredPrice;
    private BigDecimal offeredSettlementAmount;
    private BigDecimal customerCurrency;
    private String settlementCurrency;

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public long getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(long takenAt) {
        this.takenAt = takenAt;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getExpirationDuration() {
        return expirationDuration;
    }

    public void setExpirationDuration(long expirationDuration) {
        this.expirationDuration = expirationDuration;
    }

    public BigDecimal getOriginalSettlementAmount() {
        return originalSettlementAmount;
    }

    public void setOriginalSettlementAmount(BigDecimal originalSettlementAmount) {
        this.originalSettlementAmount = originalSettlementAmount;
    }

    public BigDecimal getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(BigDecimal offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public BigDecimal getOfferedSettlementAmount() {
        return offeredSettlementAmount;
    }

    public void setOfferedSettlementAmount(BigDecimal offeredSettlementAmount) {
        this.offeredSettlementAmount = offeredSettlementAmount;
    }

    public BigDecimal getCustomerCurrency() {
        return customerCurrency;
    }

    public void setCustomerCurrency(BigDecimal customerCurrency) {
        this.customerCurrency = customerCurrency;
    }

    public String getSettlementCurrency() {
        return settlementCurrency;
    }

    public void setSettlementCurrency(String settlementCurrency) {
        this.settlementCurrency = settlementCurrency;
    }
}
