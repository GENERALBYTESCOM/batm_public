/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.nano.wallet.paper;

import com.generalbytes.batm.server.extensions.IPaperWallet;

public class NanoPaperWallet implements IPaperWallet {

    private final String crypto, address, privateKey, message;
    private final byte[] content;

    public NanoPaperWallet(String crypto, String address, String privateKey, String message, byte[] zipContent) {
        this.crypto = crypto;
        this.address = address;
        this.privateKey = privateKey;
        this.message = message;
        this.content = zipContent;
    }


    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getPrivateKey() {
        return privateKey;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCryptoCurrency() {
        return crypto;
    }

    @Override
    public String getFileExtension() {
        return "zip";
    }

    @Override
    public String getContentType() {
        return "application/zip";
    }

    @Override
    public byte[] getContent() {
        return content;
    }

}
