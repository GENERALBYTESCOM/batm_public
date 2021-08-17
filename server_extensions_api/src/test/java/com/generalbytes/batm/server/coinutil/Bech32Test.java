package com.generalbytes.batm.server.coinutil;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

public class Bech32Test {

    ///// BIP-173 test vectors /////
    // https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki

    @Test
    public void decodeValidAddresses1() throws AddressFormatException {
        testValidAddress("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4", "0014751e76e8199196d454941c45d1b3a323f1433bd6");
    }

    @Test
    public void decodeValidAddresses2() throws AddressFormatException {
        testValidAddress("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7", "00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262");
    }

    @Test
    public void decodeValidAddresses3() throws AddressFormatException {
        testValidAddress("bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k7grplx", "5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6");
    }

    @Test
    public void decodeValidAddresses4() throws AddressFormatException {
        testValidAddress("BC1SW50QA3JX3S", "6002751e");
    }

    @Test
    public void decodeValidAddresses5() throws AddressFormatException {
        testValidAddress("bc1zw508d6qejxtdg4y5r3zarvaryvg6kdaj", "5210751e76e8199196d454941c45d1b3a323");
    }

    @Test
    public void decodeValidAddresses6() throws AddressFormatException {
        testValidAddress("tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy", "0020000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433");
    }

    private void testValidAddress(String address, String hexData) throws AddressFormatException {
        Bech32.Bech32Data decoded = Bech32.decodeAddress(address);
        Assert.assertEquals("HRP of " + address, address.substring(0, 2).toLowerCase(), decoded.hrp);
        String expected = hexData.substring(4);
        String actual = toHex(decoded.data);
        Assert.assertEquals("Data of " + address, expected, actual);
    }

    ///// The following string are not valid Bech32 /////

    // HRP character out of range
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid01() throws AddressFormatException {
        Bech32.decode("\u00201nwldj5");
    }

    // HRP character out of range
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid02() throws AddressFormatException {
        Bech32.decode("\u007F1axkwrx");
    }

    // HRP character out of range
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid03() throws AddressFormatException {
        Bech32.decode("\u00801eym55h");
    }

    // overall max length exceeded
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid04() throws AddressFormatException {
        Bech32.decode("an84characterslonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1569pvx");
    }

    // No separator character
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid05() throws AddressFormatException {
        Bech32.decode("pzry9x0s0muk");
    }

    // Empty HRP
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid06() throws AddressFormatException {
        Bech32.decode("1pzry9x0s0muk");
    }

    // Invalid data character
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid07() throws AddressFormatException {
        Bech32.decode("x1b4n0q5v");
    }

    // Too short checksum
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid08() throws AddressFormatException {
        Bech32.decode("li1dgmt3");
    }

    // Invalid character in checksum
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid09() throws AddressFormatException {
        Bech32.decode("de1lg7wt\u00FF");
    }

    // checksum calculated with uppercase form of HRP
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid10() throws AddressFormatException {
        Bech32.decode("A1G7SGD8");
    }

    // empty HRP
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid11() throws AddressFormatException {
        Bech32.decode("10a06t8");
    }

    // empty HRP
    @Test(expected = AddressFormatException.class)
    public void decodeInvalid12() throws AddressFormatException {
        Bech32.decode("1qzzfhee");
    }


    ///// The following list gives invalid segwit addresses /////

    // Invalid human-readable part
    @Ignore("not checked")
    @Test(expected = AddressFormatException.class)
    public void decodeInvalidAddr01() throws AddressFormatException {
        Bech32.decode("tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty");
    }

    // Invalid checksum
    @Test(expected = AddressFormatException.class)
    public void decodeInvalidAddr02() throws AddressFormatException {
        Bech32.decode("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t5");
    }

    // Invalid witness version
    @Ignore("not checked")
    @Test(expected = AddressFormatException.class)
    public void decodeInvalidAddr03() throws AddressFormatException {
        Bech32.decode("BC13W508D6QEJXTDG4Y5R3ZARVARY0C5XW7KN40WF2");
    }

    // Invalid program length
    @Ignore("not checked")
    @Test(expected = AddressFormatException.class)
    public void decodeInvalidAddr04() throws AddressFormatException {
        Bech32.decode("bc1rw5uspcuh");
    }

    // Invalid program length
    @Ignore("not checked")
    @Test(expected = AddressFormatException.class)
    public void decodeInvalidAddr05() throws AddressFormatException {
        Bech32.decode("bc10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5rljs90");
    }

    // Invalid program length for witness version 0 (per BIP141)
    @Ignore("not checked")
    @Test(expected = AddressFormatException.class)
    public void decodeInvalidAddr06() throws AddressFormatException {
        Bech32.decode("BC1QR508D6QEJXTDG4Y5R3ZARVARYV98GJ9P");
    }

    // Mixed case
    @Test(expected = AddressFormatException.class)
    public void decodeInvalidAddr07() throws AddressFormatException {
        Bech32.decode("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sL5k7");
    }

    // zero padding of more than 4 bits
    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidAddr08() throws AddressFormatException {
        Bech32.decodeAddress("bc1zw508d6qejxtdg4y5r3zarvaryvqyzf3du");
    }

    // Non-zero padding in 8-to-5 conversion
    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidAddr09() throws AddressFormatException {
        Bech32.decodeAddress("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pjxtptv");
    }

    // Empty data section
    @Test(expected = IllegalArgumentException.class)
    public void decodeInvalidAddr10() throws AddressFormatException {
        Bech32.decodeAddress("bc1gmk9yu");
    }


    ///// LNURL /////
    // test data from https://github.com/fiatjaf/lnurl-rfc
    public static final String LNURL = "LNURL1DP68GURN8GHJ7UM9WFMXJCM99E3K7MF0V9CXJ0M385EKVCENXC6R2C35XVUKXEFCV5MKVV34X5EKZD3EV56NYD3HXQURZEPEXEJXXEPNXSCRVWFNV9NXZCN9XQ6XYEFHVGCXXCMYXYMNSERXFQ5FNS";
    public static final String LNURL_DECODED = "https://service.com/api?q=3fc3645b439ce8e7f2553a69e5267081d96dcd340693afabe04be7b0ccd178df";

    @Test
    public void decodeLnurl() throws AddressFormatException {
        Assert.assertEquals(LNURL_DECODED, Bech32.decodeString("lnurl", LNURL));
    }

    @Test(expected = AddressFormatException.class)
    public void decodeLnurlEmpty() throws AddressFormatException {
        Bech32.decodeString("lnurl", "");
    }

    @Test(expected = NullPointerException.class)
    public void decodeLnurlNull() throws AddressFormatException {
        Bech32.decodeString("lnurl", null);
    }

    @Test(expected = AddressFormatException.class)
    public void decodeLnurlWrongHRP() throws AddressFormatException {
        Bech32.decodeString("bc", LNURL);
    }

    @Test
    public void encodeLnurl() {
        Assert.assertEquals(LNURL.toLowerCase(), Bech32.encodeString("lnurl", LNURL_DECODED));
        Assert.assertEquals(LNURL.toLowerCase(), Bech32.encodeString("lnURL", LNURL_DECODED));
    }

    @Test
    public void encodeLnurlEmpty() {
        Assert.assertEquals("lnurl13myvsu", Bech32.encodeString("lnurl", ""));
    }

    @Test(expected = NullPointerException.class)
    public void encodeLnurlNull() {
        Assert.assertEquals("", Bech32.encodeString("lnurl", null));
    }

    @Test(expected = NullPointerException.class)
    public void encodeLnurlNullHrp() {
        Assert.assertEquals("", Bech32.encodeString(null, LNURL_DECODED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeLnurlEmptyHrp() {
        Assert.assertEquals("", Bech32.encodeString("", LNURL_DECODED));
    }

    @Test
    public void encodeDecodeRandom() throws AddressFormatException {
        Random random = new Random(0L);
        for (int stringLength = 100; stringLength < 350; stringLength += 11) {
            String generatedString = randomString(random, stringLength);
            String encoded = Bech32.encodeString("test", generatedString);
            String decoded = Bech32.decodeString("test", encoded);
            Assert.assertEquals(generatedString, decoded);
        }
    }

    private String randomString(Random random, int length) {
        return random.ints(0, 128)
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    private static final String HEXES = "0123456789abcdef";

    public static String toHex(byte[] value) {
        final StringBuilder hex = new StringBuilder(2 * value.length);
        for (final byte b : value) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
}