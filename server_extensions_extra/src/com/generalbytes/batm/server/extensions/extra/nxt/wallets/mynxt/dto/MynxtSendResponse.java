/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto;

public class MynxtSendResponse extends MynxtResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String fullHash;
        private String signatureHash;
        private String transactionBytes;
        private String transaction;
        private boolean broadcasted;

        public String getFullHash() {
            return fullHash;
        }

        public void setFullHash(String fullHash) {
            this.fullHash = fullHash;
        }

        public String getSignatureHash() {
            return signatureHash;
        }

        public void setSignatureHash(String signatureHash) {
            this.signatureHash = signatureHash;
        }

        public String getTransactionBytes() {
            return transactionBytes;
        }

        public void setTransactionBytes(String transactionBytes) {
            this.transactionBytes = transactionBytes;
        }

        public String getTransaction() {
            return transaction;
        }

        public void setTransaction(String transaction) {
            this.transaction = transaction;
        }

        public boolean isBroadcasted() {
            return broadcasted;
        }

        public void setBroadcasted(boolean broadcasted) {
            this.broadcasted = broadcasted;
        }
    }
}
