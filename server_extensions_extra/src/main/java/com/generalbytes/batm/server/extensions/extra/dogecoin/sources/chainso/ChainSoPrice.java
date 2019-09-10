package com.generalbytes.batm.server.extensions.extra.dogecoin.sources.chainso;

/**
 * Created by b00lean on 8/11/14.
 */
public class ChainSoPrice {
    private String price;
    private String price_base;
    private String exchange;
    private long time;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_base() {
        return price_base;
    }

    public void setPrice_base(String price_base) {
        this.price_base = price_base;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
