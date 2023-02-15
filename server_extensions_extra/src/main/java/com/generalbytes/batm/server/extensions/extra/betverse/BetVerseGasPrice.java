package com.generalbytes.batm.server.extensions.extra.betverse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BetVerseGasPrice {
    @JsonProperty("LastBlock")
    public String LastBlock;
    @JsonProperty("SafeGasPrice")
    public String SafeGasPrice;
    @JsonProperty("ProposeGasPrice")
    public String ProposeGasPrice;
    @JsonProperty("FastGasPrice")
    public String FastGasPrice;
    @JsonProperty("suggestBaseFee")
    public String suggestBaseFee;
    @JsonProperty("gasUsedRatio")
    public String gasUsedRatio;
    @JsonProperty("UsdPrice")
    public String UsdPrice;
}
