package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    private byte[] getMacData(RestInvocation restInvocation) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        // THE ORDER OF THE FOLLOWING FIELDS IS IMPORTANT!
        // The bitbuy documentation uses JSONObject which uses HashMap where elements are unordered
        // but the MAC is computed over the resulting JSON string
        node.put("path", restInvocation.getPath());
        node.put("content-length", getBodyLength(restInvocation));
        node.put("query", restInvocation.getQueryString());
        try {
            return mapper.writer().writeValueAsBytes(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private int getBodyLength(RestInvocation restInvocation) {
        String body = restInvocation.getRequestBody();
        return (body == null || body.isEmpty()) ? -1 : body.length();
    }
}




