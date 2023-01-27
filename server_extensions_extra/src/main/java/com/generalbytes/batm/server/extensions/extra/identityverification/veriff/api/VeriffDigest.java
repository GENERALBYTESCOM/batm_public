package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api;

import com.generalbytes.batm.server.coinutil.Hex;
import si.mazi.rescu.HttpMethod;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.PathParam;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

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
        if (restInvocation.getMethodMetadata().getHttpMethod() == HttpMethod.GET) {
            // In case of a GET request, where there is no payload in the body and just URL parameters,
            // for example GET "/transportation-registry/123456,
            // the payload to be signed will be the id from the URL
            return digest(getFirstPathParamValue(restInvocation));
        }
        return digest(restInvocation.getRequestBody());
    }

    private String getFirstPathParamValue(RestInvocation restInvocation) {
        Collection<String> pathParamValues = restInvocation.getParamsMap().get(PathParam.class).asHttpHeaders().values();
        if (pathParamValues.size() > 1) {
            throw new IllegalArgumentException("We don't know how to get signature for a GET with more than one PathParam: " + pathParamValues);
        }
        return pathParamValues.stream().findAny().orElseThrow(() -> new IllegalArgumentException("GET call must have one PathParam"));
    }

    public String digest(String requestBody) {
        mac.update(requestBody.getBytes(CHARSET));
        return Hex.bytesToHexString(mac.doFinal());
    }
}



