package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BitGoTransfer {

    @JsonProperty("txid")
    private String transactionHash;
    @JsonProperty("valueString")
    private BigDecimal value;
    @JsonProperty("confirmations")
    private int confirmations;

    public String getTransactionHash() {
        return transactionHash;
    }

    public BigDecimal getValue() {
        return value;
    }

    public int getConfirmations() {
        return confirmations;
    }

}
