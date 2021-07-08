package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto;

import java.math.BigDecimal;

/**
 * Request DTO matching only user fields from https://bittrex.github.io/api/v3#/definitions/NewWithdrawal
 */
//TODO: BATM-2327 remove this class
public class BittrexNewWithdrawal {

    /**
     * unique symbol of the currency to withdraw from (required)
     */
    private String currencySymbol;

    /**
     * quantity to withdraw (required)
     */
    private BigDecimal quantity;

    /**
     * crypto address to withdraw funds to
     */
    private String cryptoAddress;

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getCryptoAddress() {
        return cryptoAddress;
    }

    public void setCryptoAddress(String cryptoAddress) {
        this.cryptoAddress = cryptoAddress;
    }
}
