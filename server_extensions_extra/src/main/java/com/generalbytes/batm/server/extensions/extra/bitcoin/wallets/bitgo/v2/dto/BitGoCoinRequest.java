/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

public class BitGoCoinRequest {
    private String address;
    private String amount;
    private String walletPassphrase;
    private Integer numBlocks;
    private String comment;


    public BitGoCoinRequest(String address, String amount, String walletPassphrase, String comment, Integer numBlocks) {
        this.address = address;
        this.amount = amount;
        this.walletPassphrase = walletPassphrase;
        this.numBlocks = numBlocks;
        this.comment = comment;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWalletPassphrase() {
        return walletPassphrase;
    }

    public void setWalletPassphrase(String walletPassphrase) {
        this.walletPassphrase = walletPassphrase;
    }

    public void setNumBlocks(Integer numBlocks){
      this.numBlocks = numBlocks;
    }

    public Integer getNumBlocks() {
      return numBlocks;
    }

    public String getComment() {
      return comment;
    }

    public void setComment(String comment){
      this.comment = comment;
    }
}
