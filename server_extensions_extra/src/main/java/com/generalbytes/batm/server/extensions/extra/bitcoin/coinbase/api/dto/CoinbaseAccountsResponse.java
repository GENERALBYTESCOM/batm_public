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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Paginated response with multiple Coinbase accounts.
 */
public class CoinbaseAccountsResponse {

    private CoinbasePagination pagination;
    @JsonProperty("data")
    private List<CoinbaseAccount> accounts;

    public CoinbasePagination getPagination() {
        return pagination;
    }

    public void setPagination(CoinbasePagination pagination) {
        this.pagination = pagination;
    }

    public List<CoinbaseAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<CoinbaseAccount> accounts) {
        this.accounts = accounts;
    }
}
