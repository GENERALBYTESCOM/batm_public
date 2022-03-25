package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aqua.dto;

import java.math.BigDecimal;

public class QuoteResponse {
    public BigDecimal fillPrice;

    @Override
    public String toString() {
        return "QuoteResponse{" +
            "fillPrice=" + fillPrice +
            '}';
    }
    /*
    Response example:
    {"originalQuantity":"1.00000000","originalQuantityCoinSymbol":"BTC","market":{"tradeCoin":"BTC","baseCoin":"CAD","symbol":"BTC-CAD","state":null},"baseCoinFillQuantity":"63002.71231682","tradeCoinFillQuantity":"1.00000000","fillPrice":"63097.00000000","averageFillprice":"63002.71231682","side":"BUY","status":null,"statusInfo":null}
    */
}