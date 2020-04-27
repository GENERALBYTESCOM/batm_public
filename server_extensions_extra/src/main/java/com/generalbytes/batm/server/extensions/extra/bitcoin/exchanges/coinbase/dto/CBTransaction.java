package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBTransaction {

    public String id;
    public String type;
    public String status;
    public CBAmount amount;
    public CBAmount native_amount;
    public String description;
    public String created_at;
    public String updated_at;
    public String resource;
    public String resource_path;
    public CBNetwork network;
    public CBTo to;
    public CBDetails details;

    public static class CBNetwork {
        public String status;
        public String hash;
        public String name;
    }

    public static class CBTo {
        public String resource;
        public String address;
    }

    public static class CBDetails {
        public String title;
        public String subtitle;
    }
}
