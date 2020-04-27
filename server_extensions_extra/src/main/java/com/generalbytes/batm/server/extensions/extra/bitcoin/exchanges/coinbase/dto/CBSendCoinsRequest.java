package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBSendCoinsRequest {

    public String type = "send";
    public String to;
    public String amount;
    public String currency;
    public String description;
    public String skip_notifications;
    public String fee;
    public String idem;
    public String to_financial_institution;
    public String financial_institution_website;
}
