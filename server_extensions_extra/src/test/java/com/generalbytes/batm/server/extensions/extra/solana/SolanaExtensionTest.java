package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SolanaExtensionTest {

    private final SolanaExtension solanaExtension = new SolanaExtension();

    @Test
    void testGetName() {
        String name = solanaExtension.getName();

        assertEquals("BATM Solana extension", name);
    }

    @ParameterizedTest
    @EnumSource(value = CryptoCurrency.class, mode = EnumSource.Mode.INCLUDE, names = {"SOL", "USDCSOL"})
    void testCreateAddressValidator(CryptoCurrency cryptocurrency) {
        ICryptoAddressValidator validator = solanaExtension.createAddressValidator(cryptocurrency.code);

        assertInstanceOf(SolanaAddressValidator.class, validator);
    }

    @ParameterizedTest
    @EnumSource(value = CryptoCurrency.class, mode = EnumSource.Mode.EXCLUDE, names = {"SOL", "USDCSOL"})
    void testCreateAddressValidator_unsupported(CryptoCurrency cryptocurrency) {
        ICryptoAddressValidator validator = solanaExtension.createAddressValidator(cryptocurrency.code);

        assertNull(validator);
    }

    @ParameterizedTest
    @EnumSource(value = CryptoCurrency.class, mode = EnumSource.Mode.INCLUDE, names = {"SOL", "USDCSOL"})
    void testCreatePaperWalletGenerator(CryptoCurrency cryptocurrency) {
        IPaperWalletGenerator paperWalletGenerator = solanaExtension.createPaperWalletGenerator(cryptocurrency.code);

        assertInstanceOf(SolanaWalletGenerator.class, paperWalletGenerator);
    }

    @ParameterizedTest
    @EnumSource(value = CryptoCurrency.class, mode = EnumSource.Mode.EXCLUDE, names = {"SOL", "USDCSOL"})
    void testCreatePaperWalletGenerator_unsupported(CryptoCurrency cryptocurrency) {
        IPaperWalletGenerator paperWalletGenerator = solanaExtension.createPaperWalletGenerator(cryptocurrency.code);

        assertNull(paperWalletGenerator);
    }

    private static Stream<Arguments> testCreateWallet_dummyWallet_arguments() {
        return Stream.of(
            arguments("soldemo:CZK:Gk4PLZ3p6wA4CD3shJ8XvsyhFvABvNPKM9Z8ZBrkDWiB", "SOL"),
            arguments("usdcsoldemo:CZK:Gk4PLZ3p6wA4CD3shJ8XvsyhFvABvNPKM9Z8ZBrkDWiB", "USDCSOL")
        );
    }

    @ParameterizedTest
    @MethodSource("testCreateWallet_dummyWallet_arguments")
    void testCreateWallet_dummyWallet(String walletLogin, String expectedWalletCryptocurrency) {
        IWallet wallet = solanaExtension.createWallet(walletLogin, null);

        assertInstanceOf(DummyExchangeAndWalletAndSource.class, wallet);
        if (wallet instanceof DummyExchangeAndWalletAndSource dummyWallet) {
            assertEquals(expectedWalletCryptocurrency, dummyWallet.getPreferredCryptoCurrency());
            assertEquals("CZK", dummyWallet.getPreferredFiatCurrency());
            assertEquals("Gk4PLZ3p6wA4CD3shJ8XvsyhFvABvNPKM9Z8ZBrkDWiB", dummyWallet.getCryptoAddress(expectedWalletCryptocurrency));
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

    private static Stream<Arguments> testCreateRateSource_fixPriceRateSource_arguments() {
        return Stream.of(
            arguments("solfix:21:CZK", "SOL"),
            arguments("usdcsolfix:21:CZK", "USDCSOL")
        );
    }

    @ParameterizedTest
    @MethodSource("testCreateRateSource_fixPriceRateSource_arguments")
    void testCreateRateSource_fixPriceRateSource(String sourceLogin, String cryptocurrency) {
        IRateSource rateSource = solanaExtension.createRateSource(sourceLogin);

        assertInstanceOf(FixPriceRateSource.class, rateSource);
        assertEquals(BigDecimal.valueOf(21L), rateSource.getExchangeRateLast(cryptocurrency, "CZK"));
        assertEquals("CZK", rateSource.getPreferredFiatCurrency());
    }

    private static Stream<Arguments> testCreateRateSource_fixPriceRateSource_defaultValues_arguments() {
        return Stream.of(
            arguments("solfix", "SOL"),
            arguments("usdcsolfix", "USDCSOL")
        );
    }

    @ParameterizedTest
    @MethodSource("testCreateRateSource_fixPriceRateSource_defaultValues_arguments")
    void testCreateRateSource_fixPriceRateSource_defaultValues(String sourceLogin, String cryptocurrency) {
        IRateSource rateSource = solanaExtension.createRateSource(sourceLogin);

        assertInstanceOf(FixPriceRateSource.class, rateSource);
        assertEquals(BigDecimal.ZERO, rateSource.getExchangeRateLast(cryptocurrency, "USD"));
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

        assertEquals(2, supportedCryptocurrencies.size());
        assertTrue(supportedCryptocurrencies.contains("SOL"));
        assertTrue(supportedCryptocurrencies.contains("USDCSOL"));
    }

}