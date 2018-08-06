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
 * PaymentRequest can define that wen payment is received than new forwarding transaction should be generated.
 * Forwarding transaction can contain multiple outputs.
 * For example payment for T-shirt is received and part of the payment is forwarded to shop owner and part of it to t-shirt manufacturer.
 */
public interface IPaymentOutput {
    /**
     * Destination address
     * @return
     */
    String getAddress();

    /**
     * Sometimes you want to change destination address. For example when converting 3 to M address format
     */
    void setAddress(String address);

    /**
     * Amount that should go to destination
     * @return
     */
    BigDecimal getAmount();

    /**
     * This method is used to correct/decrease the amount
     * @param amountToRemove
     */
    void removeAmount(BigDecimal amountToRemove);
}
