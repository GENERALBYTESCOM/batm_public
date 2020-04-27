package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

import java.math.BigDecimal;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBBalance {
    private BigDecimal amount;
    private String currency;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
