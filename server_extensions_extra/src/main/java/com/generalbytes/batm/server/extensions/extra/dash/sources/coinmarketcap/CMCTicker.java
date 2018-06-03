package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CMCData;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CMCMetaData;
import java.math.BigDecimal;
public class CMCTicker {
    public CMCData jsondData;
    public CMCMetaData metaData;

    public String getId() {
        return jsondData.id;
    }

    public String getName() {
        return jsondData.name;
    }

    public String getSymbol() {
        return jsondData.symbol;
    }

    public String getWebsite_slug() {
        return jsondData.website_slug;
    }

    public BigDecimal getRank() {
        return jsondData.rank;
    }

    public BigDecimal getPrice() {
        return jsondData.quotes.USD.price;
    }

    public BigDecimal get_24h_volume() {
         return jsondData.quotes.USD.volume_24h;
    }

    public BigDecimal getMarket_cap() {
         return jsondData.quotes.USD.market_cap;
    }

    public BigDecimal getCirculating_supply() {
        return jsondData.circulating_supply;
    }

    public BigDecimal getTotal_supply() {
        return jsondData.total_supply;
    }

    public BigDecimal getMax_supply() {
        return jsondData.max_supply;
    }

    public BigDecimal getPercent_change_1h() {
        return jsondData.quotes.USD.percent_change_1h;
    }

    public BigDecimal getPercent_change_24h() {
        return jsondData.quotes.USD.percent_change_24h;
    }

    public BigDecimal getPercent_change_7d() {
        return jsondData.quotes.USD.percent_change_7d;
    }

    public long getLast_updated() {
        return jsondData.last_updated;
    }
}

