package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.coinutil.Bech32;
import com.generalbytes.batm.server.coinutil.Blech32;
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
            mockedBech32.when(() -> Bech32.decode(address)).thenThrow(new AddressFormatException("Test Exception - address"));

            assertFalse(validator.isAddressValid(address));

            mockedBech32.verify(() -> Bech32.decode(address));
        }
    }

    @ParameterizedTest
    @MethodSource("provideBech32Addresses")
    void testIsAddressValid_unexpectedExceptionBech32(String address) {
        try (MockedStatic<Bech32> mockedBech32 = mockStatic(Bech32.class)) {
            mockedBech32.when(() -> Bech32.decode(address)).thenThrow(new RuntimeException("Test Exception"));

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
                .thenThrow(new AddressFormatException("Test Exception - decodeToBigInteger"));

            assertFalse(validator.isAddressValid(address));

            mockedBase58.verify(() -> Base58.decodeToBigInteger(address));
            mockedBase58.verify(() -> Base58.decodeChecked(address), never());
        }
    }

    @ParameterizedTest
    @MethodSource("provideBase58Addresses")
    void testIsAddressValid_invalidBase58Address_decodeChecked(String address) {
        try (MockedStatic<Base58> mockedBase58 = mockStatic(Base58.class)) {
            mockedBase58.when(() -> Base58.decodeChecked(address)).thenThrow(new AddressFormatException("Test Exception - decodeChecked"));

            assertFalse(validator.isAddressValid(address));

            mockedBase58.verify(() -> Base58.decodeToBigInteger(address));
            mockedBase58.verify(() -> Base58.decodeChecked(address));
        }
    }

    @ParameterizedTest
    @MethodSource("provideBase58Addresses")
    void testIsAddressValid_unexpectedExceptionBase58(String address) {
        try (MockedStatic<Base58> mockedBase58 = mockStatic(Base58.class)) {
            mockedBase58.when(() -> Base58.decodeChecked(address)).thenThrow(new RuntimeException("Test Exception"));

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

    @ParameterizedTest
    @ValueSource(strings = {
        "lq1qq2svkhxgu2qd62ctgyaw0jqgcvda0afz286kx07gy5p8mvujjuxujqkcvg3zh4u44ypf8pu4mvh7grvvsc6ju06pxsueu3h5p",
        "lq1qqwch6wp99rxvqzjqahtkzkgtqpp4ujs3p2qqn4r5rexah80jxutq9aflw5qveu7mctht5h2p39j8z6jnfhfqsn5xy9exg3rz6",
        "lq1qqdzvfs5x2vq7mxss8xmqmwzge0harjh0hv5g39gvalxu98admty7up0lvgg7p2q2rwlsm2yuhs9eqlan59dz28y0y8vhl6uhg",
        "lq1qqdvx9c57pcdu0j8gmgxnhvhgmfx5slc42smmutfw8jl6sm0aj8nt0n3helrqmdn2an59cqqe6yh4s637zzz8w9s79k9dzhu2v",
        "lq1qqd4qqjk2qyfkqnws57d6kr7mj4a75yk0vlulv3nq7qvcwragyhcg9xfdcfsm6hpvjz6av32snm6emaqrvgklxvcefxdnz3h6u",
        "lq1qqdptjyppg69chjwfer3sgs7dxq87rvpfgtv2k7jmh6ng02zpt3wgnnw522r8ytkmfkqm6u97kspyw86vcpwe247hsuqyqgnmd",
        "lq1qq0v4j9wre8aygg8dgywhpju9ys6cvqgscj5e2ykfy7dllvlj7ajzd5lvphhxff5cdyetnj56cfyafah5mgaljr04gyx5at52u"
    })
    void testIsAddressValid_blech32(String address) {
        assertTrue(validator.isAddressValid(address));
    }

    @Test
    void testIsAddressValid_invalidBlech32Address() {
        String address = "lq1Address";
        try (MockedStatic<Blech32> mockedBlech32 = mockStatic(Blech32.class)) {
            mockedBlech32.when(() -> Blech32.decode(address)).thenThrow(new AddressFormatException("Test Exception - address"));

            assertFalse(validator.isAddressValid(address));

            mockedBlech32.verify(() -> Blech32.decode(address));
        }
    }

    @Test
    void testIsAddressValid_unexpectedExceptionBlech32() {
        String address = "lq1Address";
        try (MockedStatic<Blech32> mockedBlech32 = mockStatic(Blech32.class)) {
            mockedBlech32.when(() -> Blech32.decode(address)).thenThrow(new RuntimeException("Test Exception"));

            assertFalse(validator.isAddressValid(address));

            mockedBlech32.verify(() -> Blech32.decode(address));
        }
    }
}