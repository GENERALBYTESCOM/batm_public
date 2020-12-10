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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

import org.knowm.xchange.service.BaseParamsDigest;

import javax.crypto.Mac;

import si.mazi.rescu.RestInvocation;

import java.nio.charset.StandardCharsets;

public class CBDigest extends BaseParamsDigest {

    private long timestamp;

    private CBDigest(byte[] key) {
        super(key, HMAC_SHA_256);
    }

    public static CBDigest createInstance(String apiSecret, long timestamp) {
        CBDigest instance = new CBDigest(apiSecret.getBytes());
        instance.timestamp = timestamp;
        return instance;
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {
        Mac sha256_HMAC = getMac();
        String requestBody = restInvocation.getRequestBody();
        String method = restInvocation.getHttpMethod().toUpperCase();
        String path = restInvocation.getInvocationUrl().replace(restInvocation.getBaseUrl(), "");

        String toSign = timestamp+method+path;

        if (requestBody != null) {
            toSign+=requestBody;
        }

        sha256_HMAC.update(toSign.getBytes(StandardCharsets.UTF_8));
        byte[] result = sha256_HMAC.doFinal();
        return bytesToHexString(result);
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
