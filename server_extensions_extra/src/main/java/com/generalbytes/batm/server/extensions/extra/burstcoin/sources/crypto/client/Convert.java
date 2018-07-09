package com.generalbytes.batm.server.extensions.extra.burstcoin.sources.crypto.client;

import java.io.UnsupportedEncodingException;

public class Convert {

  public static byte[] toBytes(String s) {
    try {
      return s.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e.toString(), e);
    }
  }

}
