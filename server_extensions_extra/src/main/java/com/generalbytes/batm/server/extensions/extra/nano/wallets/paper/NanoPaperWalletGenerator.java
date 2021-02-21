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
package com.generalbytes.batm.server.extensions.extra.nano.wallets.paper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;

import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencySpecification;
import uk.oczadly.karl.jnano.util.WalletUtil;
import uk.oczadly.karl.jnano.model.NanoAccount;
import uk.oczadly.karl.jnano.model.HexData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NanoPaperWalletGenerator implements IPaperWalletGenerator {

    private static final Logger log = LoggerFactory.getLogger(NanoPaperWalletGenerator.class);


    private final NanoCurrencySpecification addrSpec;
    private final IExtensionContext ctx;

    public NanoPaperWalletGenerator(IExtensionContext ctx, NanoCurrencySpecification addrSpec) {
        this.addrSpec = addrSpec;
        this.ctx = ctx;
    }


    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String oneTimePassword, String userLanguage,
            boolean shouldBeVanity) {
        // Create seed, private key and address
        HexData seed;
        try {
            seed = WalletUtil.generateRandomKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("Couldn't generate paper wallet seed", e);
            return null;
        }
        HexData privateKey = WalletUtil.deriveKeyFromSeed(seed);
        NanoAccount account = NanoAccount.fromPrivateKey(privateKey).withPrefix(addrSpec.getAddressPrefix());

        // Create email content
        byte[] zip = ctx.createPaperWallet7ZIP(seed.toHexString(), addrSpec.toUriAddress(account),
                oneTimePassword, cryptoCurrency);
        String message = createMessage(userLanguage, account.toAddress());

        // Return paper wallet
        return new NanoPaperWallet(cryptoCurrency, account.toAddress(), seed.toHexString(),
                message, "zip", "application/zip", zip);
    }


    public static String createMessage(String lang, String address) {
        String message = readMessageTemplate(lang);
        if (message == null && !lang.equalsIgnoreCase("en")) {
            message = readMessageTemplate("en"); // Try english fallback
            log.warn("No paper wallet message for language {}, using EN fallback.", lang);
        }
        if (message == null) {
            log.error("Couldn't locate a message file for paper wallet.");
            return "Here is your Nano account.";
        }
        return message.replace("{address}", address);
    }

    public static String readMessageTemplate(String lang) {
        String fileName = "paperwallet_msg_" + lang.toLowerCase() + ".txt";
        InputStream is = NanoPaperWalletGenerator.class.getResourceAsStream(fileName);
        if (is == null)
            return null;
        // Read file
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[512];
            int n;
            while ((n = is.read(buf)) > 0) {
                bos.write(buf, 0, n);
            }
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Couldn't load paper wallet message file.", e);
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {}
        }
    }

}