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

public class BPFlags {
    private boolean refundable;
    private String notRefundableCause;
    private String resolvableStatus;

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public String getNotRefundableCause() {
        return notRefundableCause;
    }

    public void setNotRefundableCause(String notRefundableCause) {
        this.notRefundableCause = notRefundableCause;
    }

    public String getResolvableStatus() {
        return resolvableStatus;
    }

    public void setResolvableStatus(String resolvableStatus) {
        this.resolvableStatus = resolvableStatus;
    }
}
