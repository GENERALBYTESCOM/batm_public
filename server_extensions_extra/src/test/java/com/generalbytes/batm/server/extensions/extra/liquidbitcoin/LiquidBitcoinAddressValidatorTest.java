package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.coinutil.Bech32;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

class LiquidBitcoinAddressValidatorTest {

    private LiquidBitcoinAddressValidator validator;

    @BeforeEach
    void setUp() {
        validator = new LiquidBitcoinAddressValidator();
    }

    @Test
    void testIsPaperWalletSupported() {
        assertTrue(validator.isPaperWalletSupported());
    }

    @Test
    void testMustBeBase58Address() {
        assertFalse(validator.mustBeBase58Address());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"\n", "\t", "   "})
    void testIsAddressValid_blankInput(String input) {
        assertFalse(validator.isAddressValid(input));
    }

    private static String[] provideBech32Addresses() {
        return new String[]{
            "lq1address",
            "lQ1address",
            "Lq1address",
            "ctaddress",
            "CTaddress",
            "qaddress",
            "Qaddress",
        };
    }

    @ParameterizedTest
    @MethodSource("provideBech32Addresses")
    void testIsAddressValid_validBech32Address(String address) {
        try (MockedStatic<Bech32> mockedBech32 = mockStatic(Bech32.class)) {
            assertTrue(validator.isAddressValid(address));

            mockedBech32.verify(() -> Bech32.decode(address));
        }
    }

    @ParameterizedTest
    @MethodSource("provideBech32Addresses")
    void testIsAddressValid_invalidBech32Address(String address) {
        try (MockedStatic<Bech32> mockedBech32 = mockStatic(Bech32.class)) {
            mockedBech32.when(() -> Bech32.decode(address)).thenThrow(new AddressFormatException("Text Exception - address"));

            assertFalse(validator.isAddressValid(address));

            mockedBech32.verify(() -> Bech32.decode(address));
        }
    }

    @ParameterizedTest
    @MethodSource("provideBech32Addresses")
    void testIsAddressValid_unexpectedExceptionBech32(String address) {
        try (MockedStatic<Bech32> mockedBech32 = mockStatic(Bech32.class)) {
            mockedBech32.when(() -> Bech32.decode(address)).thenThrow(new RuntimeException("Text Exception"));

            assertFalse(validator.isAddressValid(address));

            mockedBech32.verify(() -> Bech32.decode(address));
        }
    }

    private static String[] provideBase58Addresses() {
        return new String[]{
            "Haddress",
            "Xaddress",
            "Vaddress",
        };
    }

    @ParameterizedTest
    @MethodSource("provideBase58Addresses")
    void testIsAddressValid_validBase58Address(String address) {
        try (MockedStatic<Base58> mockedBase58 = mockStatic(Base58.class)) {
            assertTrue(validator.isAddressValid(address));

            mockedBase58.verify(() -> Base58.decodeToBigInteger(address));
            mockedBase58.verify(() -> Base58.decodeChecked(address));
        }
    }

    @ParameterizedTest
    @MethodSource("provideBase58Addresses")
    void testIsAddressValid_invalidBase58Address_decodeToBigInteger(String address) {
        try (MockedStatic<Base58> mockedBase58 = mockStatic(Base58.class)) {
            mockedBase58.when(() -> Base58.decodeToBigInteger(address))
                .thenThrow(new AddressFormatException("Text Exception - decodeToBigInteger"));

            assertFalse(validator.isAddressValid(address));

            mockedBase58.verify(() -> Base58.decodeToBigInteger(address));
            mockedBase58.verify(() -> Base58.decodeChecked(address), never());
        }
    }

    @ParameterizedTest
    @MethodSource("provideBase58Addresses")
    void testIsAddressValid_invalidBase58Address_decodeChecked(String address) {
        try (MockedStatic<Base58> mockedBase58 = mockStatic(Base58.class)) {
            mockedBase58.when(() -> Base58.decodeChecked(address)).thenThrow(new AddressFormatException("Text Exception - decodeChecked"));

            assertFalse(validator.isAddressValid(address));

            mockedBase58.verify(() -> Base58.decodeToBigInteger(address));
            mockedBase58.verify(() -> Base58.decodeChecked(address));
        }
    }

    @ParameterizedTest
    @MethodSource("provideBase58Addresses")
    void testIsAddressValid_unexpectedExceptionBase58(String address) {
        try (MockedStatic<Base58> mockedBase58 = mockStatic(Base58.class)) {
            mockedBase58.when(() -> Base58.decodeChecked(address)).thenThrow(new RuntimeException("Text Exception"));

            assertFalse(validator.isAddressValid(address));

            mockedBase58.verify(() -> Base58.decodeToBigInteger(address));
            mockedBase58.verify(() -> Base58.decodeChecked(address));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        // Lowercase H, X and V are not valid for Base 58
        "haddress",
        "xaddress",
        "vaddress",
        "anyaddress",
    })
    void testIsAddressValid_unknownAddress(String address) {
        assertFalse(validator.isAddressValid(address));
    }
}