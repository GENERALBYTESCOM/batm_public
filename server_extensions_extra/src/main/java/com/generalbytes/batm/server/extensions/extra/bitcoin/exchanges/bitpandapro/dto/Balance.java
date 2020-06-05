package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account balance for one single currency
 **/
public class Balance   {

    @JsonProperty("account_id")
    private UUID accountId;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("available")
    private BigDecimal available;
    @JsonProperty("locked")
    private BigDecimal locked;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getLocked() {
        return locked;
    }

    public void setLocked(BigDecimal locked) {
        this.locked = locked;
    }

    @Override public String toString() {
        return "Balance{" +
            "accountId=" + accountId +
            ", currencyCode='" + currencyCode + '\'' +
            ", available=" + available +
            ", locked=" + locked +
            '}';
    }
}

