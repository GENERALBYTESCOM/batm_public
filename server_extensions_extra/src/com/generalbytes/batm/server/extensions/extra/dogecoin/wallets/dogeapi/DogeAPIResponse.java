/*************************************************************************************
 * Copyright (C) 2014 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.dogeapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class DogeAPIResponse {
    private Data data;

    public class Data {
        private String[] addresses;
        private BigDecimal balance;
        private String txid;
        private Info info;


        public class Info {
            private BigDecimal difficulty;
            private BigDecimal network_hashrate;
            private BigDecimal doge_usd;
            private BigDecimal doge_btc;
            private BigDecimal five_min_btc_change;
            private BigDecimal five_min_usd_change;
            private int api_version;

            public BigDecimal getDifficulty() {
                return difficulty;
            }

            public void setDifficulty(BigDecimal difficulty) {
                this.difficulty = difficulty;
            }

            public BigDecimal getNetwork_hashrate() {
                return network_hashrate;
            }

            public void setNetwork_hashrate(BigDecimal network_hashrate) {
                this.network_hashrate = network_hashrate;
            }

            public BigDecimal getDoge_usd() {
                return doge_usd;
            }

            public void setDoge_usd(BigDecimal doge_usd) {
                this.doge_usd = doge_usd;
            }

            public BigDecimal getDoge_btc() {
                return doge_btc;
            }

            public void setDoge_btc(BigDecimal doge_btc) {
                this.doge_btc = doge_btc;
            }

            @JsonProperty("5min_btc_change")
            public BigDecimal getFive_min_btc_change() {
                return five_min_btc_change;
            }

            @JsonProperty("5min_btc_change")
            public void setFive_min_btc_change(BigDecimal five_min_btc_change) {
                this.five_min_btc_change = five_min_btc_change;
            }

            @JsonProperty("5min_usd_change")
            public BigDecimal getFive_min_usd_change() {
                return five_min_usd_change;
            }

            @JsonProperty("5min_usd_change")
            public void setFive_min_usd_change(BigDecimal five_min_usd_change) {
                this.five_min_usd_change = five_min_usd_change;
            }

            public int getApi_version() {
                return api_version;
            }

            public void setApi_version(int api_version) {
                this.api_version = api_version;
            }
        }

        public String[] getAddresses() {
            return addresses;
        }

        public void setAddresses(String[] addresses) {
            this.addresses = addresses;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


}
