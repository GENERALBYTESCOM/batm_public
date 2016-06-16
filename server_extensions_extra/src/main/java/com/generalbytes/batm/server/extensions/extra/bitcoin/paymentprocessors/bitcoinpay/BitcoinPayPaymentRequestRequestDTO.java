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

public class BitcoinPayPaymentRequestRequestDTO {
    @JsonProperty("currency")
    String currency;
    @JsonProperty("price")
    BigDecimal price;

    @JsonProperty("settled_currency")
    String settled_currency;

    @JsonProperty("reference")
    String reference;

    public BitcoinPayPaymentRequestRequestDTO(String currency, BigDecimal price, String settled_currency, String reference) {
        this.currency = currency;
        this.price = price;
        this.settled_currency = settled_currency;
        this.reference = reference;
    }
}
