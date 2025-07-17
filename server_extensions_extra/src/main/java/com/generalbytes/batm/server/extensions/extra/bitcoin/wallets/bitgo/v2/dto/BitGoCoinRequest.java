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
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
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
}
