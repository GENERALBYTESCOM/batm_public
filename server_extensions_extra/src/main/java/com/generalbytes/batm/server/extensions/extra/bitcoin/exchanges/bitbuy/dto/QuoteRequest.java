package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto;

import java.math.BigDecimal;

public class QuoteRequest {
    public CurrencySide currencySide;
    public String quantity;
    public OrderSide side;
    public String quote; // CAD
    public String base; // crypto

    public QuoteRequest() {
    }

    public QuoteRequest(OrderSide orderSide, String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        this.currencySide = CurrencySide.BASE;
        this.quantity = cryptoAmount.toPlainString();
        this.side = orderSide;
        this.quote = fiatCurrency;
        this.base = cryptoCurrency;
    }
}