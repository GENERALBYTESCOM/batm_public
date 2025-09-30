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
     * Creates a deterministic UUID version 4 (UUIDv4) based on the given input bytes.
     * <p>
     * The UUID is generated deterministically by hashing the input with SHA-256, taking
     * the first 16 bytes of the hash, and setting the version and variant bits according
     * to the UUIDv4 standard.
     * </p>
     * <p>
     * This ensures that the same input always produces the same UUID, while the resulting
     * UUID still conforms to the UUIDv4 specification:
     * <ul>
     *   <li>Version 4 is set in bits 12-15 of the MSB (most significant bits).</li>
     *   <li>Variant is set to the IETF variant using bits 6 and 7 of the LSB (least significant bits).</li>
     * </ul>
     * </p>
     * <p>
     * See the official UUIDv4 specification for details:
     * <a href="https://www.rfc-editor.org/rfc/rfc9562.html#name-uuid-version-4">RFC 9562, UUID Version 4</a>.
     * </p>
     *
     * @param input Input as byte array.
     * @return Deterministic UUIDv4.
     */
    public static UUID createDeterministicUuidV4(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] hash = md.digest(input);

            ByteBuffer bb = ByteBuffer.wrap(hash, 0, 16);

            long mostSigBits = bb.getLong();
            long leastSigBits = bb.getLong();

            // set version 4
            mostSigBits &= 0xFFFFFFFFFFFF0FFFL;
            mostSigBits |= 0x0000000000004000L;

            // set IETF standard
            leastSigBits &= 0x3FFFFFFFFFFFFFFFL;
            leastSigBits |= 0x8000000000000000L;

            return new UUID(mostSigBits, leastSigBits);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Digest algorithm " + DIGEST_ALGORITHM + " not found for creating deterministic UUIDv4");
        }
    }

}
