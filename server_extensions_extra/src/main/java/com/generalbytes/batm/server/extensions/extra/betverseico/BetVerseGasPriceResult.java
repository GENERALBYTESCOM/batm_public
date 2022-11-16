package com.generalbytes.batm.server.extensions.extra.betverseico;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BetVerseGasPriceResult {
    @JsonProperty("status")
    public String status;
    @JsonProperty("message")
    public String message;
    @JsonProperty("result")
    public BetVerseGasPrice result;
}
