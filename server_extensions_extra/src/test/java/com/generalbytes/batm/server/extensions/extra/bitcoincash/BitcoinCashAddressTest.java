package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BitcoinCashAddressTest {
    private static final BitcoinCashAddressValidator BCH_ADDRESS_VALIDATOR = new BitcoinCashAddressValidator();
    private static final SlpAddressValidator SLP_ADDRESS_VALIDATOR = new SlpAddressValidator();

    public static Object[][] getTestData() throws AddressFormatException {
        return new Object[][]{
            {new BitcoinCashAddress("1BpEi6DfDAUFd7GtittLSdBeYJvcoaVggu", "qpm2qsznhks23z7629mms6s4cwef74vcwvy22gdx6a", "qpm2qsznhks23z7629mms6s4cwef74vcwvg3pncxyr")},
            {new BitcoinCashAddress("1KXrWXciRDZUpQwQmuM1DbwsKDLYAYsVLR", "qr95sy3j9xwd2ap32xkykttr4cvcu7as4y0qverfuy", "qr95sy3j9xwd2ap32xkykttr4cvcu7as4yrm8zkfz6")},
            {new BitcoinCashAddress("16w1D5WRVKJuZUsSRzdLp9w3YGcgoxDXb", "qqq3728yw0y47sqn6l2na30mcw6zm78dzqre909m2r", "qqq3728yw0y47sqn6l2na30mcw6zm78dzq0zw5sm5a")},
            {new BitcoinCashAddress("3CWFddi6m4ndiGyKqzYvsFYagqDLPVMTzC", "ppm2qsznhks23z7629mms6s4cwef74vcwvn0h829pq", "ppm2qsznhks23z7629mms6s4cwef74vcwvl5uul9l7")},
            {new BitcoinCashAddress("3LDsS579y7sruadqu11beEJoTjdFiFCdX4", "pr95sy3j9xwd2ap32xkykttr4cvcu7as4yc93ky28e", "pr95sy3j9xwd2ap32xkykttr4cvcu7as4y576d32e8")},
            {new BitcoinCashAddress("31nwvkZwyPdgzjBJZXfDmSWsC4ZLKpYyUw", "pqq3728yw0y47sqn6l2na30mcw6zm78dzq5ucqzc37", "pqq3728yw0y47sqn6l2na30mcw6zm78dzqc8nmhc0q")},
        };
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void convert(BitcoinCashAddress input) throws AddressFormatException {
        assertValues(input, BitcoinCashAddress.valueOf(input.getLegacy()));
        assertValues(input, BitcoinCashAddress.valueOf(input.getBitcoincash(true)));
        assertValues(input, BitcoinCashAddress.valueOf(input.getBitcoincash(false)));
        assertValues(input, BitcoinCashAddress.valueOf(input.getSimpleledger(true)));
        assertValues(input, BitcoinCashAddress.valueOf(input.getSimpleledger(false)));
    }

    private void assertValues(BitcoinCashAddress expected, BitcoinCashAddress actual) {
        assertEquals(expected.getLegacy(), actual.getLegacy(), "legacy");
        assertEquals(expected.getBitcoincash(true), actual.getBitcoincash(true), "bitcoincash");
        assertEquals(expected.getBitcoincash(false), actual.getBitcoincash(false), "bitcoincash");
        assertEquals(expected.getSimpleledger(true), actual.getSimpleledger(true), "simpleledger");
        assertEquals(expected.getSimpleledger(false), actual.getSimpleledger(false), "simpleledger");
        assertTrue(BCH_ADDRESS_VALIDATOR.isAddressValid(actual.getBitcoincash(false)), "bitcoincash valid");
        assertTrue(SLP_ADDRESS_VALIDATOR.isAddressValid(actual.getSimpleledger(false)), "simpleledger valid");
    }
}