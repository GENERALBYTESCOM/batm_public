package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.ITunnelManager;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd.ElementsdRPCWalletWithUniqueAddresses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiquidBitcoinExtensionTest {

    private LiquidBitcoinExtension extension;

    @BeforeEach
    void setUp() {
        extension = new LiquidBitcoinExtension();
    }

    @Test
    void testGetName() {
        assertEquals("BATM Liquid Network Bitcoin extension", extension.getName());
    }

    @Test
    void testGetSupportedCryptoCurrencies() {
        Set<String> cryptoCurrencies = extension.getSupportedCryptoCurrencies();
        assertEquals(1, cryptoCurrencies.size());
        assertEquals(CryptoCurrency.L_BTC.getCode(), cryptoCurrencies.iterator().next());
    }

    @Test
    void testGetCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> cryptoCurrencyDefinitions = extension.getCryptoCurrencyDefinitions();
        assertEquals(1, cryptoCurrencyDefinitions.size());
        assertInstanceOf(LiquidBitcoinDefinition.class, cryptoCurrencyDefinitions.iterator().next());
    }

    @Test
    void testCreateAddressValidator() {
        ICryptoAddressValidator validator = extension.createAddressValidator(CryptoCurrency.L_BTC.getCode());
        assertInstanceOf(LiquidBitcoinAddressValidator.class, validator);
    }

    @Test
    void testCreateAddressValidator_unknownCryptocurrency() {
        ICryptoAddressValidator validator = extension.createAddressValidator("unknown");
        assertNull(validator);
    }

    private static Object[][] provideInvalidWalletParameters() {
        return new Object[][]{
            // Invalid walletLogin
            {null, null},
            {"", null},
            // Unknown wallet type
            {"unknownwallet", null},
            // Not enough parameters in walletLogin
            {"elementsdbtcnoforward", null},
            {"elementsdbtcnoforward:protocol", null},
            {"elementsdbtcnoforward:protocol:username", null},
            {"elementsdbtcnoforward:protocol:username:password", null},
            {"elementsdbtcnoforward:protocol:username:password:hostname", null},
            // Invalid port (not a number)
            {"elementsdbtcnoforward:protocol:username:password:hostname:invalid_port", null},
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidWalletParameters")
    void testCreateWallet_invalidParameters(String walletLogin, String tunnelPassword) {
        assertNull(extension.createWallet(walletLogin, tunnelPassword));
    }

    private static Object[][] provideValidWalletParameters() {
        return new Object[][]{
            {"elementsdbtcnoforward:http:username:password:hostname:123456", "http://username:password@hostname:123456", ""},
            {"elementsdbtcnoforward:http:username:password:hostname:123456:walletName", "http://username:password@hostname:123456", "walletName"},
        };
    }

    @ParameterizedTest
    @MethodSource("provideValidWalletParameters")
    void testCreateWallet_validParameters(String walletLogin, String expectedRpcUrl, String expectedWalletName) {
        try (MockedConstruction<ElementsdRPCWalletWithUniqueAddresses> mockedConstruction
                 = mockWalletConstruction(expectedRpcUrl, expectedWalletName)) {
            IWallet wallet = extension.createWallet(walletLogin, null);

            assertInstanceOf(ElementsdRPCWalletWithUniqueAddresses.class, wallet);
            assertEquals(wallet, mockedConstruction.constructed().get(0));
        }
    }

    @Test
    void testCreateWallet_validParameters_withTunnel() throws IOException {
        String walletLogin = "elementsdbtcnoforward:http:username:password:hostname1:1234";
        String expectedRpcUrl = "http://username:password@hostname2:12345";

        try (MockedConstruction<ElementsdRPCWalletWithUniqueAddresses> mockedConstruction
                 = mockWalletConstruction(expectedRpcUrl, "")) {

            IExtensionContext extensionContext = mock(IExtensionContext.class);
            ITunnelManager tunnelManager = mock(ITunnelManager.class);
            when(tunnelManager.connectIfNeeded(any(), any(), any())).thenReturn(InetSocketAddress.createUnresolved("hostname2", 12345));
            when(extensionContext.getTunnelManager()).thenReturn(tunnelManager);
            extension.init(extensionContext);

            IWallet wallet = extension.createWallet(walletLogin, "tunnelPassword");

            assertInstanceOf(ElementsdRPCWalletWithUniqueAddresses.class, wallet);
            assertEquals(wallet, mockedConstruction.constructed().get(0));
            verify(tunnelManager).connectIfNeeded(walletLogin, "tunnelPassword", InetSocketAddress.createUnresolved("hostname1", 1234));
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testcreateRateSource_loginNotSet(String sourceLogin) {
        assertNull(extension.createRateSource(sourceLogin));
    }

    @Test
    void testcreateRateSource_unknownType() {
        assertNull(extension.createRateSource("someLoginValue"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"l_btcfix", "l_btcfix:notNumber"})
    void testcreateRateSource_invalidRate(String sourceLogin) {
        IRateSource rateSource = extension.createRateSource(sourceLogin);
        assertNotNull(rateSource);
        BigDecimal exchangeRateLast = rateSource.getExchangeRateLast(CryptoCurrency.L_BTC.getCode(), FiatCurrency.USD.getCode());
        assertEquals(0, BigDecimal.ZERO.compareTo(exchangeRateLast));
    }

    @Test
    void testcreateRateSource() {
        IRateSource rateSource = extension.createRateSource("l_btcfix:50:EUR");
        assertNotNull(rateSource);
        BigDecimal exchangeRateLast = rateSource.getExchangeRateLast(CryptoCurrency.L_BTC.getCode(), FiatCurrency.EUR.getCode());
        assertEquals(0, BigDecimal.valueOf(50L).compareTo(exchangeRateLast));
    }

    private static MockedConstruction<ElementsdRPCWalletWithUniqueAddresses> mockWalletConstruction(String expectedRpcUrl,
                                                                                                    String expectedWalletName) {
        return mockConstruction(ElementsdRPCWalletWithUniqueAddresses.class, (mock, context) -> {
            assertEquals(expectedRpcUrl, context.arguments().get(0));
            assertEquals(expectedWalletName, context.arguments().get(1));
        });
    }
}