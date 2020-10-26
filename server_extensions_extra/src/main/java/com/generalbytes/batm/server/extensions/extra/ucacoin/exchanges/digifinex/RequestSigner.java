package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import org.knowm.xchange.service.BaseParamsDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;



public class RequestSigner extends BaseParamsDigest {

    private static final Logger LOG = LoggerFactory.getLogger("batm.master.DigiFinexExchange.RequestSigner");

    
    private RequestSigner(byte[] key) {
        super(key, HMAC_SHA_256);
    }

    public static RequestSigner createInstance(String key) {
        RequestSigner instance = new RequestSigner(key.getBytes());
        return instance;
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {
        Mac sha256_HMAC = getMac();
        if (restInvocation.getRequestBody() != null) {
            sha256_HMAC.update(restInvocation.getRequestBody().getBytes());
        }
        byte[] result = sha256_HMAC.doFinal();
        String signature = bytesToHexString(result);

        LOG.info(signature);
        return signature;
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
