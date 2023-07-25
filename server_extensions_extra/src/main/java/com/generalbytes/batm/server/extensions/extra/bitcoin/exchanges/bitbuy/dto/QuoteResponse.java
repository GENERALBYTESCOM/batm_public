package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto;

import java.math.BigDecimal;

public class QuoteResponse {
    public String id;
    public Long createdAt;
    public Long expireAt;
    public OrderSide side;
    public String base; // crypto currency
    public BigDecimal baseQuantity;
    public String quote; // CAD
    public BigDecimal quoteQuantity;
    public BigDecimal unitPrice;
}