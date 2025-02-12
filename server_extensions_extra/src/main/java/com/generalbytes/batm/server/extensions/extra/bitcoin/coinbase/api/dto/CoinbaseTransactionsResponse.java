package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Paginated response with multiple transactions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseTransactionsResponse {

    private CoinbasePagination pagination;
    @JsonProperty("data")
    private List<CoinbaseTransaction> transactions;

    public CoinbasePagination getPagination() {
        return pagination;
    }

    public void setPagination(CoinbasePagination pagination) {
        this.pagination = pagination;
    }

    public List<CoinbaseTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CoinbaseTransaction> transactions) {
        this.transactions = transactions;
    }
}
