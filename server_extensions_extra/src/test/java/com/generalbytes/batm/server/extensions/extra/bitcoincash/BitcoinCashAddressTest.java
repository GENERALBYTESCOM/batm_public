package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class BitcoinCashAddressTest {
    private static final BitcoinCashAddressValidator BCH_ADDRESS_VALIDATOR = new BitcoinCashAddressValidator();
    private static final SlpAddressValidator SLP_ADDRESS_VALIDATOR = new SlpAddressValidator();
    private final BitcoinCashAddress input;

    @Parameterized.Parameters
    public static Collection getTestData() throws AddressFormatException {
        return Arrays.asList(new Object[][]{
            {new BitcoinCashAddress("1BpEi6DfDAUFd7GtittLSdBeYJvcoaVggu", "qpm2qsznhks23z7629mms6s4cwef74vcwvy22gdx6a", "qpm2qsznhks23z7629mms6s4cwef74vcwvg3pncxyr")},
            {new BitcoinCashAddress("1KXrWXciRDZUpQwQmuM1DbwsKDLYAYsVLR", "qr95sy3j9xwd2ap32xkykttr4cvcu7as4y0qverfuy", "qr95sy3j9xwd2ap32xkykttr4cvcu7as4yrm8zkfz6")},
            {new BitcoinCashAddress("16w1D5WRVKJuZUsSRzdLp9w3YGcgoxDXb", "qqq3728yw0y47sqn6l2na30mcw6zm78dzqre909m2r", "qqq3728yw0y47sqn6l2na30mcw6zm78dzq0zw5sm5a")},
            {new BitcoinCashAddress("3CWFddi6m4ndiGyKqzYvsFYagqDLPVMTzC", "ppm2qsznhks23z7629mms6s4cwef74vcwvn0h829pq", "ppm2qsznhks23z7629mms6s4cwef74vcwvl5uul9l7")},
            {new BitcoinCashAddress("3LDsS579y7sruadqu11beEJoTjdFiFCdX4", "pr95sy3j9xwd2ap32xkykttr4cvcu7as4yc93ky28e", "pr95sy3j9xwd2ap32xkykttr4cvcu7as4y576d32e8")},
            {new BitcoinCashAddress("31nwvkZwyPdgzjBJZXfDmSWsC4ZLKpYyUw", "pqq3728yw0y47sqn6l2na30mcw6zm78dzq5ucqzc37", "pqq3728yw0y47sqn6l2na30mcw6zm78dzqc8nmhc0q")},
        });
    }

    // @Parameterized.Parameters annotated method results are passed here
    public BitcoinCashAddressTest(BitcoinCashAddress input) {
        this.input = input;
    }

    @Test
    public void convert() throws AddressFormatException {
        assertEquals(input, BitcoinCashAddress.valueOf(input.getLegacy()));
        assertEquals(input, BitcoinCashAddress.valueOf(input.getBitcoincash(true)));
        assertEquals(input, BitcoinCashAddress.valueOf(input.getBitcoincash(false)));
        assertEquals(input, BitcoinCashAddress.valueOf(input.getSimpleledger(true)));
        assertEquals(input, BitcoinCashAddress.valueOf(input.getSimpleledger(false)));
    }

    private void assertEquals(BitcoinCashAddress expected, BitcoinCashAddress actual) {
        Assert.assertEquals("legacy", expected.getLegacy(), actual.getLegacy());
        Assert.assertEquals("bitcoincash", expected.getBitcoincash(true), actual.getBitcoincash(true));
        Assert.assertEquals("bitcoincash", expected.getBitcoincash(false), actual.getBitcoincash(false));
        Assert.assertEquals("simpleledger", expected.getSimpleledger(true), actual.getSimpleledger(true));
        Assert.assertEquals("simpleledger", expected.getSimpleledger(false), actual.getSimpleledger(false));
        Assert.assertTrue("bitcoincash valid", BCH_ADDRESS_VALIDATOR.isAddressValid(actual.getBitcoincash(false)));
        Assert.assertTrue("simpleledger valid", SLP_ADDRESS_VALIDATOR.isAddressValid(actual.getSimpleledger(false)));
    }
}