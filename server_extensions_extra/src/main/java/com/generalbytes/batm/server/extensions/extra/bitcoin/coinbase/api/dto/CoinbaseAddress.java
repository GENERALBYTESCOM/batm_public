package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Coinbase address.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseAddress {

    /**
     * ID of the address.
     */
    private String id;
    /**
     * Address for any Coinbase supported asset.
     */
    private String address;
    /**
     * User defined label for the address.
     */
    private String name;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    /**
     * Name of blockchain.
     */
    private String network;
    /**
     * Name of the resource. Constant "address" for address.
     */
    private final String resource = "address";
    @JsonProperty("resource_path")
    private String resourcePath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getResource() {
        return resource;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
