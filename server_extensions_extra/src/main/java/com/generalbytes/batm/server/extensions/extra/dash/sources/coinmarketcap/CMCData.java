package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;
import java.math.BigDecimal;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CMCQuotes;
public class CMCData {
    public String id;
    public String name;
    public String symbol;
    public String website_slug;
    public BigDecimal rank;
    public BigDecimal circulating_supply;
    public BigDecimal total_supply;
    public BigDecimal max_supply;
    public CMCQuotes quotes;
    public long last_updated;
}