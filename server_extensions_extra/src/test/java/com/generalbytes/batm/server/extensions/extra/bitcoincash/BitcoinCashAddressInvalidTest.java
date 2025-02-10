package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BitcoinCashAddressInvalidTest {
    private static final BitcoinCashAddressValidator BCH_ADDRESS_VALIDATOR = new BitcoinCashAddressValidator();
    private static final SlpAddressValidator SLP_ADDRESS_VALIDATOR = new SlpAddressValidator();

    private void test(String in) throws AddressFormatException {
        assertFalse(BCH_ADDRESS_VALIDATOR.isAddressValid(in), "bitcoincash invalid");
        assertFalse(SLP_ADDRESS_VALIDATOR.isAddressValid(in), "simpleledger invalid");
        BitcoinCashAddress.valueOf(in);
    }

    @Test
    void nullInput() {
        assertThrows(AddressFormatException.class, () -> test(null));
    }


    @Test
    void emptyInput() {
        assertThrows(AddressFormatException.class, () -> test(""));
    }

    @Test
    void invalidInput1() {
        assertThrows(AddressFormatException.class, () -> test("1"));
    }

    @Test
    void invalidInput1111() {
        assertThrows(AddressFormatException.class, () -> test("1111111111111111111111111111111111"));
    }

    @Test
    void invalidInput3() {
        assertThrows(AddressFormatException.class, () -> test("3"));
    }

    @Test
    void invalidInput3333() {
        assertThrows(AddressFormatException.class, () -> test("3333333333333333333333333333333333"));
    }

    @Test
    void invalidInput0() {
        assertThrows(AddressFormatException.class, () -> test("0"));
    }

    @Test
    void invalidInputCol() {
        assertThrows(AddressFormatException.class, () -> test(":"));
    }


    @Test
    void invalidInputColQqqq() {
        assertThrows(AddressFormatException.class, () -> test("bitcoincash:qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"));
    }

    @Test
    void invalidInputQqqq() {
        assertThrows(AddressFormatException.class, () -> test("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"));
    }

    @Test
    void invalidInputColPppp() {
        assertThrows(AddressFormatException.class, () -> test("bitcoincash:pppppppppppppppppppppppppppppppppppppppppp"));
    }

    @Test
    void invalidInputPppp() {
        assertThrows(AddressFormatException.class, () -> test("pppppppppppppppppppppppppppppppppppppppppp"));
    }

    @Test
    void invalidInputSlpPppp() {
        assertThrows(AddressFormatException.class, () -> test("simpleledger:pppppppppppppppppppppppppppppppppppppppppp"));
    }

    @Test
    void invalidInputSlpQqqq() {
        assertThrows(AddressFormatException.class, () -> test("simpleledger:qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"));
    }

    @Test
    void invalidInputSlpE() {
        assertThrows(AddressFormatException.class, () -> test("simpleledger:"));
    }
}