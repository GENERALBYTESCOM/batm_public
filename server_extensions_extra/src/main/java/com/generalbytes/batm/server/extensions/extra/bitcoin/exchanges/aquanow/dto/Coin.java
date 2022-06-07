package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

public class Coin {
    public String symbol;
    public String name;

    @Override
    public String toString() {
        return name + '(' + symbol + ')';
    }
}