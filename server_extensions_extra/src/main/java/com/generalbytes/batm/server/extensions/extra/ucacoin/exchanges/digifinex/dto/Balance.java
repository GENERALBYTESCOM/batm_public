package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account balance for one single currency
 **/
public class Balance   {

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("free")
    private BigDecimal free;
    @JsonProperty("total")
    private BigDecimal total;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override public String toString() {
        return "Balance{" +
            ", currency='" + currency + '\'' +
            ", free=" + free +
            ", total=" + total +
            '}';
    }
}

