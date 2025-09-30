package com.generalbytes.batm.server.extensions.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Generator for creating deterministic UUIDv4.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeterministicUuidV4Generator {

    private static final String DIGEST_ALGORITHM = "SHA-256";

    /**
     * Creates a deterministic UUIDv4 based on the input value.
     *
     * @param input Input as byte array.
     * @return Deterministic UUIDv4.
     */
    public static UUID createDeterministicUuidV4(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] hash = md.digest(input);

            ByteBuffer bb = ByteBuffer.wrap(hash, 0, 16);

            long msb = bb.getLong();
            long lsb = bb.getLong();

            // set version 4
            msb &= 0xFFFFFFFFFFFF0FFFL;
            msb |= 0x0000000000004000L;

            // set IETF standard
            lsb &= 0x3FFFFFFFFFFFFFFFL;
            lsb |= 0x8000000000000000L;

            return new UUID(msb, lsb);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Digest algorithm " + DIGEST_ALGORITHM + " not found for creating deterministic UUIDv4");
        }
    }

}
