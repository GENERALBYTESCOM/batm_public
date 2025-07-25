/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.CoinbaseException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * ParamsDigest implementation for CDP authorization.
 * This implementation takes a Private Key and a Key Name from Coinbase
 * and generates and signs a JWT which is then used to authorize requests.
 *
 * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/authentication-authorization/api-key-authentication">Coinbase Documentation</a>
 */
public class CoinbaseCdpDigest implements ParamsDigest {

    private static final Logger log = LoggerFactory.getLogger(CoinbaseCdpDigest.class);
    private final String cdpPrivateKey;
    private final String cdpKeyName;

    /**
     * Create a new instance of CoinbaseCdpDigest.
     *
     * @param cdpPrivateKey The private key.
     * @param cdpKeyName    The key name.
     * @throws IllegalArgumentException If cdpPrivateKey or cdpKeyName is null.
     */
    public CoinbaseCdpDigest(String cdpPrivateKey, String cdpKeyName) {
        validateNotNull(cdpPrivateKey, "cdpPrivateKey cannot be null");
        validateNotNull(cdpKeyName, "cdpKeyName cannot be null");
        this.cdpPrivateKey = cdpPrivateKey;
        this.cdpKeyName = cdpKeyName;
    }

    /**
     * Generates a signed JWT to authorize requests for Coinbase API.
     *
     * <p>This method is invoked automatically when a REST API request includes
     * this digest as a header parameter.</p>
     *
     * <p>It processes the request details, including the URL and HTTP method,
     * to generate a JWT signed using the private key provided during this
     * digest's initialization.</p>
     *
     * @param restInvocation The request details encapsulated in a {@link RestInvocation}.
     * @return A signed JWT (prefixed with "Bearer ") for authenticating the request.
     * @throws CoinbaseException If the JWT generation fails for any reason, such as an
     *                           invalid private key format.
     */
    @Override
    public String digestParams(RestInvocation restInvocation) {
        String path = restInvocation.getInvocationUrl()
            .replaceFirst("https://", "")
            .replaceAll("\\?" + restInvocation.getQueryString(), "");
        String requestMethod = restInvocation.getHttpMethod();
        try {
            return generateJwt(path, requestMethod);
        } catch (Exception e) {
            log.error("Failed to generate JWT for request: {} {}", requestMethod, path, e);
            throw new CoinbaseException("Failed to generate JWT for request: " + requestMethod + " " + path);
        }
    }

    /**
     * @implNote This method was taken from Coinbase Documentation.
     * @see <a href="https://docs.cdp.coinbase.com/get-started/docs/jwt-authentication">Source</a>
     */
    private String generateJwt(String urlString, String httpMethod) throws Exception {
        // Register BouncyCastle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        // Load environment variables
        String privateKeyPEM = cdpPrivateKey.replace("\\n", "\n");
        String name = cdpKeyName;

        // create header object
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "ES256");
        header.put("typ", "JWT");
        header.put("kid", name);
        header.put("nonce", String.valueOf(Instant.now().getEpochSecond()));

        // create uri string for current request
        String requestMethod = httpMethod;
        String url = urlString;
        String uri = requestMethod + " " + url;

        // create data object
        Map<String, Object> data = new HashMap<>();
        data.put("iss", "cdp");
        data.put("nbf", Instant.now().getEpochSecond());
        data.put("exp", Instant.now().getEpochSecond() + 120);
        data.put("sub", name);
        data.put("uri", uri);

        // Load private key
        PEMParser pemParser = new PEMParser(new StringReader(privateKeyPEM));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        Object object = pemParser.readObject();
        PrivateKey privateKey;

        if (object instanceof PrivateKey) {
            privateKey = (PrivateKey) object;
        } else if (object instanceof org.bouncycastle.openssl.PEMKeyPair) {
            privateKey = converter.getPrivateKey(((org.bouncycastle.openssl.PEMKeyPair) object).getPrivateKeyInfo());
        } else {
            throw new Exception("Unexpected private key format");
        }
        pemParser.close();

        // Convert to ECPrivateKey
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);

        // create JWT
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            claimsSetBuilder.claim(entry.getKey(), entry.getValue());
        }
        JWTClaimsSet claimsSet = claimsSetBuilder.build();

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).customParams(header).build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);

        JWSSigner signer = new ECDSASigner(ecPrivateKey);
        signedJWT.sign(signer);

        String sJWT = signedJWT.serialize();
        return "Bearer " + sJWT;
    }

    private static void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}