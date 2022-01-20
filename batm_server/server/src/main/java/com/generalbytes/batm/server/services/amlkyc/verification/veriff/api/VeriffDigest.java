package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api;

import com.generalbytes.batm.server.coinutil.Hex;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class VeriffDigest implements ParamsDigest {

    private static final String ALGORITHM = "HmacSHA256";
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Mac mac;

    public VeriffDigest(String privateKey) {
        try {
            this.mac = Mac.getInstance(ALGORITHM);
            this.mac.init(new SecretKeySpec(privateKey.getBytes(CHARSET), ALGORITHM));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException | RuntimeException e) {
            throw new RuntimeException("Failed to initialize VeriffDigest, is the private key configured properly?", e);
        }
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {
        return digest(restInvocation.getRequestBody());
    }

    public String digest(String requestBody) {
        mac.update(requestBody.getBytes(CHARSET));
        return Hex.bytesToHexString(mac.doFinal());
    }
}



