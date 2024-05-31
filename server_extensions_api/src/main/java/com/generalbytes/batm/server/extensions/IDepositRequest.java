/*************************************************************************************
 * Copyright (C) 2014-2024 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Represents a deposit request submitted by a customer.
 */
public interface IDepositRequest {

    /**
     * Serial number of the GB Safe where the deposit was made.
     */
    String getSafeSerialNumber();

    /**
     * Deposit code used to identify the deposit. Equals to the RID of the order transaction.
     */
    String getDepositCode();

    /**
     * Local transaction ID of the deposit transaction. Generated by GB Safe when preparing deposit.
     */
    String getLocalTransactionId();

    /**
     * Remote transaction ID of the deposit transaction.
     */
    String getRemoteTransactionId();

    /**
     * Amount of cash deposited. Can be overridden by an extension.
     */
    BigDecimal getCashAmount();

    /**
     * Fiat currency of the cash deposited.
     */
    String getCashCurrency();

    /**
     * Identity public ID of the customer who made the deposit.
     */
    String getIdentityPublicId();

    /**
     * Error message in case of error.
     */
    String getErrorMessage();

}
