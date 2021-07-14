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

import java.util.List;

public class SentInfo {
    public String id;
    public String paymentHash;
    public String paymentType;
    public Status status;
    public Long amount;
    public Long createdAt;

    public static class Status {
        public Type type;
        public List<Failure> failures;
        public Long completedAt;
        public Long feesPaid;
        public String paymentPreimage;

        public static class Failure {
            public String failureType;
            public String failureMessage;

            @Override
            public String toString() {
                return "Failure{" +
                    "failureType='" + failureType + '\'' +
                    ", failureMessage='" + failureMessage + '\'' +
                    '}';
            }
        }

        public enum Type {
            // see CustomTypeHints at https://github.com/ACINQ/eclair/blob/master/eclair-core/src/main/scala/fr/acinq/eclair/json/JsonSerializers.scala#L353
            pending, failed, sent
        }

        @Override
        public String toString() {
            return "Status{" +
                "type=" + type +
                ", failures=" + failures +
                ", completedAt=" + completedAt +
                ", feesPaid=" + feesPaid +
                ", paymentPreimage='" + paymentPreimage + '\'' +
                '}';
        }
    }

    @Override
    public String toString() {
        return "SentInfo{" +
            "id='" + id + '\'' +
            ", paymentHash='" + paymentHash + '\'' +
            ", paymentType='" + paymentType + '\'' +
            ", status=" + status +
            ", amount=" + amount +
            ", createdAt=" + createdAt +
            '}';
    }
}
