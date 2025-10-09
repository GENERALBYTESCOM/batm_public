package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.digest;

import com.generalbytes.batm.server.coinutil.Hex;
import com.generalbytes.batm.server.extensions.common.sumsub.SumsubException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * The SumSubWebhookSecretDigest class is responsible for generating HMAC-based hash digests
 * used for verifying webhook requests from SumSub. This class provides functionality to calculate
 * a digest using a specified hashing algorithm and secret key.
 *
 * <p>This class is designed to ensure secure verification of webhook payloads, providing confidence
 * that the payloads originate from a trusted source.
 *
 * <p><a href="https://docs.sumsub.com/docs/webhooks">Webhooks documentation</a>
 */
public class SumSubWebhookSecretDigest {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Map<String, String> ALGO_MAP;

    static {
        ALGO_MAP = Map.of(
                "HMAC_SHA1_HEX", "HmacSHA1",
                "HMAC_SHA256_HEX", "HmacSHA256",
                "HMAC_SHA512_HEX", "HmacSHA512"
        );
    }

    private final Mac mac;

    /**
     * Constructs a new instance of SumSubWebhookSecretDigest with the specified secret key and algorithm key.
     *
     * @param secret the secret key used to initialize the HMAC instance
     * @param algKey the key to identify the hashing algorithm to be used (e.g., HMAC_SHA256_HEX)
     * @throws SumsubException if the initialization of the cryptographic components fails due to an invalid key or unsupported algorithm
     */
    public SumSubWebhookSecretDigest(String secret, String algKey) {
        try {
            String alg = ALGO_MAP.get(algKey);
            this.mac = Mac.getInstance(alg);
            this.mac.init(new SecretKeySpec(secret.getBytes(CHARSET), alg));
        } catch (InvalidKeyException e) {
            throw new SumsubException("Failed to initialize SumSubSignatureDigest, is the private key configured properly?", e);
        } catch (NoSuchAlgorithmException e) {
            throw new SumsubException(e);
        }
    }

    public String digest(String requestBody) {
        mac.update(requestBody.getBytes(CHARSET));
        return Hex.bytesToHexString(mac.doFinal());
    }
}
