package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by b00lean on 1.8.17.
 */

public class CBSendRequest {
    private String type = "send";
    private String to;
    private String amount;
    private String currency;
    private String description;
    private String idem;

    // Optional - Whether this send is to another financial institution or exchange.
    // Required if this send is to an address and is valued at over USD$3000.
    @JsonProperty("to_financial_institution")
    private boolean toFinancialInstitution = false;

    // XRP "destination tag"
    @JsonProperty("destination_tag")
    private String destinationTag;


    public CBSendRequest() {
    }

    public CBSendRequest(String type, String to, String amount, String currency, String description, String idem, String destinationTag) {
        this.type = type;
        this.to = to;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.idem = idem;
        this.destinationTag = destinationTag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIdem() {
        return idem;
    }

    public void setIdem(String idem) {
        this.idem = idem;
    }
}
