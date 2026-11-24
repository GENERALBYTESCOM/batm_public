package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CryptalTransaction(String uuid, String transactionId, String status, String type, String recipient, BigDecimal amount,
                                 String uniqueId) {
}
