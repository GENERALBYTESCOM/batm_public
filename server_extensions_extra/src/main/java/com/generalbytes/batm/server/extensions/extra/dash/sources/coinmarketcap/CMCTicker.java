package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
public class CMCMetaData {
    private long timestamp;
    private String error;
}
public class CMCQuote {
    private BigDecimal price;
    private BigDecimal volume_24h;
    private BigDecimal market_cap;
    private BigDecimal percent_change_1h;
    private BigDecimal percent_change_24h;
    private BigDecimal percent_change_7d;
}
public class CMCQuotes {
    private CMCQuote USD;
}
public class CMCData {
    private String id;
    private String name;
    private String symbol;
    private String website_slug;
    private BigDecimal rank;
    private BigDecimal circulating_supply;
    private BigDecimal total_supply;
    private BigDecimal max_supply;
    private CMCQuotes quotes;
    private long last_updated;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getWebsite_slug() {
        return website_slug;
    }

    public BigDecimal getRank() {
        return rank;
    }

    public BigDecimal getPrice() {
        return quotes.USD.price;
    }

    public BigDecimal get_24h_volume() {
         return quotes.USD.volume_24h;
    }

    public BigDecimal getMarket_cap() {
         return quotes.USD.market_cap;
    }

    public BigDecimal getCirculating_supply() {
        return circulating_supply;
    }

    public BigDecimal getTotal_supply() {
        return total_supply;
    }

    public BigDecimal getMax_supply() {
        return max_supply;
    }

    public BigDecimal getPercent_change_1h() {
        return quotes.USD.percent_change_1h;
    }

    public BigDecimal getPercent_change_24h() {
        return quotes.USD.percent_change_24h;
    }

    public BigDecimal getPercent_change_7d() {
        return quotes.USD.percent_change_7d;
    }

    public long getLast_updated() {
        return last_updated;
    }
}
public class CMCTicker {
    private CMCData jsondData;
    private CMCMetaData metaData;
}

