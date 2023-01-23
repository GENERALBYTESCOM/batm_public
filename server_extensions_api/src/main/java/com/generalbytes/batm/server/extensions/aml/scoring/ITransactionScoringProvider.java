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
package com.generalbytes.batm.server.extensions.aml.scoring;

import java.util.Collection;

/**
 * Provides AML scoring of transactions or addresses
 */
public interface ITransactionScoringProvider {

    /**
     * @param identityPublicId identity Public ID or null if identity is not known (anonymous transaction)
     */
    ScoringResult scoreCryptoAddress(String cryptoAddress, String cryptoCurrency, String identityPublicId);

    /**
     * @param transactionId    on-chain transaction id (hash)
     * @param identityPublicId identity Public ID or null if identity is not known (anonymous transaction)
     */
    ScoringResult scoreTransaction(String transactionId, String destinationCryptoAddress, String cryptoCurrency, String identityPublicId);

    /**
     * Reports a sent transaction to the scoring provider
     *
     * @param transactionId    on-chain transaction id (hash)
     * @param identityPublicId identity Public ID or null if identity is not known (anonymous transaction)
     */
    void registerSentTransfer(String transactionId, String destinationCryptoAddress, String cryptoCurrency, String identityPublicId);

    /**
     * @return transaction scoring provider name
     */
    String getName();

    @Deprecated // not used
    Collection<String> getSupportedCryptoCurrencies();
}
