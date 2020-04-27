package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase;

import org.knowm.xchange.service.BaseParamsDigest;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import javax.ws.rs.HeaderParam;

public class CoinbaseDigest  extends BaseParamsDigest {

    private CoinbaseDigest(byte[] key) {
        super(key, HMAC_SHA_256);
    }

    public static CoinbaseDigest createInstance(String key) {
        return new CoinbaseDigest(key.getBytes());
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {
        Mac sha256_HMAC = getMac();
        sha256_HMAC.update(restInvocation.getParamValue(HeaderParam.class, "CB-ACCESS-TIMESTAMP").toString().getBytes());
        sha256_HMAC.update(restInvocation.getHttpMethod().toUpperCase().getBytes());
        sha256_HMAC.update(restInvocation.getPath().getBytes());
        if (restInvocation.getRequestBody() != null) {
            sha256_HMAC.update(restInvocation.getRequestBody().getBytes());
        }
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
