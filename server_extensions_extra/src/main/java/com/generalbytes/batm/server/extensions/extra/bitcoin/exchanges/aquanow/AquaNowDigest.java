package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow;

import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;


public class AquaNowDigest implements ParamsDigest {


    private static final String ALGORITHM = "HmacSHA384";

    private final Mac mac;

    public AquaNowDigest(String apiSecret) throws GeneralSecurityException {
        this.mac = Mac.getInstance(ALGORITHM);
        this.mac.init(new SecretKeySpec(apiSecret.getBytes(), ALGORITHM));
    }

    public String digestParams(RestInvocation restInvocation) {
        String data = AquaNowMacData.from(restInvocation);
        byte[] signature = mac.doFinal(data.getBytes());
        return bytesToHexString(signature);
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




