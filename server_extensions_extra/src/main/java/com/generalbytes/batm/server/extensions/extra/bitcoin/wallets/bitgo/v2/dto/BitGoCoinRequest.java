/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BitGoCoinRequest {
    private String address;
    private String amount;
    private String walletPassphrase;
    private Integer numBlocks;
    private String comment;
    private Integer feeRate;
    private Integer maxFeeRate;
    private String type;


    public BitGoCoinRequest(String address, String amount, String walletPassphrase, String comment, Integer numBlocks) {
        this.address = address;
        this.amount = amount;
        this.walletPassphrase = walletPassphrase;
        this.numBlocks = numBlocks;
        this.comment = comment;
    }

    public BitGoCoinRequest(String address,
                            String amount,
                            String walletPassphrase,
                            String comment,
                            Integer numBlocks,
                            Integer feeRate,
                            Integer maxFeeRate
    ) {
        this(address, amount, walletPassphrase, comment, numBlocks);

        this.feeRate = feeRate;
        this.maxFeeRate = maxFeeRate;
    }

    public BitGoCoinRequest(String address,
                            String amount,
                            String walletPassphrase,
                            String comment,
                            Integer numBlocks,
                            Integer feeRate,
                            Integer maxFeeRate,
                            String type
    ) {
        this(address, amount, walletPassphrase, comment, numBlocks, feeRate, maxFeeRate);

        this.type = type;
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

    public Integer getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(Integer feeRate) {
        this.feeRate = feeRate;
    }

    public Integer getMaxFeeRate() {
        return maxFeeRate;
    }

    public void setMaxFeeRate(Integer maxFeeRate) {
        this.maxFeeRate = maxFeeRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
