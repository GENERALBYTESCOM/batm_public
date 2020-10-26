package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/*{
    "code": 200,
    "data": [
        {
            "currency": "btc",
            "address": "1PSRjPg53cX7hMRYAXGJnL8mqHtzmQgPUs",
            "addressTag": "",
            "chain": ""
        }
    ]
}
*/
/**
 * An existing or new deposit address for crypto currencies.
 */
public class DepositAddress {
    @JsonProperty("address")
    private String address;
    @JsonProperty("addressTag")
    private String addressTag;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("chain")
    private String chain;

    public String getAddressTag() {
        return this.addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChain() {
        return this.chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
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
            ", adressTag='" + addressTag + '\'' +
            ", currency=" + currency +
            ", chain=" + chain +
            '}';
    }
}
