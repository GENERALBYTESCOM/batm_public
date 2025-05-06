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

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;

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
     * Allows the operator to override following values in {@link ITransactionRequest}.
     * <ul>
     *     <li>cryptoAmount</li>
     * </ul>
     * This method is called for both BUY and SELL transactions.
     *
     * @param request The transaction request initialized by server
     * @return {@link ITransactionRequest} that may contain modified transaction request.
     */
    default ITransactionRequest overrideTransactionRequest(ITransactionRequest request) {
        return request;
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
     * Called when there is an Input Queue configured for a SELL transaction on the server
     * and the transaction is about to be queued instead.
     * The default rules for inserting into the given queue (configured in Input Queue in admin) are passed in as an argument
     * and extensions can return overridden rules.
     *
     * @param transactionQueueRequest information about the transaction that is going to be queued
     * @param inputQueueInsertConfig  rules for inserting into the queue configured in admin or possibly already overridden by another extension
     * @return new rules for inserting the transaction into the queue.
     * This can be a new {@link InputQueueInsertConfig} instance or the same one that is passed in (modified or not).
     * Returning null has the same effect as returning unmodified inputQueueInsertConfig argument.
     */
    default InputQueueInsertConfig overrideInputQueueInsertConfig(ITransactionQueueRequest transactionQueueRequest,
                                                                  InputQueueInsertConfig inputQueueInsertConfig) {
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

    /**
     * Callback method that is called by server when a deposit transaction is created on server
     * Returned value is a map of keys and values that will be stored in the database and available for later use in ticket template
     *
     * @param depositDetails {@link IDepositDetails}
     * @return map containing custom data related to the deposit
     */
    default Map<String, String> onDepositCreated(IDepositDetails depositDetails) {
        return null;
    }

    /**
     * Allows to approve or deny deposit preparation. Called after person inserts deposit code but before inserting money into GB Safe.
     * When returned {@code false}, provided error message via {@link IDepositPreparation#getErrorMessage()} is displayed to user.
     *
     * @return result of the approval
     */
    default boolean isDepositPreparationApproved(IDepositPreparation preparation) {
        return true;
    }

    /**
     * Allows the operator to override following values in {@link IDepositPreparation}.
     * <ul>
     *     <li>cashAmount</li> - cannot be higher than the provided amount, if yes will be reduced back to the provided amount
     *     <li>errorMessage</li>
     * </ul>
     * The method is called right before {@link ITransactionListener#isDepositPreparationApproved(IDepositPreparation)},
     * this allows to override values in preparation before the approval check.
     *
     * @param preparation The deposit preparation data
     * @return {@link IDepositPreparation} that may contain modified data from an extension
     */
    default IDepositPreparation overrideDepositPreparation(IDepositPreparation preparation) {
        return preparation;
    }

    /**
     * Allows to approve or deny a deposit request. Called by server before a deposit transaction is created.
     * When returned {@code false}, provided error message via {@link IDepositRequest#getErrorMessage()} is displayed to user
     * and the transaction will be created in an ERROR status.
     *
     * @return result of the approval
     */
    default boolean isDepositApproved(IDepositRequest request) {
        return true;
    }

    /**
     * Allows the operator to override following values in {@link IDepositRequest}.
     * <ul>
     *     <li>cashAmount</li> - cannot be higher than the provided amount, if yes will be reduced back to the provided amount
     *     <li>errorMessage</li>
     * </ul>
     * This method is called for both BUY and SELL transactions.
     *
     * @param request The transaction request initialized by server
     * @return {@link ITransactionRequest} that may contain modified transaction request.
     */
    default IDepositRequest overrideDepositRequest(IDepositRequest request) {
        return request;
    }

    /**
     * This method can be used to approve/reject an incoming transfer.
     *
     * <p>Called when an incoming transfer is received using {@link ITravelRuleTransferListener#onIncomingTransferReceived}
     * from the Travel Rule provider.</p>
     *
     * <p>Called before the internal evaluation of the transfer on the server and the result returned from this method
     * always takes precedence. If you want to use the server's internal evaluation mechanism,
     * return {@link TravelRuleProviderTransferStatus#IN_PROGRESS}.</p>
     *
     * <p>When calling this method, it is guaranteed that the transfer can be set to both APPROVED and REJECTED status.</p>
     *
     * @param storedTransferData   Transfer data which is already stored on the server.
     * @param incomingTransferData Newly received data from Travel Rule provider.
     * @return The new status to which the transfer is to be transitioned.
     *         If {@link TravelRuleProviderTransferStatus#IN_PROGRESS} is returned,
     *         the status will be further evaluated according to the internal mechanism on the server.
     */
    default TravelRuleProviderTransferStatus evaluateTravelRuleIncomingTransfer(ITravelRuleTransferData storedTransferData,
                                                                                ITravelRuleIncomingTransferData incomingTransferData
    ) {
        return TravelRuleProviderTransferStatus.IN_PROGRESS;
    }

}
