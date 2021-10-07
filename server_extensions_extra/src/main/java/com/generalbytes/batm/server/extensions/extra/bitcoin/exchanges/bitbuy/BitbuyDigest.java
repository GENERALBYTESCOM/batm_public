package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class BitbuyDigest implements ParamsDigest {

    private static final String ALGORITHM = "HmacSHA256";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Mac mac;

    public BitbuyDigest(String apiSecret) throws GeneralSecurityException {
        this.mac = Mac.getInstance(ALGORITHM);
        this.mac.init(new SecretKeySpec(apiSecret.getBytes(CHARSET), ALGORITHM));
    }

    public String digestParams(RestInvocation restInvocation) {
        byte[] data = getMacData(restInvocation);
        byte[] signature = mac.doFinal(data);
        return Base64.getEncoder().encodeToString(signature);
    }

    protected byte[] getMacData(RestInvocation restInvocation) {
        try {
            return new ObjectMapper().writer().writeValueAsBytes(BitbuyMacData.from(restInvocation));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}




