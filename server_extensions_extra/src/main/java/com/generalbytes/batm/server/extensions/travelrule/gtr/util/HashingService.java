package com.generalbytes.batm.server.extensions.travelrule.gtr.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility service for hashing values.
 */
@Slf4j
public class HashingService {

    /**
     * Hash the value with the SHA-512 algorithm.
     *
     * @param value Value for hashing.
     * @return SHA-512 hash of given value.
     */
    public String computeSha512(String value) {
        if (value == null) {
            return null;
        }

        MessageDigest digest = getSha512MessageDigest();
        if (digest == null) {
            return null;
        }

        byte[] hashedValueAsBytes = hashValue(digest, value);

        return convertBytesToHexString(hashedValueAsBytes);
    }

    private MessageDigest getSha512MessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            log.error("getSha512MessageDigest - implementation of SHA-512 algorithm not found");
            return null;
        }
    }

    private byte[] hashValue(MessageDigest digest, String value) {
        return digest.digest(value.getBytes(StandardCharsets.UTF_8));
    }

    private String convertBytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

}
