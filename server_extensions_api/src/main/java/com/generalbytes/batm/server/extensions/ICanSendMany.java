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
     * @param cryptoCurrency
     * @param description
     * @return transaction ID of the only transaction created or null in case of any error
     */
    String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description);

    class Transfer {
        private final String destinationAddress;
        private final BigDecimal amount;
        private final String batchUID;

        public Transfer(String destinationAddress, BigDecimal amount) {
            this.destinationAddress = destinationAddress;
            this.amount = amount;
            this.batchUID = null;
        }

        public Transfer(String destinationAddress, BigDecimal amount, String batchUID) {
            this.destinationAddress = destinationAddress;
            this.amount = amount;
            this.batchUID = batchUID;
        }

        public String getDestinationAddress() {
            return destinationAddress;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getBatchUID() {
            return batchUID;
        }

        @Override
        public String toString() {
            return "{" + amount + " to '" + destinationAddress + '\'' + '}';
        }
    }
}