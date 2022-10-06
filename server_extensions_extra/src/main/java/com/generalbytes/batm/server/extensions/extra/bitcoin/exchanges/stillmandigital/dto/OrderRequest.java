package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto;

import java.math.BigDecimal;

public class OrderRequest {

    public Side side;

    public String symbol;

    public String clOrdId;

    public OrdType ordType;

    public BigDecimal orderQty;
}
