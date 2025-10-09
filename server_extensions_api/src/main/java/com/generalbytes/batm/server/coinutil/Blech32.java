/*
 * Copyright 2018 Coinomi Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.generalbytes.batm.server.coinutil;

import java.util.Arrays;
import java.util.Locale;

/**
 * Based on {@link Bech32} and modified according to <a href="https://github.com/ElementsProject/elements/blob/master/src/blech32.cpp">ElementsProject - blech32</a>
 */
public class Blech32 {

    /**
     * The Bech32 character set for decoding.
     */
    private static final byte[] CHARSET_REV = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        15, -1, 10, 17, 21, 20, 26, 30, 7, 5, -1, -1, -1, -1, -1, -1,
        -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18, 22, 31, 27, 19, -1,
        1, 0, 3, 16, 11, 28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1,
        -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18, 22, 31, 27, 19, -1,
        1, 0, 3, 16, 11, 28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1
    };

    public static class Bech32Data {
        public final String hrp;
        public final byte[] data;

        private Bech32Data(final String hrp, final byte[] data) {
            this.hrp = hrp;
            this.data = data;
        }
    }

    private static int polymod(final byte[] values) {
        long c = 1;
        for (byte v_i : values) {
            long c0 = (c >>> 55);
            c = ((c & 0x7fffffffffffffL) << 5) ^ (v_i & 0xFFL);
            if ((c0 & 1) != 0) c ^= 0x7d52fba40bd886L;
            if ((c0 & 2) != 0) c ^= 0x5e8dbf1a03950cL;
            if ((c0 & 4) != 0) c ^= 0x1c3a3c74072a18L;
            if ((c0 & 8) != 0) c ^= 0x385d72fa0e5139L;
            if ((c0 & 16) != 0) c ^= 0x7093e5a608865bL;
        }
        return (int) c;
    }

    /**
     * Expand a HRP for use in checksum computation.
     */
    private static byte[] expandHrp(final String hrp) {
        int hrpLength = hrp.length();
        byte ret[] = new byte[hrpLength * 2 + 1];
        for (int i = 0; i < hrpLength; ++i) {
            int c = hrp.charAt(i) & 0x7f; // Limit to standard 7-bit ASCII
            ret[i] = (byte) ((c >>> 5) & 0x07);
            ret[i + hrpLength + 1] = (byte) (c & 0x1f);
        }
        ret[hrpLength] = 0;
        return ret;
    }

    /**
     * Verify a checksum.
     */
    private static boolean verifyChecksum(final String hrp, final byte[] values) {
        byte[] hrpExpanded = expandHrp(hrp);
        byte[] combined = new byte[hrpExpanded.length + values.length];
        System.arraycopy(hrpExpanded, 0, combined, 0, hrpExpanded.length);
        System.arraycopy(values, 0, combined, hrpExpanded.length, values.length);
        return polymod(combined) == 1;
    }

    public static Bech32Data decode(final String str) throws AddressFormatException {
        if (str.length() < 13) {
            throw new AddressFormatException("Input too short: " + str.length());
        }
        if (str.length() > 1000) {
            throw new AddressFormatException("Input too long: " + str.length());
        }
        return decodeUnlimitedLength(str);
    }

    public static Bech32Data decodeUnlimitedLength(final String str) throws AddressFormatException {
        boolean lower = false, upper = false;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c < 33 || c > 126) throw new AddressFormatException("Invalid character: " + c + ", pos: " + i);
            if (c >= 'a' && c <= 'z') {
                if (upper)
                    throw new AddressFormatException("Invalid character: " + c + ", pos: " + i);
                lower = true;
            }
            if (c >= 'A' && c <= 'Z') {
                if (lower)
                    throw new AddressFormatException("Invalid character: " + c + ", pos: " + i);
                upper = true;
            }
        }
        final int pos = str.lastIndexOf('1');
        if (pos < 1) throw new AddressFormatException("Missing human-readable part");
        final int dataPartLength = str.length() - 1 - pos;
        if (dataPartLength < 6) throw new AddressFormatException("Data part too short: " + dataPartLength);
        byte[] values = new byte[dataPartLength];
        for (int i = 0; i < dataPartLength; ++i) {
            char c = str.charAt(i + pos + 1);
            if (CHARSET_REV[c] == -1) throw new AddressFormatException("Invalid character: " + c + ", pos: " + (i + pos + 1));
            values[i] = CHARSET_REV[c];
        }
        String hrp = str.substring(0, pos).toLowerCase(Locale.ROOT);
        if (!verifyChecksum(hrp, values)) throw new AddressFormatException("Invalid Checksum");
        return new Bech32Data(hrp, Arrays.copyOfRange(values, 0, values.length - 6));
    }
}
