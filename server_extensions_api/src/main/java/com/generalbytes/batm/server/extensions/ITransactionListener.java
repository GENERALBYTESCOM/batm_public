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
package com.generalbytes.batm.server.extensions;

import java.util.Map;

public interface ITransactionListener {

    /**
     * Called before the person inserts cash, or sell or withdraw screen is entered but after the moment when identity is established and limits are calculated.
     * @return when returned false, error message is displayed to user or withdrawal reason
     */
    default boolean isTransactionPreparationApproved(ITransactionPreparation preparation) {
        return true;
    }

    /**
     * Allows the operator to override following values in {@link ITransactionPreparation}.
     * <ul>
     *     <li>cryptoAddress</li>
     *     <li>cashTransactionLimitWithName</li>
     *     <li>cashTransactionMinimum</li>
     *     <li>supplyTransactionLimit</li>
     *     <li>allowedDiscountCode</li>
     * </ul>
     * This method is called for both BUY and SELL transactions.
     *
     * @param preparation The transaction preparation details, including calculated values.
     * @return {@link ITransactionPreparation} that may contain modified transaction details.
     */
    default ITransactionPreparation overrideTransactionPreparation(ITransactionPreparation preparation) {
        return preparation;
    }

    /**
     * Callback method that is called by server before transaction is executed - however the cash is already inserted in machine in case of buy transaction.
     * If your method returns false than transaction will not take place and will fail with error ERROR_NOT_APPROVED.
     * Try to return from this method in less then 10 seconds.
     */
    default boolean isTransactionApproved(ITransactionRequest transactionRequest) {
        return true;
    }

    /**
     * Called when there is an Output Queue configured for a BUY transaction on the server
     * and the transaction is about to be queued instead.
     * The default rules for inserting into the given queue (configured in Output Queue in admin) are passed in as an argument
     * and extensions can return overridden rules.
     * @param transactionQueueRequest information about the transaction that is going to be queued
     * @param outputQueueInsertConfig rules for inserting into the queue configured in admin or possibly already overridden by another extension
     * @return new rules for inserting the transaction into the queue.
     * This can be a new {@link OutputQueueInsertConfig} instance or the same one that is passed in (modified or not).
     * Returning null has the same effect as returning unmodified outputQueueInsertConfig argument.
     */
    default OutputQueueInsertConfig overrideOutputQueueInsertConfig(ITransactionQueueRequest transactionQueueRequest, OutputQueueInsertConfig outputQueueInsertConfig) {
        return null;
    }

    /**
     * Callback method that is called by server when transaction is created on server
     * Returned value is a map of keys and values that will be stored in the database and available for later use in ticket template
     */
    default Map<String,String> onTransactionCreated(ITransactionDetails transactionDetails) {
        return null;
    }

    /**
     * Callback method that is called by server when transaction is updated by server
     * Returned value is a map of keys and values that will be stored in the database and available for later use in ticket template
     */
    default Map<String,String> onTransactionUpdated(ITransactionDetails transactionDetails) {
        return null;
    }

    /**
     * Callback method that is called by server with user email address or cellphone number after the receipt is sent to the user
     */
    default void receiptSent(IReceiptDetails receiptDetails) {
    }
}
