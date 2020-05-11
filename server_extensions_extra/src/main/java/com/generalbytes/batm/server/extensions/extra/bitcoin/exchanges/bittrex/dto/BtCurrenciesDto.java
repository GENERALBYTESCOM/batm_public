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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BtCurrenciesDto {

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("CurrencyLong")
    private String currencyLong;

    @JsonProperty("MinConfirmation")
    private Integer minConfirmation;

    @JsonProperty("TxFee")
    private Double txFee;

    @JsonProperty("IsActive")
    private Boolean isActive;

    @JsonProperty("CoinType")
    private String coinType;

    @JsonProperty("BaseAddress")
    private String baseAddress;

    @JsonProperty("Notice")
    private String notice;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyLong() {
        return currencyLong;
    }

    public void setCurrencyLong(String currencyLong) {
        this.currencyLong = currencyLong;
    }

    public Integer getMinConfirmation() {
        return minConfirmation;
    }

    public void setMinConfirmation(Integer minConfirmation) {
        this.minConfirmation = minConfirmation;
    }

    public Double getTxFee() {
        return txFee;
    }

    public void setTxFee(Double txFee) {
        this.txFee = txFee;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
