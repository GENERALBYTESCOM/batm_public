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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds information about a Coinbase account.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseAccount {

    private String id;
    private String name;
    private String type;
    private CoinbaseCurrency currency;
    private CoinbaseAmount balance;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    private boolean primary;
    /**
     * Name of the resource. Constant "account" for accounts.
     */
    private final String resource = "account";
    @JsonProperty("resource_path")
    private String resourcePath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the cryptocurrency that this account holds.
     *
     * @return The cryptocurrency.
     */
    public CoinbaseCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(CoinbaseCurrency currency) {
        this.currency = currency;
    }

    /**
     * Get the balance of this account. This is the amount of cryptocurrency it has.
     *
     * @return The balance.
     */
    public CoinbaseAmount getBalance() {
        return balance;
    }

    public void setBalance(CoinbaseAmount balance) {
        this.balance = balance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return true if this is the primary account for the cryptocurrency it holds, false otherwise.
     */
    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getResource() {
        return resource;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
