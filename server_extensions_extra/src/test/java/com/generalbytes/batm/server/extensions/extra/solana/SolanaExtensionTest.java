package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolanaExtensionTest {

    private final SolanaExtension solanaExtension = new SolanaExtension();

    @Test
    void testGetName() {
        String name = solanaExtension.getName();

        assertEquals("BATM Solana extension", name);
    }

    @ParameterizedTest
    @EnumSource(CryptoCurrency.class)
    void testCreateAddressValidator(CryptoCurrency cryptocurrency) {
        ICryptoAddressValidator validator = solanaExtension.createAddressValidator(cryptocurrency.code);

        if (cryptocurrency == CryptoCurrency.SOL) {
            assertInstanceOf(SolanaAddressValidator.class, validator);
        } else {
            assertNull(validator);
        }
    }

    @Test
    void testCreateWallet_dummyWallet() {
        String walletLogin = "soldemo:CZK:Gk4PLZ3p6wA4CD3shJ8XvsyhFvABvNPKM9Z8ZBrkDWiB";

        IWallet wallet = solanaExtension.createWallet(walletLogin, null);

        assertInstanceOf(DummyExchangeAndWalletAndSource.class, wallet);
        if (wallet instanceof DummyExchangeAndWalletAndSource dummyWallet) {
            assertEquals("SOL", dummyWallet.getPreferredCryptoCurrency());
            assertEquals("CZK", dummyWallet.getPreferredFiatCurrency());
            assertEquals("Gk4PLZ3p6wA4CD3shJ8XvsyhFvABvNPKM9Z8ZBrkDWiB", dummyWallet.getCryptoAddress("SOL"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "  ",
        "\t",
        "\n",
        ":",
        "::",
        ":::",
        "soldemo",
        "soldemo:CZK",
        "soldemo:CZK:Gk4PLZ3p6wA4CD3shJ8XvsyhFvABvNPKM9Z8ZBrkDWiB:any",
        "btcdemo:CZK:Gk4PLZ3p6wA4CD3shJ8XvsyhFvABvNPKM9Z8ZBrkDWiB"
    })
    void testCreateWallet_dummyWallet_invalidWalletLogin(String walletLogin) {
        IWallet wallet = solanaExtension.createWallet(walletLogin, null);

        assertNull(wallet);
    }

    @Test
    void testCreateRateSource_fixPriceRateSource() {
        String sourceLogin = "solfix:21:CZK";

        IRateSource rateSource = solanaExtension.createRateSource(sourceLogin);

        assertInstanceOf(FixPriceRateSource.class, rateSource);
        assertEquals(BigDecimal.valueOf(21L), rateSource.getExchangeRateLast("SOL", "CZK"));
        assertEquals("CZK", rateSource.getPreferredFiatCurrency());
    }

    @Test
    void testCreateRateSource_fixPriceRateSource_defaultValues() {
        String sourceLogin = "solfix";

        IRateSource rateSource = solanaExtension.createRateSource(sourceLogin);

        assertInstanceOf(FixPriceRateSource.class, rateSource);
        assertEquals(BigDecimal.ZERO, rateSource.getExchangeRateLast("SOL", "USD"));
        assertEquals("USD", rateSource.getPreferredFiatCurrency());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "  ",
        "\t",
        "\n",
        ":",
        "::",
        ":::",
        "btcfix",
        "btcfix:21",
        "btcfix:21:CZK"
    })
    void testCreateRateSource_fixPriceRateSource_invalidSourceLogin(String sourceLogin) {
        IRateSource rateSource = solanaExtension.createRateSource(sourceLogin);

        assertNull(rateSource);
    }

    @Test
    void testGetSupportedCryptoCurrencies() {
        Set<String> supportedCryptocurrencies = solanaExtension.getSupportedCryptoCurrencies();

        assertEquals(Set.of("SOL"), supportedCryptocurrencies);
    }

}