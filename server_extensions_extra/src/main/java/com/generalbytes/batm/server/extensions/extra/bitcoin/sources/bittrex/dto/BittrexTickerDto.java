package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bittrex.dto;

import java.math.BigDecimal;

public class BittrexTickerDto {
    private String symbol;
    private BigDecimal lastTradeRate;
    private BigDecimal bidRate;
    private BigDecimal askRate;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getLastTradeRate() {
        return lastTradeRate;
    }

    public void setLastTradeRate(BigDecimal lastTradeRate) {
        this.lastTradeRate = lastTradeRate;
    }

    public BigDecimal getBidRate() {
        return bidRate;
    }

    public void setBidRate(BigDecimal bidRate) {
        this.bidRate = bidRate;
    }

    public BigDecimal getAskRate() {
        return askRate;
    }

    public void setAskRate(BigDecimal askRate) {
        this.askRate = askRate;
    }
}
