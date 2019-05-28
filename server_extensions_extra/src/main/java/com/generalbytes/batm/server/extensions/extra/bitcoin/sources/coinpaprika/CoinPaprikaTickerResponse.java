package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coinpaprika;

import java.math.BigDecimal;
import java.util.Map;

public class CoinPaprikaTickerResponse {
    public Map<String, Quote> quotes;

    public static class Quote {
        public BigDecimal price;
    }
}
