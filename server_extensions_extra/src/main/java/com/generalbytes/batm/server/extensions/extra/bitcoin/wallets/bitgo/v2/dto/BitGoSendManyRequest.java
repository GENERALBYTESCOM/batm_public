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

import java.util.List;

public class BitGoSendManyRequest {
    public List<BitGoRecipient> recipients;
    public String walletPassphrase;
    public Integer numBlocks;
    public String comment;
    public String type;

    public BitGoSendManyRequest(List<BitGoRecipient> recipients, String walletPassphrase, String comment){
      this(recipients, walletPassphrase, comment, 2);
    }

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

    public static class BitGoRecipient {
        public String address;
        public String amount; // in satoshis

        public BitGoRecipient(String address, String amount) {
            this.address = address;
            this.amount = amount;
        }
    }
}
