package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class BitGoTransfer {
    @JsonProperty("txid")
    private String transactionHash;
    @JsonProperty("valueString")
    private BigDecimal value;
    @JsonProperty("confirmations")
    private int confirmations;
}
