package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal;

import jakarta.ws.rs.HeaderParam;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Base64;

public class CryptalSignatureDigest implements ParamsDigest {

    public static final String API_KEY_HEADER = "WP-API-KEY";
    public static final String TIMESTAMP_HEADER = "WP-TIMESTAMP";
    public static final String SIGNATURE_HEADER = "WP-SIGNATURE";

    private final PrivateKey privateKey;

    public CryptalSignatureDigest(String privateKey) {
        this.privateKey = parsePrivateKey(privateKey);
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {
        String timestamp = String.valueOf(restInvocation.getParamValue(HeaderParam.class, TIMESTAMP_HEADER));
        String path = restInvocation.getPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String query = restInvocation.getQueryString() == null ? "" : URLDecoder.decode(restInvocation.getQueryString(), StandardCharsets.UTF_8);
        String body = "GET".equalsIgnoreCase(restInvocation.getHttpMethod()) || restInvocation.getRequestBody() == null ? "" : restInvocation.getRequestBody();
        return sign(timestamp + path + query + body);
    }

    String sign(String data) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not sign the Cryptal Wallet Pro request", e);
        }
    }

    static PrivateKey parsePrivateKey(String value) {
        String base64 = value
            .replace("\\n", "")
            .replaceAll("-----(BEGIN|END) [A-Z ]+-----", "")
            .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(base64);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            try {
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(der));
            } catch (InvalidKeySpecException notPkcs8) {
                RSAPrivateKey rsa = RSAPrivateKey.getInstance(der);
                return keyFactory.generatePrivate(new RSAPrivateCrtKeySpec(
                    rsa.getModulus(), rsa.getPublicExponent(), rsa.getPrivateExponent(),
                    rsa.getPrime1(), rsa.getPrime2(), rsa.getExponent1(), rsa.getExponent2(), rsa.getCoefficient()));
            }
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("The private key is not an RSA key in PKCS#8 or PKCS#1 format", e);
        }
    }
}
