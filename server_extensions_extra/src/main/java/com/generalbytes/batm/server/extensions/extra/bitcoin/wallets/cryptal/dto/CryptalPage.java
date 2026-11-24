package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CryptalPage<T>(List<T> content, long count) {
}
