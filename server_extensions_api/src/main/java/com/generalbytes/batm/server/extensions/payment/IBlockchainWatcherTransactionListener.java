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
package com.generalbytes.batm.server.extensions.payment;

/**
 * Classes implementing this interface will be notified about transaction changes
 */
public interface IBlockchainWatcherTransactionListener {
    /**
     * This method is called when transaction is no longer watched
     * @param cryptoCurrency
     * @param transactionHash
     * @param tag
     */
    void removedFromWatch(String cryptoCurrency, String transactionHash, Object tag);

    /**
     * This method is called when transaction is first time mined
     * @param cryptoCurrency
     * @param transactionHash
     * @param tag
     * @param blockHeight
     */
    void newBlockMined(String cryptoCurrency, String transactionHash, Object tag, long blockHeight);

    /**
     * This when transaction's number of confirmations changes
     * @param cryptoCurrency
     * @param transactionHash
     * @param tag
     * @param numberOfConfirmations
     */
    void numberOfConfirmationsChanged(String cryptoCurrency, String transactionHash, Object tag, int numberOfConfirmations);
}
