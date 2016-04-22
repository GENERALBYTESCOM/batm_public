/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BitcoinPayPaymentResponseDTO {
    public class Data {

        @JsonProperty("address")
        String address;
        @JsonProperty("confirmations")
        int confirmations;
        @JsonProperty("create_time")
        long create_time;
        @JsonProperty("currency")
        String currency;
        @JsonProperty("paid_amount")
        BigDecimal paid_amount;
        @JsonProperty("paid_currency")
        String paid_currency;
        @JsonProperty("payment_id")
        String payment_id;
        @JsonProperty("payment_url")
        String payment_url;
        @JsonProperty("price")
        BigDecimal price;
        @JsonProperty("reference")
        String reference;
        @JsonProperty("server_time")
        long server_time;
        @JsonProperty("settled_amount")
        BigDecimal settled_amount;
        @JsonProperty("settled_currency")
        String settled_currency;
        @JsonProperty("status")
        String status;
        @JsonProperty("timeout_time")
        long timeout_time;
        @JsonProperty("txid")
        String txid;
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
