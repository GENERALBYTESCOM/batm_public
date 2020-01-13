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
package com.generalbytes.batm.server.extensions.extra.bitcoinsv;

import com.generalbytes.batm.server.extensions.IPaperWallet;

public class BitcoinSVPaperWallet implements IPaperWallet{
    private String cryptoCurrency;
    private byte[] content;
    private String address;
    private String privateKey;
    private String message;
    private String contentType;
    private String fileExtension;

    public BitcoinSVPaperWallet(String cryptoCurrency, byte[] content, String address, String privateKey, String message, String contentType, String fileExtension) {
        this.cryptoCurrency = cryptoCurrency;
        this.content = content;
        this.address = address;
        this.privateKey = privateKey;
        this.message = message;
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public byte[] getContent() {
        return content;
    }

    public String getAddress() {
        return address;
    }

    public String getMessage() {
        return message;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    @Override
    public String getCryptoCurrency() {
        return cryptoCurrency;
    }
}
