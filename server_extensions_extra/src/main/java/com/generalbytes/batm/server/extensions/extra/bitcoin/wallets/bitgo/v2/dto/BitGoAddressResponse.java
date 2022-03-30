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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BitGoAddressResponse {
    private String id;
    private String address;
    private Integer index;
    private String coin;
    private String wallet;
    private String label;
    private Balance balance;

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public Integer getIndex() {
        return index;
    }

    public String getCoin() {
        return coin;
    }

    public String getWallet() {
        return wallet;
    }

    public String getLabel() {
        return label;
    }

    public Balance getBalance() {
        return balance;
    }

    public static class Balance {
        @JsonProperty("confirmedBalanceString")
        private BigDecimal confirmedBalance;
        @JsonProperty("balanceString")
        private BigDecimal balance;

        /**
         * @return confirmed balance in the smallest unit (e.g. satoshi)
         */
        public BigDecimal getConfirmedBalance() {
            return confirmedBalance;
        }

        /**
         * @return balance in the smallest unit (e.g. satoshi)
         */
        public BigDecimal getBalance() {
            return balance;
        }
    }
}
