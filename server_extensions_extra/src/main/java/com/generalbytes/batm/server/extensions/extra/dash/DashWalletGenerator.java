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
package com.generalbytes.batm.server.extensions.extra.dash;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;

import com.generalbytes.bitrafael.tools.wallet.dash.WalletToolsDASH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DashWalletGenerator implements IPaperWalletGenerator {

    private static final Logger log = LoggerFactory.getLogger("batm.master.DashWalletGenerator");
    private String prefix;
    private IExtensionContext ctx;

    public DashWalletGenerator(String prefix, IExtensionContext ctx) {
        this.prefix = prefix;
        this.ctx = ctx;
    }

    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String oneTimePassword, String userLanguage, boolean shouldBeVanity) {
        WalletToolsDASH wt = new WalletToolsDASH();
        String privateKey = wt.generateWalletPrivateKeyWithPrefix(shouldBeVanity ? prefix : null, CryptoCurrency.DASH.getCode());
        String address = wt.getWalletAddressFromPrivateKey(privateKey, CryptoCurrency.DASH.getCode());

        byte[] content = ctx.createPaperWallet7ZIP(privateKey, address, oneTimePassword, cryptoCurrency);

        //send wallet to customer
        String messageText = "New wallet " + address + " use your onetime password to open the attachment.";
        String messageTextLang = readTemplate("/batm/config/template_wallet_" + userLanguage + ".txt");
        if (messageTextLang != null) {
            messageText = messageTextLang;
        }else{
            String messageTextEN = readTemplate("/batm/config/template_wallet_en.txt");
            if (messageTextEN != null) {
                messageText = messageTextEN;
            }
        }

        return new DashPaperWallet(cryptoCurrency, content, address, privateKey, messageText,"application/zip", "zip");
    }

    @SuppressWarnings("all")
    private String readTemplate(String templateFile) {
        File f = new File(templateFile);
        if (f.exists() && f.canRead()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(templateFile)),"UTF-8");
                return content;
            } catch (IOException e) {
                log.error("readTemplate", e);
            }
        }
        return null;
    }
}
