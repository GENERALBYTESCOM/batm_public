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
package com.generalbytes.batm.server.extensions.extra.bitcoinsv.test;

import com.generalbytes.batm.server.extensions.payment.IPaymentOutput;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PO implements IPaymentOutput{

    private String address;
    private BigDecimal amount;

    PO(String address, BigDecimal amount) {
        this.address = address;
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void removeAmount(BigDecimal amountToRemove) {
        amount = amount.subtract(amountToRemove).setScale(8, RoundingMode.HALF_DOWN);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PO{" +
            "address='" + address + '\'' +
            ", amount=" + amount +
            '}';
    }
}
