package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

import org.knowm.xchange.service.BaseParamsDigest;

import javax.crypto.Mac;

import si.mazi.rescu.RestInvocation;

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
        String path = restInvocation.getPath();

        String toSign = timestamp+method+path;

        if (requestBody != null) {
            toSign+=requestBody;
        }

        sha256_HMAC.update(toSign.getBytes());
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
