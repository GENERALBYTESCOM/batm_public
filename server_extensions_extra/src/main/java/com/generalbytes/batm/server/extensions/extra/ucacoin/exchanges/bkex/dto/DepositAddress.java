package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/*{
      "currency": "BTC",
      "address": "1m4k2yUKTSrX6SM9FGgvwMybAbYtRVi2N",
      "memo": ""
    }
*/
/**
 * An existing or new deposit address for crypto currencies.
 */
public class DepositAddress {
    @JsonProperty("address")
    private String address;
    @JsonProperty("memo")
    private String memo;
    @JsonProperty("currency")
    private String currency;


    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override public String toString() {
        return "DepositAddress{" +
            "address='" + address + '\'' +
            ", memo='" + memo + '\'' +
            ", currency=" + currency +
            '}';
    }
}
