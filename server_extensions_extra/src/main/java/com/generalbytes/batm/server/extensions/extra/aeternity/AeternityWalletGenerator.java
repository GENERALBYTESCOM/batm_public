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
package com.generalbytes.batm.server.extensions.extra.aeternity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

public class AeternityWalletGenerator implements IPaperWalletGenerator {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BitcoinWalletGenerator");
    //private String prefix;
    private IExtensionContext ctx;

    public AeternityWalletGenerator(String prefix, IExtensionContext ctx) {
        //this.prefix = prefix;
        this.ctx = ctx;
    }

    @Override
    public IPaperWallet generateWallet(String cryptoCurrency, String oneTimePassword, String userLanguage) {
    	final KeyPairService keyPairService = new KeyPairServiceFactory().getService();
    	String mnemonicPassword = PasswordGenerator.generateRandomPassword(12);
    	BaseKeyPair keyPair = null;
		MnemonicKeyPair master = null;
		try {
			master = keyPairService.generateMasterMnemonicKeyPair(mnemonicPassword);
			keyPair = EncodingUtils.createBaseKeyPair(
	                keyPairService.generateDerivedKey(master, true).toRawKeyPair());

		} catch (AException e) {
			e.printStackTrace();
		}
				
    	String privateKey = keyPair.getPrivateKey();
        String address = keyPair.getPublicKey();
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
        return new AeternityPaperWallet(cryptoCurrency, content, address, privateKey, messageText,"application/zip", "zip");
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
    
    private static class PasswordGenerator {

        private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        private static final String NUMBER = "0123456789";
        private static final String OTHER_CHAR = "@";
        private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        // optional, make it more random
        private static final String PASSWORD_ALLOW_BASE_SHUFFLE = shuffleString(PASSWORD_ALLOW_BASE);
        private static final String PASSWORD_ALLOW = PASSWORD_ALLOW_BASE_SHUFFLE;
        private static SecureRandom random = new SecureRandom();

        public static String generateRandomPassword(int length) {
            if (length < 1) throw new IllegalArgumentException();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int rndCharAt = random.nextInt(PASSWORD_ALLOW.length());
                char rndChar = PASSWORD_ALLOW.charAt(rndCharAt);
                sb.append(rndChar);
            }
            return sb.toString();
        }
        // shuffle
        public static String shuffleString(String string) {
            List<String> letters = Arrays.asList(string.split(""));
            Collections.shuffle(letters);
            return letters.stream().collect(Collectors.joining());
        }
    }
}
