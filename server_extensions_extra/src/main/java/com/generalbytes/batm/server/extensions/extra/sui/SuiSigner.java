package com.generalbytes.batm.server.extensions.extra.sui;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

import java.util.Base64;

/**
 * Signs SUI transaction bytes using Ed25519 via BouncyCastle.
 *
 * SUI signature format (serialized flag || sig || pubkey, base64-encoded):
 *   byte[0]    = 0x00  (Ed25519 scheme flag)
 *   byte[1-64] = 64-byte Ed25519 signature
 *   byte[65-96]= 32-byte Ed25519 public key
 *
 * The bytes to sign are: Blake2b-256("TransactionData::" || txBytes)
 * but SUI nodes also accept signing the raw txBytes when using unsafe_transferSui.
 */
class SuiSigner {

    private SuiSigner() {}

    /**
     * Signs the transaction intent bytes and returns a SUI-serialized base64 signature.
     *
     * @param txBytes       raw transaction bytes (from unsafe_transferSui txBytes, base64-decoded)
     * @param privateKeySeed 32-byte Ed25519 private key seed
     * @param publicKeyBytes 32-byte Ed25519 public key
     */
    static String signTransaction(byte[] txBytes, byte[] privateKeySeed, byte[] publicKeyBytes) {
        // Prepend the SUI intent: TRANSACTION intent bytes [0, 0, 0]
        byte[] intentMessage = new byte[3 + txBytes.length];
        intentMessage[0] = 0; // IntentScope::TransactionData
        intentMessage[1] = 0; // IntentVersion::V0
        intentMessage[2] = 0; // AppId::Sui
        System.arraycopy(txBytes, 0, intentMessage, 3, txBytes.length);

        Ed25519PrivateKeyParameters privKey = new Ed25519PrivateKeyParameters(privateKeySeed, 0);
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privKey);
        signer.update(intentMessage, 0, intentMessage.length);
        byte[] signature = signer.generateSignature();

        // Build the serialized signature: flag || sig || pubkey
        byte[] serialized = new byte[1 + 64 + 32];
        serialized[0] = 0x00; // Ed25519 scheme flag
        System.arraycopy(signature, 0, serialized, 1, 64);
        System.arraycopy(publicKeyBytes, 0, serialized, 65, 32);

        return Base64.getEncoder().encodeToString(serialized);
    }
}
