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

import java.math.BigDecimal;
import java.util.Collection;

/**
 * A wallet that is able to send different amounts to multiple addresses in a single transaction.
 * Fees must cost less than sending individual transactions separately using {@link IWallet#sendCoins}
 */
public interface ICanSendMany {

    /**
     * Send coins to multiple addresses in a single transaction.
     * Used to reduce network fees.
     *
     * @param transfers amounts and destination addresses
     * @param cryptoCurrency cryptocurrency representing the coin to be sent
     * @param description description of the transaction contains comma separated RIDs of all transactions e.g. 'ROOOOO, ROOOO1, ROOOO2'
     * @return transaction ID of the only transaction created or null in case of any error
     */
    String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description);

    /**
     * Send coins to multiple addresses in a single transaction.
     * Used to reduce network fees.
     *
     * @param transfers amounts and destination addresses
     * @param cryptoCurrency cryptocurrency representing the coin to be sent
     * @param description description of the transaction contains comma separated RIDs of all transactions e.g. 'ROOOOO, ROOOO1, ROOOO2'
     * @param batchId unique identifier of the batch of transactions
     * @return transaction ID of the only transaction created or null in case of any error
     */
    default String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description, String batchId) {
        return sendMany(transfers, cryptoCurrency, description);
    }

    class Transfer {
        private final String destinationAddress;
        private final BigDecimal amount;

        public Transfer(String destinationAddress, BigDecimal amount) {
            this.destinationAddress = destinationAddress;
            this.amount = amount;
        }

        public String getDestinationAddress() {
            return destinationAddress;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return "{" + amount + " to '" + destinationAddress + '\'' + '}';
        }
    }
}