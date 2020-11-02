package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account balance for one single currency
 **/
public class Balance   {

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("available")
    private BigDecimal available;
    @JsonProperty("frozen")
    private BigDecimal frozen;
    @JsonProperty("total")
    private BigDecimal total;

    public BigDecimal getAvailable() {
        return this.available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getFrozen() {
        return this.frozen;
    }

    public void setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override public String toString() {
        return "Balance{" +
            "currency='" + currency + '\'' +
            ", available=" + available +
            ", frozen=" + available +
            ", total=" + total +
            '}';
    }
}

