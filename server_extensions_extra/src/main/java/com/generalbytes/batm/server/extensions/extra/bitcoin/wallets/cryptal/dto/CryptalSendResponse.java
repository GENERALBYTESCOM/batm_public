package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CryptalSendResponse(String uuid, String uniqueId, String txHash) {
}
