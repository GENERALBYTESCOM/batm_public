package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex;

import javax.crypto.Mac;

import si.mazi.rescu.RestInvocation;

import org.knowm.xchange.service.BaseParamsDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestSigner extends BaseParamsDigest {

    private static final Logger LOG = LoggerFactory.getLogger("batm.master.BkexExchange.RequestSigner");

    
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
        String payloadString = "";
        LOG.info("QUERY" + restInvocation.getQueryString());

        payloadString += restInvocation.getQueryString();

        LOG.info("PAYLOAD" + payloadString);

        sha256_HMAC.update(payloadString.getBytes());

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
