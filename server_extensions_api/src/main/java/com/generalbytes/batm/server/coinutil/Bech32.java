/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.coinutil;

import java.util.HashMap;
import java.util.Map;

public class Bech32 {

    public static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
    private static final Long[] POLYMOD_GENERATORS = new Long[] {
        Long.parseLong("98f2bc8e61", 16),
        Long.parseLong("79b76d99e2", 16),
        Long.parseLong("f33e5fb3c4", 16),
        Long.parseLong("ae2eabe2a8", 16),
        Long.parseLong("1e4f43e470", 16)};

    private static final long POLYMOD_AND_CONSTANT = Long.parseLong("07ffffffff", 16);
    private static final char[] CHARS = CHARSET.toCharArray();

    private static Map<Character, Integer> charPositionMap;

    static {
        charPositionMap = new HashMap<>();
        for (int i = 0; i < CHARS.length; i++) {
            charPositionMap.put(CHARS[i], i);
        }
        if (charPositionMap.size() != 32) {
            throw new RuntimeException("The charset must contain 32 unique characters.");
        }
    }

    /**
     * Method calculateChecksumBytesPolymod calculates checksum from bitcoincash address
     *
     * @param checksumInput bitcoinhash address
     * @return Returns a 40 bits checksum in form of 5 8-bit arrays. This still has
     *         to me mapped to 5-bit array representation
     */
    public static byte[] calculateChecksumBytesPolymod(byte[] checksumInput) {
        long l = 1;
        for (int i = 0; i < checksumInput.length; i++) {
            byte l0 = (byte)(l>>>35);
            l = ((l & POLYMOD_AND_CONSTANT)<<5) ^ (Long.parseLong(String.format("%02x", checksumInput[i]), 16));
            if ((l0 & 0x01) != 0) {
                l = l ^ POLYMOD_GENERATORS[0].longValue();
            }

            if ((l0 & 0x02) != 0) {
                l = l ^ POLYMOD_GENERATORS[1].longValue();
            }

            if ((l0 & 0x04) != 0) {
                l = l ^ POLYMOD_GENERATORS[2].longValue();
            }

            if ((l0 & 0x08) != 0) {
                l = l ^ POLYMOD_GENERATORS[3].longValue();
            }

            if ((l0 & 0x10) != 0) {
                l = l ^ POLYMOD_GENERATORS[4].longValue();
            }
        }

        byte[] checksum = long2Bytes((l ^ 1l));
        if (checksum.length == 5) {
            return checksum;
        } else {
            byte[] newChecksumArray = new byte[5];
            System.arraycopy(checksum, Math.max(0, checksum.length - 5), newChecksumArray, Math.max(0, 5 - checksum.length), Math.min(5, checksum.length));
            return newChecksumArray;
        }
    }

    /**
     * Decode a base32 string back to the byte array representation
     *
     * @param base32String
     * @return
     */
    public static byte[] decode(String base32String) {
        byte[] bytes = new byte[base32String.length()];

        char[] charArray = base32String.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            Integer position = charPositionMap.get(charArray[i]);
            if (position == null) {
                throw new RuntimeException("There seems to be an invalid char: " + charArray[i]);
            }
            bytes[i] = (byte) ((int) position);
        }

        return bytes;
    }

    /**
     * Method concatenateByteArrays concatenates two byte arrays
     *
     * @param first - first byte array
     * @param second - second byte array
     * @return concatenated byte array
     */
    public static byte[] concatenateByteArrays(byte[] first, byte[] second) {
        byte[] concatenatedBytes = new byte[first.length + second.length];
        System.arraycopy(first, 0, concatenatedBytes, 0, first.length);
        System.arraycopy(second, 0, concatenatedBytes, first.length, second.length);
        return concatenatedBytes;
    }

    /**
     * Method getPrefixBytes returns byte array for prefixString parameter
     *
     * @param prefixString prefix for bitcoincash
     * @return byte array for bitcoincash
     */
    public static byte[] getPrefixBytes(String prefixString) {
        byte[] prefixBytes = new byte[prefixString.length()];
        char[] charArray = prefixString.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            prefixBytes[i] = (byte) (charArray[i] & 0x1f);
        }

        return prefixBytes;
    }

    /**
     * Method calculateBitLength calculates the size of bits representing the number of long format
     *
     * @param value number of type long
     * @return bit length of value
     */
    private static int calculateBitLength(long value) {
        return Long.SIZE-Long.numberOfLeadingZeros(value);
    }

    /**
     * Method long2Bytes converts long into byte array
     *
     * @param l number of type long
     * @return byte array
     */
    private static byte[] long2Bytes(long l) {
        int bitLength = calculateBitLength(l);
        byte[] result = new byte[bitLength];
        for (int i = bitLength - 1; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= bitLength;
        }
        return result;
    }

    /**
     * Method bytes2Long converts byte array to long
     *
     * @param bytes byte array
     * @return result of type long
     */
    public static long bytes2Long(byte[] bytes) {
        long result = 0;
        for (byte b : bytes) {
            result <<= bytes.length;
            result |= (b & 0xFF);
        }
        return result;
    }

}
