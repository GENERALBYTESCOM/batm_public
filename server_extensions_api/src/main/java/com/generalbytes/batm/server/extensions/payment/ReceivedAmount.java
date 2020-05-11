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
package com.generalbytes.batm.server.extensions.payment;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Amount received to an address
 */
public class ReceivedAmount {
    public static final ReceivedAmount ZERO = new ReceivedAmount(BigDecimal.ZERO, 0);

    private final BigDecimal totalAmountReceived;
    private final int confirmations;

    public ReceivedAmount(BigDecimal totalAmountReceived, int confirmations) {
        this.totalAmountReceived = Objects.requireNonNull(totalAmountReceived);
        this.confirmations = confirmations;
    }

    /**
     *
     * @return sum of all incoming transactions (outgoing transactions not taken into account)
     */
    public BigDecimal getTotalAmountReceived() {
        return totalAmountReceived;
    }

    /**
     *
     * @return number of confirmations for the latest (newest) incoming transaction
     */
    public int getConfirmations() {
        return confirmations;
    }
}
