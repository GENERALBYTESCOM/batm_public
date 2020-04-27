package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBTimeResponse extends CBResponse {

    public CBTime data;

    public static class CBTime {
        public String iso;
        public long epoch;
    }
}
