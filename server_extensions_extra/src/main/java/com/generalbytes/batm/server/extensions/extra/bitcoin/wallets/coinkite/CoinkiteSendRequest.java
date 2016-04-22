/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinkite;


public class CoinkiteSendRequest {
    private String memo;
    private String amount;
    private String account;
    private String dest;
    private String public_msg;

    public CoinkiteSendRequest() {
    }

    public CoinkiteSendRequest(String memo, String amount, String accountId, String dest, String public_msg) {
        this.memo = memo;
        this.amount = amount;
        this.account = accountId;
        this.dest = dest;
        this.public_msg = public_msg;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getPublic_msg() {
        return public_msg;
    }

    public void setPublic_msg(String public_msg) {
        this.public_msg = public_msg;
    }
}
