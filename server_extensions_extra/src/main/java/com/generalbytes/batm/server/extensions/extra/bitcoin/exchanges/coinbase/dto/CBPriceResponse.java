package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBPriceResponse extends CBResponse {

    public CBPrice data;

    public static class CBPrice {
        public String amount;
        public String currency;
    }
}
