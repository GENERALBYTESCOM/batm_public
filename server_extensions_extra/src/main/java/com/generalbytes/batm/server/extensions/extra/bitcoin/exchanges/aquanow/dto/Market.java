package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

public class Market {
    public String symbol;
    public String baseCoin;
    public String tradeCoin;

    @Override
    public String toString() {
        return symbol;
    }
}