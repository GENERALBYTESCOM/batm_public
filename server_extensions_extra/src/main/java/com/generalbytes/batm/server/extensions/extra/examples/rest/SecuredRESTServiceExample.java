/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.examples.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * REST service implementation class that uses JSR-000311 JAX-RS
 * demonstrating how to secure calling methods via signature
 */
@Path("/")
public class SecuredRESTServiceExample {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.SecuredRESTServiceExample");
    public static final String API_KEY      = "P123454S6818ASMSISNSOS2USJAODKMW";
    public static final String API_SECRET   = "SHDDOSMsS5540OK9KD7F53J4EIA2J383";

    private static Map<String,Long> previousNonceByAPIKey = new HashMap<>();
    private static Map<String,String> apiKeyToAPISecret = new HashMap<>();
    static {
        //In real world example api and api secrets should not be hardcoded but stored in database or filesystem in encrypted form
        //Same applies to previousNonceByAPIKey
        apiKeyToAPISecret.put(API_KEY,API_SECRET);
    }


    @GET
    @Path("/helloworld")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns JSON response on following URL https://localhost:7743/extensions/secured/helloworld
     */
    public Object helloWorld(@Context HttpServletRequest request, @Context HttpServletResponse response, @QueryParam("api_key") String apiKey,  @QueryParam("nonce") String nonce,  @QueryParam("signature") String signature, @QueryParam("serial_number") String serialNumber) {
        if (!checkSecurity(apiKey, nonce, signature,serialNumber)){
            return new MyExtensionExampleResponse(1, "Access Denied");
        }
        String serverVersion = RESTExampleExtension.getExtensionContext().getServerVersion();
        return new MyExtensionExampleResponse(0, "Server version is: " + serverVersion);
    }


    private boolean checkSecurity(String apiKey, String nonce, String signature, String ... params) {
        //check that API key exists
        if (!apiKeyToAPISecret.containsKey(apiKey)) {
            return false;
        }

        //Check nonce. Nonce must be higher every call. Typically use system time for nonce - java.lang.System.currentTimeMillis()
        //Nonce prevents against reply attacks.
        synchronized (apiKey.intern()) {
            Long previousNonce = previousNonceByAPIKey.get(apiKey);
            Long currentNonce = Long.parseLong(nonce);
            if (previousNonce != null) {
                if (previousNonce >= currentNonce) {
                    return false;
                }
            }
            previousNonceByAPIKey.put(apiKey, currentNonce);
        }

        StringBuilder input = new StringBuilder();
        input.append(nonce); //lets start with nonce so we have always different beginning
        for (int i = 0; i < params.length; i++) {
            input.append(params[i]); //it is a good practice to make all parameters part of the check
        }
        input.append(apiKey); //appending API key on the end makes it much more fun

        //caluclate signature
        String testSignature = generateSignature(input.toString(), apiKeyToAPISecret.get(apiKey));

        //signatures must match
        if (testSignature != null && testSignature.equalsIgnoreCase(signature)) {
            return true;
        }
        return false;
    }

    public static String generateSignature(String input, String privateKey) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(privateKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] result = sha256_HMAC.doFinal(input.getBytes());
            return bytesToHexString(result).toUpperCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error", e);
        }
        return null;
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
