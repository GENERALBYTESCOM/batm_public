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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

public class ReceivedInfo {
    public String paymentHash;
    public String paymentPreimage;
    public String paymentType;
    public Status status;

    public static class Status {

        public Long amount;
        public Long receivedAt;
        public Type type;

        public enum Type {
            // see CustomTypeHints at https://github.com/ACINQ/eclair/blob/master/eclair-core/src/main/scala/fr/acinq/eclair/json/JsonSerializers.scala#L353
            pending, received, expired;
        }

        @Override
        public String toString() {
            return "Status{" +
                "amount=" + amount +
                ", receivedAt=" + receivedAt +
                ", type=" + type +
                '}';
        }
    }

    @Override
    public String toString() {
        return "ReceivedInfo{" +
            "status=" + status +
            ", paymentHash='" + paymentHash + '\'' +
            ", paymentPreimage='" + paymentPreimage + '\'' +
            ", paymentType='" + paymentType + '\'' +
            '}';
    }
}
