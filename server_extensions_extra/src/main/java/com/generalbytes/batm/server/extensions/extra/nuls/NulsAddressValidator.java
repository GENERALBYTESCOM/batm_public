/*
 *
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
 */
package com.generalbytes.batm.server.extensions.extra.nuls;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

import java.util.Arrays;

/**
 * @author naveen
 */
public class NulsAddressValidator implements ICryptoAddressValidator {

    @Override
    public boolean isAddressValid(String address) {
        return validAddress(address);
    }

    @Override
    public boolean mustBeBase58Address() {
        return true;
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    private boolean validAddress(String address) {
        if (address.isEmpty()) {
            return false;
        }
        AddressTool addressTool = new AddressTool();
        return addressTool.validNormalAddress(addressTool.getAddressBytes(address),0);
    }

    private class NulsByteBuffer {
        private final byte[] payload;

        private int cursor;

        NulsByteBuffer(byte[] bytes) {
            this(bytes, 0);
        }

        NulsByteBuffer(byte[] bytes, int cursor) {
            if (null == bytes || bytes.length == 0 || cursor < 0) {
                throw new RuntimeException();
            }
            this.payload = bytes;
            this.cursor = cursor;
        }

        public byte[] readBytes(int length) throws Exception {
            try {
                byte[] b = new byte[length];
                System.arraycopy(payload, cursor, b, 0, length);
                cursor += length;
                return b;
            } catch (IndexOutOfBoundsException e) {
                throw new Exception(e);
            }
        }

        public short readShort() throws Exception {
            byte[] bytes = this.readBytes(2);
            if (null == bytes) {
                return 0;
            }
            return (short) (((bytes[1] << 8) | bytes[0] & 0xff));
        }

        public byte readByte() throws Exception {
            try {
                byte b = payload[cursor];
                cursor += 1;
                return b;
            } catch (IndexOutOfBoundsException e) {
                throw new Exception(e);
            }
        }


    }

    private class AddressTool{

        public final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
        private final int[] INDEXES = new int[128];
        private final int ADDRESS_LENGTH = 23;

        AddressTool(){
            Arrays.fill(INDEXES, -1);
            for (int i = 0; i < ALPHABET.length; i++) {
                INDEXES[ALPHABET[i]] = i;
            }
        }

        public  boolean validNormalAddress(byte[] bytes, int chainId) {
            if (null == bytes || bytes.length != ADDRESS_LENGTH) {
                return false;
            }
            NulsByteBuffer byteBuffer = new NulsByteBuffer(bytes);
            int chainid;
            byte type;
            try {
                chainid = byteBuffer.readShort();
                type = byteBuffer.readByte();
            } catch (Exception e) {
                return false;
            }
            /*if (chainId != chainid) {
                return false;
            }*/
            if (1 != type) {
                return false;
            }
            return true;
        }

        private  byte[] getAddressBytes(String addressString) {
            byte[] result = new byte[ADDRESS_LENGTH+1];
            byte[] chainIdBytes;
            byte[] hash160Bytes;
            try {
                int length = addressString.length();
                String hash160 = addressString.substring(0, length - 4);
                String chainIdHex = addressString.substring(length - 4, length);
                chainIdBytes = decodeHex(chainIdHex);
                hash160Bytes = decodeBase58(hash160);

                System.arraycopy(chainIdBytes, 0, result, 0, 2);
                System.arraycopy(hash160Bytes, 0, result, 2, 22);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        private byte[] decodeHex(String hexString) {
            byte[] bts = new byte[hexString.length() / 2];
            for (int i = 0; i < bts.length; i++) {
                bts[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
            }
            return bts;
        }

        private byte[] decodeBase58(String input) throws Exception {
            if (input.length() == 0) {
                return new byte[0];
            }
            // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
            byte[] input58 = new byte[input.length()];
            for (int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                int digit = c < 128 ? INDEXES[c] : -1;
                if (digit < 0) {
                    throw new Exception("Illegal character " + c + " at position " + i);
                }
                input58[i] = (byte) digit;
            }
            // Count leading zeros.
            int zeros = 0;
            while (zeros < input58.length && input58[zeros] == 0) {
                ++zeros;
            }
            // Convert base-58 digits to base-256 digits.
            byte[] decoded = new byte[input.length()];
            int outputStart = decoded.length;
            for (int inputStart = zeros; inputStart < input58.length; ) {
                decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
                if (input58[inputStart] == 0) {
                    ++inputStart; // optimization - skip leading zeros
                }
            }
            // Ignore extra leading zeroes that were added during the calculation.
            while (outputStart < decoded.length && decoded[outputStart] == 0) {
                ++outputStart;
            }
            // Return decoded data (including original number of leading zeros).
            return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
        }

        private byte divmod(byte[] number, int firstDigit, int base, int divisor) {
            // this is just long division which accounts for the base of the input digits
            int remainder = 0;
            for (int i = firstDigit; i < number.length; i++) {
                int digit = (int) number[i] & 0xFF;
                int temp = remainder * base + digit;
                number[i] = (byte) (temp / divisor);
                remainder = temp % divisor;
            }
            return (byte) remainder;
        }
    }
}
