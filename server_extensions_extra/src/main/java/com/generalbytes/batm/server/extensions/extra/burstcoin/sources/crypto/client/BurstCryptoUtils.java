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
package com.generalbytes.batm.server.extensions.extra.burstcoin.sources.crypto.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BurstCryptoUtils {

    private static byte[] sign(byte[] message, String secretPhrase) {
        byte[] P = new byte[32];
        byte[] s = new byte[32];
        MessageDigest digest = sha256();
        Curve25519.keygen(P, s, digest.digest(Convert.toBytes(secretPhrase)));
        byte[] m = digest.digest(message);

        digest.update(m);
        byte[] x = digest.digest(s);

        byte[] Y = new byte[32];
        Curve25519.keygen(Y, null, x);

        digest.update(m);
        byte[] h = digest.digest(Y);

        byte[] v = new byte[32];
        Curve25519.sign(v, h, x, s);

        byte[] signature = new byte[64];
        System.arraycopy(v, 0, signature, 0, 32);
        System.arraycopy(h, 0, signature, 32, 32);

        return signature;
    }

    public static byte[] signTransaction(byte[] unsignedTransactionBytes, String secretPhrase) {
        byte[] signature = sign(unsignedTransactionBytes, secretPhrase);
        byte[] signedTransaction = new byte[unsignedTransactionBytes.length];
        System.arraycopy(unsignedTransactionBytes, 0, signedTransaction, 0, unsignedTransactionBytes.length);
        System.arraycopy(signature, 0, signedTransaction, 96, 64);
        return signedTransaction;
    }

    public static String getPublicKey(String secretPhrase) {
        byte[] P = new byte[32];
        byte[] s = new byte[32];
        MessageDigest digest = sha256();
        Curve25519.keygen(P, s, digest.digest(Convert.toBytes(secretPhrase)));
        return Convert.toHexString(P);
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
