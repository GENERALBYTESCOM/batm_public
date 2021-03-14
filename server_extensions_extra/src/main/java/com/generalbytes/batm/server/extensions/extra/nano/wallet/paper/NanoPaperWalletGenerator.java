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
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class NanoPaperWalletGenerator implements IPaperWalletGenerator {

    private static final Logger log = LoggerFactory.getLogger(NanoPaperWalletGenerator.class);

    private final NanoExtensionContext ctx;

    public NanoPaperWalletGenerator(NanoExtensionContext ctx) {
        this.ctx = ctx;
    }


    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String oneTimePassword, String userLanguage,
            boolean shouldBeVanity) {
        NanoRpcClient rpcClient = ctx.getRpcClient();
        if (rpcClient == null) {
            log.warn("Couldn't create paper wallet as no nano_node wallet is configured (required!)");
            return null;
        }

        /*
         * Create a cryptographically random seed. I have decided on using the seed instead of private key, as seeds
         * appear to be more widely supported by third-party wallets.
         */
        byte[] seedBytes = new byte[32];
        try {
            SecureRandom.getInstanceStrong().nextBytes(seedBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("Couldn't generate seed securely.", e);
            return null;
        }
        String seed = BaseEncoding.base16().encode(seedBytes);

        // Obtain account
        String address;
        try {
            address = rpcClient.accountFromSeed(seed, 0);
        } catch (IOException | NanoRpcClient.RpcException e) {
            log.error("Couldn't generate paper wallet via RPC.", e);
            return null;
        }

        // Create email content
        byte[] zipContents = ctx.getExtensionContext().createPaperWallet7ZIP(
                seed, address, oneTimePassword, cryptoCurrency);
        String message = createMessage(userLanguage, address);

        return new NanoPaperWallet(ctx.getCurrencyCode(), address, seed, message, zipContents);
    }


    private static String createMessage(String lang, String address) {
        String message = readMessageTemplate(lang);
        if (message == null && !lang.equalsIgnoreCase("en")) {
            message = readMessageTemplate("en"); // Try english fallback
            log.info("No paper wallet message for language {}, using EN fallback.", lang);
        }
        if (message == null) {
            log.warn("Couldn't locate a message file for paper wallet.");
            return "Here is your Nano account: " + address;
        }
        return message.replace("{address}", address);
    }

    private static String readMessageTemplate(String lang) {
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