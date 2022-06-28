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
package com.generalbytes.batm.server.extensions.extra.betverse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.ethereum.EtherUtils;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.eth.MasterPrivateKeyETH;
import com.generalbytes.bitrafael.tools.wallet.eth.WalletToolsETH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetVerseWalletGenerator implements IPaperWalletGenerator {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BTokenWalletGenerator");
    private IExtensionContext ctx;

    public BetVerseWalletGenerator(IExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String oneTimePassword, String userLanguage, boolean shouldBeVanity) {
        WalletToolsETH wt = new WalletToolsETH();
        String mnemonic = EtherUtils.generateMnemonic();

        MasterPrivateKeyETH m = wt.getMasterPrivateKey(mnemonic, "", CryptoCurrency.BVT.getCode(), IWalletTools.STANDARD_BIP44);

        String privateKey = wt.getWalletPrivateKey(m, CryptoCurrency.BVT.getCode(), 0, 0, 0);
        String address = wt.getWalletAddress(m, CryptoCurrency.BVT.getCode(), 0, 0, 0);

        byte[] content = ctx.createPaperWallet7ZIP(mnemonic, address, oneTimePassword, cryptoCurrency);

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

        return new BetVersePaperWallet(content, address, mnemonic, messageText, "application/zip", "zip", cryptoCurrency);
    }

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
