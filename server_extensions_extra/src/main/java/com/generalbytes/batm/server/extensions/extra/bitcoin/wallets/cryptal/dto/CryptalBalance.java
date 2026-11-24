package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CryptalBalance(String currency, BigDecimal balance, BigDecimal autoFill, BigDecimal total) {
}
