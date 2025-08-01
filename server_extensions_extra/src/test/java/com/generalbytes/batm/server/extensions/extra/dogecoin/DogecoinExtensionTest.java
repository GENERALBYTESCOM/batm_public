package com.generalbytes.batm.server.extensions.extra.dogecoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IWallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class DogecoinExtensionTest {

    private DogecoinExtension extension;

    @BeforeEach
    void setUp() {
        extension = new DogecoinExtension();
    }

    private static String[] provideInvalidLogin() {
        return new String[]{
                null,
                "",
                "  ",
                "\t",
                "\n",
                // Invalid DOGE demo parameters
                "dogedemo",
                "dogedemo:",
                "dogedemo::",
                "dogedemo::address",
                "dogedemo:CZK:",
                "dogedemo:CZK",
                // Unknown wallet
                "unknown:CZK:address",
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLogin")
    void testCreateWallet_invalidLogin(String invalidLogin) {
        assertNull(extension.createWallet(invalidLogin, null));
    }

    @Test
    void testCreateWallet_demo() {
        IWallet wallet = extension.createWallet("dogedemo:CZK:address", null);

        assertInstanceOf(DummyExchangeAndWalletAndSource.class, wallet);
        DummyExchangeAndWalletAndSource dummyWallet = (DummyExchangeAndWalletAndSource) wallet;
        assertEquals("address", dummyWallet.getCryptoAddress(CryptoCurrency.DOGE.getCode()));
        assertEquals(Set.of(CryptoCurrency.DOGE.getCode()), dummyWallet.getCryptoCurrencies());
        assertEquals(Set.of("CZK"), dummyWallet.getFiatCurrencies());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLogin")
    void testCreateExchange_invalidLogin(String invalidLogin) {
        assertNull(extension.createExchange(invalidLogin));
    }

    @Test
    void testCreateExchange_demo() {
        IExchange exchange = extension.createExchange("dogedemo:CZK:address");

        assertInstanceOf(DummyExchangeAndWalletAndSource.class, exchange);
        DummyExchangeAndWalletAndSource dummyExchange = (DummyExchangeAndWalletAndSource) exchange;
        assertEquals("address", dummyExchange.getCryptoAddress(CryptoCurrency.DOGE.getCode()));
        assertEquals(Set.of(CryptoCurrency.DOGE.getCode()), dummyExchange.getCryptoCurrencies());
        assertEquals(Set.of("CZK"), dummyExchange.getFiatCurrencies());
    }
}