package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponsePrepareTransaction;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Generates ECKey from provided {@link BlockIOResponsePrepareTransaction.BlockIOUserKey} data.
 * <p>
 * Implementation based on Block.io <a href="https://github.com/BlockIo/block_io-java/blob/master/src/main/java/lib/blockIo/Key.java">library</a>
 * </p>
 */
public class BlockIOKey {

    public static ECKey dynamicExtractKey(BlockIOResponsePrepareTransaction.BlockIOUserKey userKey, String secretPin) throws Exception{
        BlockIOResponsePrepareTransaction.BlockIOUserKeyAlgorithm algorithm = userKey.getAlgorithm();
        if(algorithm == null) {
            // use the legacy algorithm
            algorithm = new BlockIOResponsePrepareTransaction.BlockIOUserKeyAlgorithm();
            algorithm.setPbkdf2_salt("");
            algorithm.setPbkdf2_iterations(2048);
            algorithm.setPbkdf2_hash_function("SHA256");
            algorithm.setPbkdf2_phase1_key_length(16);
            algorithm.setPbkdf2_phase2_key_length(32);
            algorithm.setAes_iv(null);
            algorithm.setAes_cipher("AES-256-ECB");
            algorithm.setAes_auth_tag(null);
            algorithm.setAes_auth_data(null);
        }
        // string pin, string salt = "", int iterations = 2048, int phase1_key_length = 16, int phase2_key_length = 32, string hash_function = "SHA256"
        String b64Key = pinToAesKey(secretPin, algorithm.getPbkdf2_salt(),
            algorithm.getPbkdf2_iterations(),
            algorithm.getPbkdf2_phase1_key_length(),
            algorithm.getPbkdf2_phase2_key_length(),
            algorithm.getPbkdf2_hash_function());
        // string data, string key, string iv = null, string cipher_type = "AES-256-ECB", string auth_tag = null, string auth_data = null
        String decrypted = decrypt(userKey.getEncrypted_passphrase(),
            b64Key,
            algorithm.getAes_iv(),
            algorithm.getAes_cipher(),
            algorithm.getAes_auth_tag(),
            algorithm.getAes_auth_data());

        return extractKeyFromPassphrase(decrypted);
    }

    private static String pinToAesKey(String pin, String salt, int iterations, int phase1_key_length, int phase2_key_length, String hash_function) throws Exception {

        if (!hash_function.equals("SHA256"))
            throw new Exception("Unknown hash function specified. Are you using current version of this library?");

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());

        //round 1
        gen.init(pin.getBytes(StandardCharsets.UTF_8), salt.getBytes(StandardCharsets.UTF_8), iterations/2);
        byte[] dk = ((KeyParameter) gen.generateDerivedParameters(phase1_key_length * Byte.SIZE)).getKey();

        //round 2
        String hexStr = Hex.toHexString(dk).toLowerCase();
        gen.init(hexStr.getBytes(), salt.getBytes(StandardCharsets.UTF_8), iterations/2);
        dk = ((KeyParameter) gen.generateDerivedParameters(phase2_key_length * Byte.SIZE)).getKey();

        return Base64.getEncoder().encodeToString(dk);

    }

    private static String decrypt(String strToDecrypt, String secret, String iv, String cipher_type, String auth_tag, String auth_data) throws Exception {
        // encrypted_data, b64_enc_key, iv = nil, cipher_type = "AES-256-ECB", auth_tag = nil, auth_data = nil
        byte[] key = Base64.getDecoder().decode(secret);
        byte[] keyArrBytes32Value = Arrays.copyOf(key, 32);
        Cipher cipher;
        if(!cipher_type.equals("AES-256-GCM")){
            if(cipher_type.equals("AES-256-ECB")) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            } else if(cipher_type.equals("AES-256-CBC")) {
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            } else {
                throw new Exception("Unsupported cipher " + cipher_type);
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyArrBytes32Value, "AES");
            if(iv != null)
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(Hex.decode(iv)));
            else
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), StandardCharsets.UTF_8);
        }
        else{
            // AES-256-GCM
            if (auth_tag.length() != 32)
                throw new Exception("Auth tag must be 16 bytes exactly.");

            int AUTH_TAG_SIZE = 128; //16 bytes
            byte[] authTag = Hex.decode(auth_tag);
            byte[] ivBytes = Hex.decode(iv);
            byte[] cipherText = Base64.getDecoder().decode(strToDecrypt);

            ByteBuffer byteBuffer = ByteBuffer.allocate(cipherText.length + authTag.length + ivBytes.length);
            byteBuffer.put(ivBytes);
            byteBuffer.put(cipherText);
            byteBuffer.put(authTag);

            byte[] bytesToDecrypt = byteBuffer.array();

            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(AUTH_TAG_SIZE, bytesToDecrypt, 0, ivBytes.length));
            if (auth_data != null) {
                cipher.updateAAD(Hex.decode(auth_data));
            }

            byte[] plainText = cipher.doFinal(bytesToDecrypt, ivBytes.length, bytesToDecrypt.length - ivBytes.length);

            return new String(plainText, StandardCharsets.UTF_8);
        }
    }

    private static ECKey extractKeyFromPassphrase(String hexPass) throws NoSuchAlgorithmException {
        byte[] unHexlified = Hex.decode(hexPass);
        byte[] hashed = sha256Hash(unHexlified);

        return ECKey.fromPrivate(hashed);
    }

    private static byte[] sha256Hash(byte[] toHash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(toHash);
    }
}
