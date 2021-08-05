package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An existing or new deposit address for cryptocurrencies.
 */
public class DepositAddress {
    @JsonProperty("address")
    private String address;
    @JsonProperty("destination_tag")
    private String destinationTag;
    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("is_smart_contract")
    private Boolean isSmartContract;
    @JsonProperty("can_create_more")
    private Boolean canCreateMore;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDestinationTag() {
        return destinationTag;
    }

    public void setDestinationTag(String destinationTag) {
        this.destinationTag = destinationTag;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getSmartContract() {
        return isSmartContract;
    }

    public void setSmartContract(Boolean smartContract) {
        isSmartContract = smartContract;
    }

    public Boolean getCanCreateMore() {
        return canCreateMore;
    }

    public void setCanCreateMore(Boolean canCreateMore) {
        this.canCreateMore = canCreateMore;
    }

    @Override public String toString() {
        return "DepositAddress{" +
            "address='" + address + '\'' +
            ", destinationTag='" + destinationTag + '\'' +
            ", enabled=" + enabled +
            ", isSmartContract=" + isSmartContract +
            ", canCreateMore=" + canCreateMore +
            '}';
    }
}
