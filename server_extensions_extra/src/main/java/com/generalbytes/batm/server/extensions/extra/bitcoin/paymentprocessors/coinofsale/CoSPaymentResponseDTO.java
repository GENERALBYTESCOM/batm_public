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
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.coinofsale;

import java.math.BigDecimal;

public class CoSPaymentResponseDTO {
    private String address;
    private BigDecimal bitcoin_price;
    private BigDecimal fiat_price;
    private String fiat_currency;
    private String uri;
    private boolean determined;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getBitcoin_price() {
        return bitcoin_price;
    }

    public void setBitcoin_price(BigDecimal bitcoin_price) {
        this.bitcoin_price = bitcoin_price;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isDetermined() {
        return determined;
    }

    public void setDetermined(boolean determined) {
        this.determined = determined;
    }

    public BigDecimal getFiat_price() {
        return fiat_price;
    }

    public void setFiat_price(BigDecimal fiat_price) {
        this.fiat_price = fiat_price;
    }

    public String getFiat_currency() {
        return fiat_currency;
    }

    public void setFiat_currency(String fiat_currency) {
        this.fiat_currency = fiat_currency;
    }
}

