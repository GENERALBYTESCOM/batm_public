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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

public class Invoice {
    public String serialized;
    public Long amount;
    public String paymentHash;
    public String nodeId;
    public Long timestamp;
    public Long expiry;
    public String description;

    @Override
    public String toString() {
        return "Invoice{" +
            "amount=" + amount +
            ", paymentHash='" + paymentHash + '\'' +
            ", nodeId='" + nodeId + '\'' +
            ", timestamp=" + timestamp +
            ", expiry=" + expiry +
            ", description='" + description + '\'' +
            ", serialized='" + serialized + '\'' +
            '}';
    }
}
