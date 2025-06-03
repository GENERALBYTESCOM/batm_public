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
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Transaction at Coinbase.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseTransaction {

    /**
     * ID of the transaction.
     */
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
     * Info about crypto networks including on-chain transaction hashes. Only available for certain types of transactions.
     */
    private CoinbaseNetwork network;

}
