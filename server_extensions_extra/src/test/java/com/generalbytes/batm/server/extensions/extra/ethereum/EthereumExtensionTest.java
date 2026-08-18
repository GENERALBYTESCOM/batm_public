package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.sources.stasis.StasisTickerRateSource;
import com.generalbytes.batm.server.extensions.extra.ethereum.stream365.Stream365;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class EthereumExtensionTest {

    private EthereumExtension extension;

    @BeforeEach
    void setUp() {
        extension = new EthereumExtension();
    }

    private static String[] provideInvalidLogin() {
        return new String[]{
                null,
                "",
                "  ",
                "\t",
                "\n",
                // Invalid USDC demo parameters
                "usdcdemo",
                "usdcdemo:",
                "usdcdemo::",
                "usdcdemo::address",
                "usdcdemo:CZK:",
                "usdcdemo:CZK",
                // Invalid USDT demo parameters
                "usdtdemo",
                "usdtdemo:",
                "usdtdemo::",
                "usdtdemo::address",
                "usdtdemo:CZK:",
                "usdtdemo:CZK",
                // Invalid QCAD demo parameters
                "qcaddemo",
                "qcaddemo:",
                "qcaddemo::",
                "qcaddemo::address",
                "qcaddemo:CZK:",
                "qcaddemo:CZK",
                // Unknown wallet
                "unknown:CZK:address",
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLogin")
    void testCreateWallet_invalidLogin(String invalidLogin) {
        assertNull(extension.createWallet(invalidLogin, null));
    }

    private static Object[][] provideDemoLogin() {
        return new Object[][]{
                {"usdcdemo:CZK:address", CryptoCurrency.USDC},
                {"usdtdemo:CZK:address", CryptoCurrency.USDT},
                {"qcaddemo:CZK:address", CryptoCurrency.QCAD},
        };
    }

    @ParameterizedTest
    @MethodSource("provideDemoLogin")
    void testCreateWallet_demo(String login, CryptoCurrency expectedCryptocurrency) {
        IWallet wallet = extension.createWallet(login, null);

        assertInstanceOf(DummyExchangeAndWalletAndSource.class, wallet);
        DummyExchangeAndWalletAndSource dummyWallet = (DummyExchangeAndWalletAndSource) wallet;
        assertEquals("address", dummyWallet.getCryptoAddress(expectedCryptocurrency.getCode()));
        assertEquals(Set.of(expectedCryptocurrency.getCode()), dummyWallet.getCryptoCurrencies());
        assertEquals(Set.of("CZK"), dummyWallet.getFiatCurrencies());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLogin")
    void testCreateExchange_invalidLogin(String invalidLogin) {
        assertNull(extension.createExchange(invalidLogin));
    }

    @ParameterizedTest
    @MethodSource("provideDemoLogin")
    void testCreateExchange_demo(String login, CryptoCurrency expectedCryptocurrency) {
        IExchange exchange = extension.createExchange(login);

        assertInstanceOf(DummyExchangeAndWalletAndSource.class, exchange);
        DummyExchangeAndWalletAndSource dummyExchange = (DummyExchangeAndWalletAndSource) exchange;
        assertEquals("address", dummyExchange.getCryptoAddress(expectedCryptocurrency.getCode()));
        assertEquals(Set.of(expectedCryptocurrency.getCode()), dummyExchange.getCryptoCurrencies());
        assertEquals(Set.of("CZK"), dummyExchange.getFiatCurrencies());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "::", "unknown:1:CZK"})
    void testCreateRateSource_invalidLogin(String invalidLogin) {
        assertNull(extension.createRateSource(invalidLogin));
    }

    @ParameterizedTest
    @ValueSource(strings = {"stream365", "stream365:", "stream365:something"})
    void testCreateRateSource_stream365(String login) {
        IRateSource rateSource = extension.createRateSource(login);

        assertInstanceOf(Stream365.class, rateSource);
    }

    @ParameterizedTest
    @ValueSource(strings = {"stasis", "stasis:", "stasis:something"})
    void testCreateRateSource_stasis(String login) {
        IRateSource rateSource = extension.createRateSource(login);

        assertInstanceOf(StasisTickerRateSource.class, rateSource);
    }

    private static Stream<Arguments> provideQcadFixRateSourceLogin() {
        return Stream.of(
            arguments("qcadfix:5:CAD", new BigDecimal("5"), "CAD"),
            arguments("qcadfix:0.98", new BigDecimal("0.98"), "USD"),
            arguments("qcadfix", BigDecimal.ONE, "USD"),
            arguments("qcadfix:abc:CZK", BigDecimal.ONE, "CZK")
        );
    }

    @ParameterizedTest
    @MethodSource("provideQcadFixRateSourceLogin")
    void testCreateRateSource_qcadFix(String login, BigDecimal expectedRate, String expectedFiatCurrency) {
        IRateSource rateSource = extension.createRateSource(login);

        assertInstanceOf(FixPriceRateSource.class, rateSource);
        assertEquals(expectedFiatCurrency, rateSource.getPreferredFiatCurrency());
        assertEquals(0, expectedRate.compareTo(rateSource.getExchangeRateLast(CryptoCurrency.QCAD.getCode(), expectedFiatCurrency)));
    }

}