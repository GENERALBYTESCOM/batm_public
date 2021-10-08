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
package com.generalbytes.batm.server.extensions.extra.ripple;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.keypairs.DefaultKeyPairService;
import org.xrpl.xrpl4j.keypairs.KeyPairService;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.WalletFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RippleWalletGenerator implements IPaperWalletGenerator {

    private static final Logger log = LoggerFactory.getLogger("batm.master.RippleWalletGenerator");
    private final IExtensionContext ctx;
    private final KeyPairService keyPairService = DefaultKeyPairService.getInstance();
    private final WalletFactory walletFactory = DefaultWalletFactory.getInstance();

    public RippleWalletGenerator(IExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String oneTimePassword, String userLanguage, boolean shouldBeVanity) {
        String seed = keyPairService.generateSeed(); // seed is starting with "s"; private key could be derived from the seed but other paper wallet generators also use the seed
        String address = walletFactory.fromSeed(seed, false).classicAddress().value(); // address starting with "r"

        byte[] content = ctx.createPaperWallet7ZIP(seed, address, oneTimePassword, cryptoCurrency);

        //send wallet to customer
        String messageText = "New wallet " + address + " use your onetime password to open the attachment.";
        String messageTextLang = readTemplate("/batm/config/template_wallet_" + userLanguage + ".txt");
        if (messageTextLang != null) {
            messageText = messageTextLang;
        } else {
            String messageTextEN = readTemplate("/batm/config/template_wallet_en.txt");
            if (messageTextEN != null) {
                messageText = messageTextEN;
            }
        }

        return new RipplePaperWallet(cryptoCurrency, content, address, seed, messageText, "application/zip", "zip");
    }

    @SuppressWarnings("all")
    private String readTemplate(String templateFile) {
        File f = new File(templateFile);
        if (f.exists() && f.canRead()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(templateFile)), "UTF-8");
                return content;
            } catch (IOException e) {
                log.error("readTemplate", e);
            }
        }
        return null;
    }
}
