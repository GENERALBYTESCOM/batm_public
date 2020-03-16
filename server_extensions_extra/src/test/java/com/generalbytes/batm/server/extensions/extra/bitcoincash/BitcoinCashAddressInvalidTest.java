package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import org.junit.Assert;
import org.junit.Test;

public class BitcoinCashAddressInvalidTest {
    private static final BitcoinCashAddressValidator BCH_ADDRESS_VALIDATOR = new BitcoinCashAddressValidator();
    private static final SlpAddressValidator SLP_ADDRESS_VALIDATOR = new SlpAddressValidator();

    private void test(String in) throws AddressFormatException {
        Assert.assertFalse("bitcoincash invalid", BCH_ADDRESS_VALIDATOR.isAddressValid(in));
        Assert.assertFalse("simpleledger invalid", SLP_ADDRESS_VALIDATOR.isAddressValid(in));
        BitcoinCashAddress.valueOf(in);
    }

    @Test(expected = AddressFormatException.class)
    public void nullInput() throws AddressFormatException {
        test(null);
    }


    @Test(expected = AddressFormatException.class)
    public void emptyInput() throws AddressFormatException {
        test("");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInput1() throws AddressFormatException {
        test("1");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInput1111() throws AddressFormatException {
        test("1111111111111111111111111111111111");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInput3() throws AddressFormatException {
        test("3");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInput3333() throws AddressFormatException {
        test("3333333333333333333333333333333333");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInput0() throws AddressFormatException {
        test("0");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInputCol() throws AddressFormatException {
        test(":");
    }


    @Test(expected = AddressFormatException.class)
    public void invalidInputColQqqq() throws AddressFormatException {
        test("bitcoincash:qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInputQqqq() throws AddressFormatException {
        test("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInputColPppp() throws AddressFormatException {
        test("bitcoincash:pppppppppppppppppppppppppppppppppppppppppp");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInputPppp() throws AddressFormatException {
        test("pppppppppppppppppppppppppppppppppppppppppp");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInputSlpPppp() throws AddressFormatException {
        test("simpleledger:pppppppppppppppppppppppppppppppppppppppppp");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInputSlpQqqq() throws AddressFormatException {
        test("simpleledger:qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
    }

    @Test(expected = AddressFormatException.class)
    public void invalidInputSlpE() throws AddressFormatException {
        test("simpleledger:");
    }
}