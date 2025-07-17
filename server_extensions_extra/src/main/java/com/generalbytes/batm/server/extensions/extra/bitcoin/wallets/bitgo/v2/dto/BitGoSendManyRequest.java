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

import lombok.Getter;

import java.util.List;

@Getter
public class BitGoSendManyRequest {
    private final List<BitGoRecipient> recipients;
    private final String walletPassphrase;
    private final Integer numBlocks;
    private final String comment;
    private String type;

    public BitGoSendManyRequest(List<BitGoRecipient> recipients, String walletPassphrase, String comment, Integer numBlocks) {
        this.recipients = recipients;
        this.walletPassphrase = walletPassphrase;
        this.numBlocks = numBlocks;
        this.comment = comment;
    }

    public BitGoSendManyRequest(List<BitGoRecipient> recipients, String walletPassphrase, String comment, Integer numBlocks, String type) {
        this(recipients, walletPassphrase, comment, numBlocks);

        this.type = type;
    }

    public record BitGoRecipient(String address, String amount) {

    }
}
