package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.requests;

public class NewOrderRequest extends BasicRequest {
    public int type_trade;
    public int type;
    public String rate;
    public String volume;
    public String pair;
}
