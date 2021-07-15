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
package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto;

public class BlockIOResponseBalance {
    private String status;
    private BlockIOData data;

    public BlockIOResponseBalance() {
    }

    public class BlockIOData {
        private String network;
        private String available_balance;
        private String unconfirmed_sent_balance;
        private String unconfirmed_received_balance;
        private String pending_received_balance;


        public BlockIOData() {
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getAvailable_balance() {
            return available_balance;
        }

        public void setAvailable_balance(String available_balance) {
            this.available_balance = available_balance;
        }

        public String getUnconfirmed_sent_balance() {
            return unconfirmed_sent_balance;
        }

        public void setUnconfirmed_sent_balance(String unconfirmed_sent_balance) {
            this.unconfirmed_sent_balance = unconfirmed_sent_balance;
        }

        public String getUnconfirmed_received_balance() {
            return unconfirmed_received_balance;
        }

        public void setUnconfirmed_received_balance(String unconfirmed_received_balance) {
            this.unconfirmed_received_balance = unconfirmed_received_balance;
        }

        public String getPending_received_balance() {
            return pending_received_balance;
        }

        public void setPending_received_balance(String pending_received_balance) {
            this.pending_received_balance = pending_received_balance;
        }
    }



    public String getStatus() {
        return status;
    }

    public BlockIOData getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(BlockIOData data) {
        this.data = data;
    }
}
