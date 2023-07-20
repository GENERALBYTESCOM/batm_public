package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto;

import java.util.List;

public class OrderBook {
    public String marketSymbol;
    public List<OrderBookLevel> asks;
    public List<OrderBookLevel> bids;
}