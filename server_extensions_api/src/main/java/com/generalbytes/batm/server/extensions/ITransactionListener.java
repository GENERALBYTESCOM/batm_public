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
package com.generalbytes.batm.server.extensions;

import java.util.Map;

public interface ITransactionListener {
    /**
     * Callback method that is called by server before transaction is executed.
     * If your method returns false than transaction will not take place and will fail with error ERROR_NOT_APPROVED.
     * Try to return from this method in less then 10 seconds.
     * @param transactionRequest
     * @return
     */
    boolean isTransactionApproved(ITransactionRequest transactionRequest);

    /**
     * Callback method that is called by server when transaction is created on server
     * Returned value is a map of keys and values that will be stored in the database and available for later use in ticket template
     * @param transactionDetails
     * @return
     */
    Map<String,String> onTransactionCreated(ITransactionDetails transactionDetails);

    /**
     * Callback method that is called by server when transaction is updated by server
     * Returned value is a map of keys and values that will be stored in the database and available for later use in ticket template
     * @param transactionDetails
     * @return
     */
    Map<String,String> onTransactionUpdated(ITransactionDetails transactionDetails);
}
