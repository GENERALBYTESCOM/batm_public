package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coinpaprika;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CoinPaprikaHistoricalTickerResponse {
    public String timestamp;

    public BigDecimal price;

    @JsonProperty("volume_24h")
    public long volume24h;

    @JsonProperty("market_cap")
    public long marketCap;
}
