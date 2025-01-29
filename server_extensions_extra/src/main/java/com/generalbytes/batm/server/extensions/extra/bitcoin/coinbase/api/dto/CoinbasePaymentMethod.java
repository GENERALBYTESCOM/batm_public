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
 * Represents a single payment method.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbasePaymentMethod {

    private String id;
    private String type;
    private String name;
    private String currency;
    private boolean verified;
    @JsonProperty("allow_buy")
    private boolean allowBuy;
    @JsonProperty("allow_sell")
    private boolean allowSell;
    @JsonProperty("allow_deposit")
    private boolean allowDeposit;
    @JsonProperty("allow_withdraw")
    private boolean allowWithdraw;

    /**
     * @return The payment method's unique identifier.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The payment method type.
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The payment method name.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The currency symbol.
     */
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return {@code true} if the payment method is verified, otherwise {@code false}.
     */
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * @return {@code true} if buys are allowed, otherwise {@code false}.
     */
    public boolean isAllowBuy() {
        return allowBuy;
    }

    public void setAllowBuy(boolean allowBuy) {
        this.allowBuy = allowBuy;
    }

    /**
     * @return {@code true} if sells are allowed, otherwise {@code false}.
     */
    public boolean isAllowSell() {
        return allowSell;
    }

    public void setAllowSell(boolean allowSell) {
        this.allowSell = allowSell;
    }

    /**
     * @return {@code true} if deposits are allowed, otherwise {@code false}.
     */
    public boolean isAllowDeposit() {
        return allowDeposit;
    }

    public void setAllowDeposit(boolean allowDeposit) {
        this.allowDeposit = allowDeposit;
    }

    /**
     * @return {@code true} if withdrawals are allowed, otherwise {@code false}.
     */
    public boolean isAllowWithdraw() {
        return allowWithdraw;
    }

    public void setAllowWithdraw(boolean allowWithdraw) {
        this.allowWithdraw = allowWithdraw;
    }
}
