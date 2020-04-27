package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBNewAddressResponse extends CBResponse {

    public CBAddress data;

    public static class CBAddress {
        public String id;
        public String address;
        public String name;
        public String created_at;
        public String updated_at;
        public String network;
        public String resource;
        public String resource_path;
    }
}
