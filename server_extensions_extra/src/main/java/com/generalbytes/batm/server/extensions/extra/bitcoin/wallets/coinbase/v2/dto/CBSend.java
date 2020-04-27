/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

/**
 * Created by b00lean on 30.7.17.
 */

public class CBSend {
    private String id;
    private String type;
    private String status;
    private CBBalance amount;
    private CBBalance native_amount;
    private String description;
    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;
    private CBNetwork network;
    private CBTo to;
    private CBDetails details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CBBalance getAmount() {
        return amount;
    }

    public void setAmount(CBBalance amount) {
        this.amount = amount;
    }

    public CBBalance getNative_amount() {
        return native_amount;
    }

    public void setNative_amount(CBBalance native_amount) {
        this.native_amount = native_amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResource_path() {
        return resource_path;
    }

    public void setResource_path(String resource_path) {
        this.resource_path = resource_path;
    }

    public CBNetwork getNetwork() {
        return network;
    }

    public void setNetwork(CBNetwork network) {
        this.network = network;
    }

    public CBTo getTo() {
        return to;
    }

    public void setTo(CBTo to) {
        this.to = to;
    }

    public CBDetails getDetails() {
        return details;
    }

    public void setDetails(CBDetails details) {
        this.details = details;
    }
}
