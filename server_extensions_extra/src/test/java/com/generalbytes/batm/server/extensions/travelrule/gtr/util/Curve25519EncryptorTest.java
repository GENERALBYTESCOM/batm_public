package com.generalbytes.batm.server.extensions.travelrule.gtr.util;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Curve25519EncryptorTest {

    private static final String DATA_TO_ENCRYPT = "{\"message\": \"my data to encrypt\"}";
    private static final String REMOTE_PUBLIC_KEY = "ChdUeaXA1gqBSnHldH04wZUdSobNvwDRbpVEhTtpKzM=";
    private static final String SELF_PRIVATE_KEY
            = "M86vvrRiktC9t7/5PU0KdqI9DyJw8G8I7dd/DDXutDIKF1R5pcDWCoFKceV0fTjBlR1Khs2/ANFulUSFO2krMw==";
    private final Curve25519Encryptor encryptor = new Curve25519Encryptor();

    @Test
    void testEncryptAndDecrypt() {
        String encryptedData = encryptor.encrypt(DATA_TO_ENCRYPT, REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY);
        String decryptedData = encryptor.decrypt(encryptedData, REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY);

        assertEquals(100, encryptedData.length());
        assertEquals(DATA_TO_ENCRYPT, decryptedData);
    }

    @Test
    void testEncrypt_publicKey_notBase64() {
        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> encryptor.encrypt(DATA_TO_ENCRYPT, "public_key", SELF_PRIVATE_KEY)
        );

        assertEquals("Public key is not in Base64 format, decoding is not possible", exception.getMessage());
    }

    @Test
    void testEncrypt_privateKey_notBase64() {
        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> encryptor.encrypt(DATA_TO_ENCRYPT, REMOTE_PUBLIC_KEY, "private_key")
        );

        assertEquals("Private key is not in Base64 format, decoding is not possible", exception.getMessage());
    }

    @Test
    void testDecrypt_data_notBase64() {
        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> encryptor.decrypt("invalid_base64", REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY)
        );

        assertEquals("Data is not in Base64 format, decoding is not possible", exception.getMessage());
    }

    @Test
    void testDecrypt_publicKey_notBase64() {
        String encryptedData = encryptor.encrypt(DATA_TO_ENCRYPT, REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> encryptor.decrypt(encryptedData, "public_key", SELF_PRIVATE_KEY)
        );

        assertEquals("Public key is not in Base64 format, decoding is not possible", exception.getMessage());
    }

    @Test
    void testDecrypt_privateKey_notBase64() {
        String encryptedData = encryptor.encrypt(DATA_TO_ENCRYPT, REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> encryptor.decrypt(encryptedData, REMOTE_PUBLIC_KEY, "private_key")
        );

        assertEquals("Private key is not in Base64 format, decoding is not possible", exception.getMessage());
    }

    @Test
    void testDecrypt_publicKey_invalid() {
        String encryptedData = encryptor.encrypt(DATA_TO_ENCRYPT, REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> encryptor.decrypt(encryptedData, "invalid", SELF_PRIVATE_KEY)
        );

        assertEquals("Curve25519 decryption failed with the provided keys and nonce", exception.getMessage());
    }

    @Test
    void testDecrypt_privateKey_invalid() {
        String encryptedData = encryptor.encrypt(DATA_TO_ENCRYPT, REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> encryptor.decrypt(encryptedData, REMOTE_PUBLIC_KEY, "invalid")
        );

        assertEquals("Curve25519 decryption failed with the provided keys and nonce", exception.getMessage());
    }

    @Test
    void testValidateKeyPair_valid() {
        boolean valid = encryptor.validateKeyPair(REMOTE_PUBLIC_KEY, SELF_PRIVATE_KEY);

        assertTrue(valid);
    }

    @Test
    void testValidateKeyPair_invalid() {
        String invalidPublicKey = "S/1A8vBSFMe/MtT+nEkQJwJNBO60VrNDKPPOL44tn8s=";

        boolean valid = encryptor.validateKeyPair(invalidPublicKey, SELF_PRIVATE_KEY);

        assertFalse(valid);
    }

}