/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Request to get the price of a cryptocurrency in a specific fiat currency.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoinbasePriceRequest {

    private String cryptocurrency;
    private String fiatCurrency;
    private CoinbasePriceType priceType;

    public String getCryptocurrency() {
        return cryptocurrency;
    }

    /**
     * @param cryptocurrency The cryptocurrency to get the price of.
     */
    public void setCryptocurrency(String cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }

    public String getFiatCurrency() {
        return fiatCurrency;
    }

    /**
     * @param fiatCurrency The fiat currency to get the price in.
     */
    public void setFiatCurrency(String fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }

    public CoinbasePriceType getPriceType() {
        return priceType;
    }

    /**
     * @param priceType The type of price to get.
     * @see CoinbasePriceType
     */
    public void setPriceType(CoinbasePriceType priceType) {
        this.priceType = priceType;
    }
}
