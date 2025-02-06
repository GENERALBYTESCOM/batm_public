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
 * Request to send coins.
 *
 * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-transactions#send-money">Coinbase Documentation</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoinbaseSendCoinsRequest {

    /**
     * Always "send". (required)
     */
    private final String type = "send";
    /**
     * A blockchain address, or an email of the recipient. (required)
     */
    private String to;
    /**
     * Amount to be sent. (required)
     */
    private String amount;
    /**
     * Currency of the amount. (required)
     */
    private String currency;
    /**
     * Notes to be included in the email to the recipient. (optional)
     */
    private String description;
    /**
     * [Recommended] A UUIDv4 token to ensure idempotence. (optional)
     * If a previous transaction with the same idem parameter exists for this sender,
     * that previous transaction is returned, and a new one is not created.
     * Max length is 100 characters. Must be a valid UUID and lowercased.
     */
    private String idem;

    public String getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdem() {
        return idem;
    }

    public void setIdem(String idem) {
        this.idem = idem;
    }
}
