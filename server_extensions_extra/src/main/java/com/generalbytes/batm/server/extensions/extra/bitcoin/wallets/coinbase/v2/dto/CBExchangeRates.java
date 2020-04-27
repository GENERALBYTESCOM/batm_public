package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

import java.math.BigDecimal;
import java.util.Map;

public class CBExchangeRates {
    private String currency;
    private Map<String, BigDecimal> rates;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
