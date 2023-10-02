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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Payment {
    /**
     * A bare-bones invoice for a payment within the Lightning Network. With the details of the invoice, the sender has all the data necessary to send a payment to the recipient
     */
    public String payment_request;

    /**
     * Number of satoshis to send
     */
    public String amt;

    /**
     * The maximum number of satoshis that will be paid as a fee of the payment. This value can be represented either as
     * a percentage of the amount being sent, or as a fixed amount of the maximum fee the user is willing the pay to send the payment
     */
    public FeeLimit fee_limit;

    /**
     * An optional field that can be used to pass an arbitrary set of TLV records to a peer which understands the new records.
     * This can be used to pass application specific data during the payment attempt.
     * Record types are required to be in the custom range >= 65536.
     * When using REST, the values must be encoded as base64.
     */
    public Map<Long, byte[]> dest_custom_records;

    public static class FeeLimit {
        /**
         * The fee limit expressed as a fixed amount of satoshis
         */
        public String fixed;
        /**
         * The fee limit expressed as a percentage of the payment amount
         */
        public String percent;

        @Override
        public String toString() {
            return "FeeLimit{" + fixed + " sat fixed, " + percent + "%}";
        }
    }

    @Override
    public String toString() {
        String rid = "";
        if (dest_custom_records != null && !dest_custom_records.containsKey(1L)) {
            rid = new String(dest_custom_records.get(1L), StandardCharsets.UTF_8);
        }
        return "Payment{" + amt + " sat, fee limit: " + fee_limit + " to " + payment_request + ", rid: " + rid + '}';
    }
}
