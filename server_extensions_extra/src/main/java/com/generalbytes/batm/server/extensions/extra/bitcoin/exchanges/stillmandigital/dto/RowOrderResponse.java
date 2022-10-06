package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto;


import java.math.BigDecimal;

public class RowOrderResponse {

    public long id;
    public String clOrdId;
    public String origClOrdId;
    public String symbol;
    public Side side;
    public BigDecimal orderQty;
    public OrdType ordType;
    public BigDecimal price;
    public OrderStatus ordStatus;
    public BigDecimal leavesQty;
    public BigDecimal cumQty;
    public BigDecimal avgPx;
}
