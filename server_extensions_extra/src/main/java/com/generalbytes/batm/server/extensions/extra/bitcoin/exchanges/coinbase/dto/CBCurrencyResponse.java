package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBCurrencyResponse extends CBResponse {

    public CBCurrency[] data;

    public static class CBCurrency {
        public String id;
        public String name;
        public String min_size;
    }
}
