package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBAccount {

    public String id;
    public String name;
    public boolean primary;
    public String type;
    public CBCurrency currency;
    public CBAmount balance;
    public CBAmount native_balance;
    public String created_at;
    public String updated_at;
    public String resource;
    public String resource_path;
}
