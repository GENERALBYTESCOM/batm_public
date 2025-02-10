package com.generalbytes.batm.server.coinutil;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Bech32Test {

    ///// BIP-173 test vectors /////
    // https://github.com/bitcoin/bips/blob/master/bip-0173.mediawiki

    @Test
    void decodeValidAddresses1() throws AddressFormatException {
        testValidAddress("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4", "0014751e76e8199196d454941c45d1b3a323f1433bd6");
    }

    @Test
    void decodeValidAddresses2() throws AddressFormatException {
        testValidAddress("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7", "00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262");
    }

    @Test
    void decodeValidAddresses3() throws AddressFormatException {
        testValidAddress("bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k7grplx", "5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6");
    }

    @Test
    void decodeValidAddresses4() throws AddressFormatException {
        testValidAddress("BC1SW50QA3JX3S", "6002751e");
    }

    @Test
    void decodeValidAddresses5() throws AddressFormatException {
        testValidAddress("bc1zw508d6qejxtdg4y5r3zarvaryvg6kdaj", "5210751e76e8199196d454941c45d1b3a323");
    }

    @Test
    void decodeValidAddresses6() throws AddressFormatException {
        testValidAddress("tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy", "0020000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433");
    }

    private void testValidAddress(String address, String hexData) throws AddressFormatException {
        Bech32.Bech32Data decoded = Bech32.decodeAddress(address);
        assertEquals(address.substring(0, 2).toLowerCase(), decoded.hrp, "HRP of " + address);
        String expected = hexData.substring(4);
        String actual = toHex(decoded.data);
        assertEquals(expected, actual, "Data of " + address);
    }

    ///// The following string are not valid Bech32 /////

    // HRP character out of range
    @Test
    void decodeInvalid01() {
        assertThrows(AddressFormatException.class, () -> Bech32.decode("\u00201nwldj5"));
    }

    // HRP character out of range
    @Test
    void decodeInvalid02() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("\u007F1axkwrx"));
    }

    // HRP character out of range
    @Test
    void decodeInvalid03() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("\u00801eym55h"));
    }

    // overall max length exceeded
    @Test
    void decodeInvalid04() {
       assertThrows(AddressFormatException.class,
            () -> Bech32.decode("an84characterslonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1569pvx"));
    }

    // No separator character
    @Test
    void decodeInvalid05() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("pzry9x0s0muk"));
    }

    // Empty HRP
    @Test
    void decodeInvalid06() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("1pzry9x0s0muk"));
    }

    // Invalid data character
    @Test
    void decodeInvalid07() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("x1b4n0q5v"));
    }

    // Too short checksum
    @Test
    void decodeInvalid08() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("li1dgmt3"));
    }

    // Invalid character in checksum
    @Test
    void decodeInvalid09() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("de1lg7wt\u00FF"));
    }

    // checksum calculated with uppercase form of HRP
    @Test
    void decodeInvalid10() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("A1G7SGD8"));
    }

    // empty HRP
    @Test
    void decodeInvalid11() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("10a06t8"));
    }

    // empty HRP
    @Test
    void decodeInvalid12() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("1qzzfhee"));
    }


    ///// The following list gives invalid segwit addresses /////

    // Invalid human-readable part
    @Disabled("not checked")
    @Test
    void decodeInvalidAddr01() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty"));
    }

    // Invalid checksum
    @Test
    void decodeInvalidAddr02() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t5"));
    }

    // Invalid witness version
    @Disabled("not checked")
    @Test
    void decodeInvalidAddr03() {
        assertThrows(AddressFormatException.class, () -> Bech32.decode("BC13W508D6QEJXTDG4Y5R3ZARVARY0C5XW7KN40WF2"));
    }

    // Invalid program length
    @Disabled("not checked")
    @Test
    void decodeInvalidAddr04() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("bc1rw5uspcuh"));
    }

    // Invalid program length
    @Disabled("not checked")
    @Test
    void decodeInvalidAddr05() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("bc10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5rljs90"));
    }

    // Invalid program length for witness version 0 (per BIP141)
    @Disabled("not checked")
    @Test
    void decodeInvalidAddr06() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("BC1QR508D6QEJXTDG4Y5R3ZARVARYV98GJ9P"));
    }

    // Mixed case
    @Test
    void decodeInvalidAddr07() {
       assertThrows(AddressFormatException.class, () -> Bech32.decode("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sL5k7"));
    }

    // zero padding of more than 4 bits
    @Test
    void decodeInvalidAddr08() {
       assertThrows(IllegalArgumentException.class, () -> Bech32.decodeAddress("bc1zw508d6qejxtdg4y5r3zarvaryvqyzf3du"));
    }

    // Non-zero padding in 8-to-5 conversion
    @Test
    void decodeInvalidAddr09() {
       assertThrows(IllegalArgumentException.class, () -> Bech32.decodeAddress("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pjxtptv"));
    }

    // Empty data section
    @Test
    void decodeInvalidAddr10() {
       assertThrows(IllegalArgumentException.class, () -> Bech32.decodeAddress("bc1gmk9yu"));
    }


    ///// LNURL /////
    // test data from https://github.com/fiatjaf/lnurl-rfc
    public static final String LNURL = "LNURL1DP68GURN8GHJ7UM9WFMXJCM99E3K7MF0V9CXJ0M385EKVCENXC6R2C35XVUKXEFCV5MKVV34X5EKZD3EV56NYD3HXQURZEPEXEJXXEPNXSCRVWFNV9NXZCN9XQ6XYEFHVGCXXCMYXYMNSERXFQ5FNS";
    public static final String LNURL_DECODED = "https://service.com/api?q=3fc3645b439ce8e7f2553a69e5267081d96dcd340693afabe04be7b0ccd178df";

    @Test
    void decodeLnurl() throws AddressFormatException {
        assertEquals(LNURL_DECODED, Bech32.decodeString("lnurl", LNURL));
    }

    @Test
    void decodeLnurlEmpty() {
       assertThrows(AddressFormatException.class, () -> Bech32.decodeString("lnurl", ""));
    }

    @Test
    void decodeLnurlNull() {
       assertThrows(NullPointerException.class, () -> Bech32.decodeString("lnurl", null));
    }

    @Test
    void decodeLnurlWrongHRP() {
       assertThrows(AddressFormatException.class, () -> Bech32.decodeString("bc", LNURL));
    }

    @Test
    void encodeLnurl() {
        assertEquals(LNURL.toLowerCase(), Bech32.encodeString("lnurl", LNURL_DECODED));
        assertEquals(LNURL.toLowerCase(), Bech32.encodeString("lnURL", LNURL_DECODED));
    }

    @Test
    void encodeLnurlEmpty() {
        assertEquals("lnurl13myvsu", Bech32.encodeString("lnurl", ""));
    }

    @Test
    void encodeLnurlNull() {
        assertThrows(NullPointerException.class, () -> Bech32.encodeString("lnurl", null));
    }

    @Test
    void encodeLnurlNullHrp() {
        assertThrows(NullPointerException.class, () -> Bech32.encodeString(null, LNURL_DECODED));
    }

    @Test
    void encodeLnurlEmptyHrp() {
        assertThrows(IllegalArgumentException.class, () -> Bech32.encodeString("", LNURL_DECODED));
    }

    @Test
    void encodeDecodeRandom() throws AddressFormatException {
        Random random = new Random(0L);
        for (int stringLength = 100; stringLength < 350; stringLength += 11) {
            String generatedString = randomString(random, stringLength);
            String encoded = Bech32.encodeString("test", generatedString);
            String decoded = Bech32.decodeString("test", encoded);
            assertEquals(generatedString, decoded);
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