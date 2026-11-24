package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CryptalSendRequest(String currency,
                                 String network,
                                 String address,
                                 @JsonRawValue String amount,
                                 boolean includeFee,
                                 String uniqueId) {
}
