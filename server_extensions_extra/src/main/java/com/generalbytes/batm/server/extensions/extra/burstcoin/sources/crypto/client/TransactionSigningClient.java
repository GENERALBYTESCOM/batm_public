package com.generalbytes.batm.server.extensions.extra.burstcoin.sources.crypto.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TransactionSigningClient {

  public static byte[] sign(byte[] message, String secretPhrase) {

    byte[] P = new byte[32];
    byte[] s = new byte[32];
    MessageDigest digest = sha256();
    Curve25519.keygen(P, s, digest.digest(Convert.toBytes(secretPhrase)));
    byte[] m = digest.digest(message);

    digest.update(m);
    byte[] x = digest.digest(s);

    byte[] Y = new byte[32];
    Curve25519.keygen(Y, null, x);

    digest.update(m);
    byte[] h = digest.digest(Y);

    byte[] v = new byte[32];
    Curve25519.sign(v, h, x, s);

    byte[] signature = new byte[64];
    System.arraycopy(v, 0, signature, 0, 32);
    System.arraycopy(h, 0, signature, 32, 32);

    return signature;
  }

  private static MessageDigest sha256() {
    return getMessageDigest("SHA-256");
  }

  private static MessageDigest getMessageDigest(String algorithm) {
    try {
      return MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
