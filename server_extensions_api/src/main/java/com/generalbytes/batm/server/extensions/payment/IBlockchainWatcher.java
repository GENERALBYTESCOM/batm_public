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
 * This interface defines basic set methods that BlockchainWatcher should have.
 * Basically blockchain watcher watches for transactions to appear and move trough blockchain history
 */
public interface IBlockchainWatcher {
    /**
     * Call this method to start watcher to follow transactions
     */
    void start();

    /**
     * Call this method to stop watcher
     */
    void stop();

    /**
     * Call this method to tell watcher to follow transaction. All transaction depth changes will be reported.
     * @param cryptoCurrency
     * @param transactionHash
     * @param listener
     */
    void addTransaction(String cryptoCurrency, String transactionHash, IBlockchainWatcherTransactionListener listener);

    /**
     * Stop following this transaction
     * @param transactionHash
     * @return
     */
    void removeTransaction(String transactionHash);

    /**
     * Stop watching all transactions that use this listener
     * @param listener
     */
    void removeTransactions(IBlockchainWatcherTransactionListener listener);

    /**
     * Call this method to tell watcher to follow this address. All new transactions will be reported.
     * @param cryptoCurrency
     * @param address
     * @param listener
     */
    void addAddress(String cryptoCurrency, String address, IBlockchainWatcherAddressListener listener);

    /**
     * Call this method to stop watching for new transactions on this address.
     * @param cryptoCurrency
     * @param address
     */
    void removeAddress(String cryptoCurrency, String address);

    /**
     * Call this method to stop watching foll all addresses using this listener.
     * @param listener
     */
    void removeAddresses(IBlockchainWatcherAddressListener listener);
}
