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
 * Represents a Transaction at Coinbase.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseTransaction {

    private String id;
    private String type;
    private String status;
    /**
     * Amount of any supported digital asset.
     * Value is negative to indicate the debiting of funds for the following transaction type cases:
     * - advanced_trade_fill, sell
     * - pro_deposit
     */
    private CoinbaseTransactionAmount amount;
    /**
     * Amount in user's native currency.
     * Value is negative to indicate the debiting of funds for the following transaction type cases:
     * - advanced_trade_fill, sell
     * - pro_deposit
     */
    @JsonProperty("native_amount")
    private CoinbaseTransactionAmount nativeAmount;
    /**
     * User defined description
     */
    private String description;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    /**
     * Name of the resource. Constant "transaction" for transaction.
     */
    private final String resource = "transaction";
    @JsonProperty("resource_path")
    private String resourcePath;

    /**
     * @return ID of the transaction.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CoinbaseTransactionAmount getAmount() {
        return amount;
    }

    public void setAmount(CoinbaseTransactionAmount amount) {
        this.amount = amount;
    }

    public CoinbaseTransactionAmount getNativeAmount() {
        return nativeAmount;
    }

    public void setNativeAmount(CoinbaseTransactionAmount nativeAmount) {
        this.nativeAmount = nativeAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
