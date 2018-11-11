/* Copyright (c) 2018 Coinomi Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * source: https://github.com/Coinomi/bech32/blob/3278e1f/ref/java/src/main/java/org/bech32/Bech32.java
 */

package com.generalbytes.batm.server.coinutil;

import java.util.Locale;

public class Bech32 {

    static class Pair<K, V> {
        private K elementLeft = null;
        private V elementRight = null;

        public static <K, V> Pair<K, V> of(K elementLeft, V elementRight) {
            return new Pair<>(elementLeft, elementRight);
        }

        public Pair(K elementLeft, V elementRight) {
            this.elementLeft = elementLeft;
            this.elementRight = elementRight;
        }

        public K getLeft() {
            return elementLeft;
        }

        public V getRight() {
            return elementRight;
        }
    }

    private static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    public static String bech32Encode(String hrp, byte[] data) {

        byte[] chk = createChecksum(hrp.getBytes(), data);
        byte[] combined = new byte[chk.length + data.length];

        System.arraycopy(data, 0, combined, 0, data.length);
        System.arraycopy(chk, 0, combined, data.length, chk.length);

        byte[] xlat = new byte[combined.length];
        for(int i = 0; i < combined.length; i++)   {
            xlat[i] = (byte)CHARSET.charAt(combined[i]);
        }

        byte[] ret = new byte[hrp.getBytes().length + xlat.length + 1];
        System.arraycopy(hrp.getBytes(), 0, ret, 0, hrp.getBytes().length);
        System.arraycopy(new byte[] { 0x31 }, 0, ret, hrp.getBytes().length, 1);
        System.arraycopy(xlat, 0, ret, hrp.getBytes().length + 1, xlat.length);

        return new String(ret);
    }

    public static Pair<String, byte[]> bech32Decode(String bech) throws AddressFormatException  {

        byte[] buffer = bech.getBytes();
        for(byte b : buffer)   {
            if(b < 0x21 || b > 0x7e)    {
                throw new AddressFormatException("bech32 characters out of range");
            }
        }

        if(!bech.equals(bech.toLowerCase(Locale.ROOT)) && !bech.equals(bech.toUpperCase(Locale.ROOT)))  {
            throw new AddressFormatException("bech32 cannot mix upper and lower case");
        }

        bech = bech.toLowerCase();
        int pos = bech.lastIndexOf("1");
        if(pos < 1)    {
            throw new AddressFormatException("bech32 missing separator");
        }
        else if(pos + 7 > bech.length())    {
            throw new AddressFormatException("bech32 separator misplaced");
        }
        else if(bech.length() < 8)    {
            throw new AddressFormatException("bech32 input too short");
        }
        else if(bech.length() > 90)    {
            throw new AddressFormatException("bech32 input too long");
        }

        String s = bech.substring(pos + 1);
        for(int i = 0; i < s.length(); i++) {
            if(CHARSET.indexOf(s.charAt(i)) == -1)    {
                throw new AddressFormatException("bech32 characters out of range");
            }
        }

        byte[] hrp = bech.substring(0, pos).getBytes();

        byte[] data = new byte[bech.length() - pos - 1];
        for(int j = 0, i = pos + 1; i < bech.length(); i++, j++) {
            data[j] = (byte)CHARSET.indexOf(bech.charAt(i));
        }

        if (!verifyChecksum(hrp, data)) {
            throw new AddressFormatException("invalid bech32 checksum");
        }

        byte[] ret = new byte[data.length - 6];
        System.arraycopy(data, 0, ret, 0, data.length - 6);

        return Pair.of(new String(hrp), ret);
    }

    private static int polymod(byte[] values)  {

        final int[] GENERATORS = { 0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3 };

        int chk = 1;

        for(byte b : values)   {
            byte top = (byte)(chk >> 0x19);
            chk = b ^ ((chk & 0x1ffffff) << 5);
            for(int i = 0; i < 5; i++)   {
                chk ^= ((top >> i) & 1) == 1 ? GENERATORS[i] : 0;
            }
        }

        return chk;
    }

    private static byte[] hrpExpand(byte[] hrp) {

        byte[] buf1 = new byte[hrp.length];
        byte[] buf2 = new byte[hrp.length];
        byte[] mid = new byte[1];

        for (int i = 0; i < hrp.length; i++)   {
            buf1[i] = (byte)(hrp[i] >> 5);
        }
        mid[0] = 0x00;
        for (int i = 0; i < hrp.length; i++)   {
            buf2[i] = (byte)(hrp[i] & 0x1f);
        }

        byte[] ret = new byte[(hrp.length * 2) + 1];
        System.arraycopy(buf1, 0, ret, 0, buf1.length);
        System.arraycopy(mid, 0, ret, buf1.length, mid.length);
        System.arraycopy(buf2, 0, ret, buf1.length + mid.length, buf2.length);

        return ret;
    }

    private static boolean verifyChecksum(byte[] hrp, byte[] data) {

        byte[] exp = hrpExpand(hrp);

        byte[] values = new byte[exp.length + data.length];
        System.arraycopy(exp, 0, values, 0, exp.length);
        System.arraycopy(data, 0, values, exp.length, data.length);

        return (1 == polymod(values));
    }

    private static byte[] createChecksum(byte[] hrp, byte[] data)  {

        final byte[] zeroes = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        byte[] expanded = hrpExpand(hrp);
        byte[] values = new byte[zeroes.length + expanded.length + data.length];

        System.arraycopy(expanded, 0, values, 0, expanded.length);
        System.arraycopy(data, 0, values, expanded.length, data.length);
        System.arraycopy(zeroes, 0, values, expanded.length + data.length, zeroes.length);

        int polymod = polymod(values) ^ 1;
        byte[] ret = new byte[6];
        for(int i = 0; i < ret.length; i++)   {
            ret[i] = (byte)((polymod >> 5 * (5 - i)) & 0x1f);
        }

        return ret;
    }

}