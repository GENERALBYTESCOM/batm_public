/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto;

public class Account {
    private String id_account;
    private String tx_account_id;
    private String tx_public_key;
    private String bl_selected;
    private String tx_label;
    private String bl_active;
    private String tx_account_rs;

    public String getId_account() {
        return id_account;
    }

    public void setId_account(String id_account) {
        this.id_account = id_account;
    }

    public String getTx_account_id() {
        return tx_account_id;
    }

    public void setTx_account_id(String tx_account_id) {
        this.tx_account_id = tx_account_id;
    }

    public String getTx_label() {
        return tx_label;
    }

    public void setTx_label(String tx_label) {
        this.tx_label = tx_label;
    }

    public String getBl_active() {
        return bl_active;
    }

    public void setBl_active(String bl_active) {
        this.bl_active = bl_active;
    }

    public String getBl_selected() {
        return bl_selected;
    }

    public void setBl_selected(String bl_selected) {
        this.bl_selected = bl_selected;
    }

    public String getTx_public_key() {
        return tx_public_key;
    }

    public void setTx_public_key(String tx_public_key) {
        this.tx_public_key = tx_public_key;
    }

    public String getTx_account_rs() {
        return tx_account_rs;
    }

    public void setTx_account_rs(String tx_account_rs) {
        this.tx_account_rs = tx_account_rs;
    }
}
