package com.generalbytes.batm.server.extensions.travelrule.gtr.util;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.interfaces.Box;
import com.goterl.lazysodium.interfaces.MessageEncoder;
import com.goterl.lazysodium.interfaces.Sign;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility service for encrypting and decrypting messages using Curve25519-based key exchange.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/appendix/cipher-curve-25519-guideline">Global Travel Rule (GTR) documentation</a>
 */
@Slf4j
public class Curve25519Encryptor {

    private final LazySodiumJava sodium;

    /**
     * Initializes {@link LazySodiumJava} with a {@link Base64} encoder to ensure safe encoding of binary data.
     */
    public Curve25519Encryptor() {
        sodium = new LazySodiumJava(new SodiumJava(), new UrlSafeBase64MessageEncoder());
        sodium.sodiumInit();
    }

    private static class UrlSafeBase64MessageEncoder implements MessageEncoder {

        @Override
        public String encode(byte[] cipher) {
            return Base64.getUrlEncoder().encodeToString(cipher);
        }

        @Override
        public byte[] decode(String cipherText) {
            return Base64.getUrlDecoder().decode(cipherText);
        }

    }

    /**
     * Encrypts the given plaintext using a shared key derived from a Curve25519-based key exchange.
     * The result is additionally Base64-encoded to ensure safe transmission over HTTP.
     *
     * @param data            Plaintext as a string to encrypt.
     * @param remotePublicKey Public key of counterparty (recipient) in Base64 format.
     * @param selfPrivateKey  Own private key (sender) in Base64 format.
     * @return Base64-encoded encrypted data.
     * @throws TravelRuleProviderException if encryption fails.
     */
    public String encrypt(String data, String remotePublicKey, String selfPrivateKey) {
        byte[] dataAsBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] sharedKey = getSharedKey(remotePublicKey, selfPrivateKey);

        byte[] encryptedData = encryptInternal(dataAsBytes, sharedKey);

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private byte[] encryptInternal(byte[] data, byte[] sharedKey) {
        byte[] nonce = sodium.randomBytesBuf(Box.NONCEBYTES);
        byte[] encryptedData = new byte[data.length + Box.MACBYTES];

        if (!sodium.cryptoBoxEasyAfterNm(encryptedData, data, data.length, nonce, sharedKey)) {
            throw new TravelRuleProviderException("Curve25519 encryption failed with the provided keys and nonce");
        }

        return prependNonceToEncryptedData(nonce, encryptedData);
    }

    private byte[] prependNonceToEncryptedData(byte[] nonce, byte[] encryptedData) {
        byte[] result = new byte[nonce.length + encryptedData.length];

        System.arraycopy(nonce, 0, result, 0, nonce.length);
        System.arraycopy(encryptedData, 0, result, nonce.length, encryptedData.length);

        return result;
    }

    /**
     * Decrypts the given Base64-encoded encrypted data using a shared key derived from a Curve25519-based key exchange.
     *
     * @param data            Base64-encoded encrypted data.
     * @param remotePublicKey Public key of counterparty (sender) in Base64 format.
     * @param selfPrivateKey  Own private key (recipient) in Base64 format.
     * @return Decrypted plaintext as a string.
     * @throws TravelRuleProviderException if decryption fails.
     */
    public String decrypt(String data, String remotePublicKey, String selfPrivateKey) {
        byte[] dataAsBytes = decodeBase64Input(data, "Data");
        byte[] sharedKey = getSharedKey(remotePublicKey, selfPrivateKey);

        byte[] decryptedData = decryptInternal(dataAsBytes, sharedKey);

        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    private byte[] decryptInternal(byte[] data, byte[] sharedKey) {
        byte[] nonce = Arrays.copyOfRange(data, 0, Box.NONCEBYTES);
        byte[] encryptedData = Arrays.copyOfRange(data, Box.NONCEBYTES, data.length);
        byte[] decryptedData = new byte[encryptedData.length - Box.MACBYTES];

        if (!sodium.cryptoBoxOpenEasyAfterNm(decryptedData, encryptedData, encryptedData.length, nonce, sharedKey)) {
            throw new TravelRuleProviderException("Curve25519 decryption failed with the provided keys and nonce");
        }

        return decryptedData;
    }

    private byte[] getSharedKey(String publicKey, String privateKey) {
        byte[] publicKeyAsBytes = decodeBase64Input(publicKey, "Public key");
        byte[] privateKeyAsBytes = decodeBase64Input(privateKey, "Private key");

        return getSharedKey(publicKeyAsBytes, privateKeyAsBytes);
    }

    private byte[] getSharedKey(byte[] publicKey, byte[] privateKey) {
        byte[] sharedKey = new byte[Box.BEFORENMBYTES];
        byte[] curvePublicKey = new byte[Sign.CURVE25519_PUBLICKEYBYTES];
        byte[] curvePrivateKey = new byte[Sign.CURVE25519_SECRETKEYBYTES];

        sodium.convertPublicKeyEd25519ToCurve25519(curvePublicKey, publicKey);
        sodium.convertSecretKeyEd25519ToCurve25519(curvePrivateKey, privateKey);
        sodium.cryptoBoxBeforeNm(sharedKey, curvePublicKey, curvePrivateKey);

        return sharedKey;
    }

    private byte[] decodeBase64Input(String input, String dataType) {
        try {
            return Base64.getDecoder().decode(input);
        } catch (IllegalArgumentException e) {
            throw new TravelRuleProviderException(dataType + " is not in Base64 format, decoding is not possible");
        }
    }

    /**
     * Validate whether the specified key pair is valid.
     *
     * @param publicKey  Public key.
     * @param privateKey Private key.
     * @return {@code True} if key pair is valid, otherwise {@code false}.
     */
    public boolean validateKeyPair(String publicKey, String privateKey) {
        byte[] publicKeyAsBytes = decodeBase64Input(publicKey, "Public key");
        byte[] privateKeyAsBytes = decodeBase64Input(privateKey, "Private key");

        byte[] calculatedPublicKeyAsBytes = new byte[Sign.PUBLICKEYBYTES];
        sodium.cryptoSignEd25519SkToPk(calculatedPublicKeyAsBytes, privateKeyAsBytes);

        return Arrays.equals(publicKeyAsBytes, calculatedPublicKeyAsBytes);
    }

}
