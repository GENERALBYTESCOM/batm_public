package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.digest;

import com.generalbytes.batm.server.coinutil.Hex;
import com.generalbytes.batm.server.extensions.common.sumsub.SumsubException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.ISumSubApi;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * This class is responsible for generating a HMAC-SHA256 signature used in SumSub API requests.
 * It implements the ParamsDigest interface provided by the REST client framework, enabling the
 * construction of signed API requests.
 *
 * <p>This class is specifically designed to work with the SumSub API for secure and authenticated
 * communication between the client and the SumSub server.
 */
public class SumSubSignatureDigest implements ParamsDigest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String ALGORITHM = "HmacSHA256";
    private final Mac mac;

    /**
     * Constructs an instance of the SumSubSignatureDigest with a specified secret key.
     * This key is used to initialize the HMAC-SHA256 signature generator for secure communication
     * with the SumSub API.
     *
     * @param secret the secret key used for initializing the HMAC instance.
     * @throws SumsubException if configuration is not valid
     */
    public SumSubSignatureDigest(String secret) {
        try {
            this.mac = Mac.getInstance(ALGORITHM);
            this.mac.init(new SecretKeySpec(secret.getBytes(CHARSET), ALGORITHM));
        } catch (InvalidKeyException e) {
            throw new SumsubException("Failed to initialize SumSubSignatureDigest, is the private key configured properly?", e);
        } catch (NoSuchAlgorithmException e) {
            throw new SumsubException(e);
        }
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {
        Map<String, String> headers = restInvocation.getHttpHeadersFromParams();
        String ts = headers.get(ISumSubApi.HEADER_APP_TS);
        String httpMethod = restInvocation.getHttpMethod();
        // Use restInvocation.getInvocationUrl() and remove the baseUrl since restInvocation.getPath() does not include
        // the query parameters which is required for the signature
        String path = restInvocation.getInvocationUrl().replaceFirst(restInvocation.getBaseUrl(), "");
        String requestBody = restInvocation.getRequestBody();

        String combined = ts + httpMethod + path + requestBody;
        mac.update(combined.getBytes(CHARSET));
        return Hex.bytesToHexString(mac.doFinal());
    }
}
