package com.generalbytes.batm.server.extensions.extra.bitcoin;

import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.TestExtensionContext;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseApiFactory;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseApiWrapperCdp;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseApiWrapperLegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseV2ApiWrapperCdp;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseV2ApiWrapperLegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.ICoinbaseV3Api;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.CoinbaseExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.ICoinbaseAPI;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.BitgoWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.CoinbaseV2RateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.CoinbaseWalletV2;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.CoinbaseWalletV2WithUniqueAddresses;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.ICoinbaseV2API;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class BitcoinExtensionTest {

    private static final Logger log = LoggerFactory.getLogger(BitcoinExtensionTest.class);

    @Test
    void testCreateFromExtension() {
        testUrl("http://localhost:3080/", "bitgo:http://localhost:3080:tokentoken:wallet_address:wallet_passphrase");
        testUrl("http://localhost:3080/", "bitgo:localhost:3080:tokentoken:wallet_address:wallet_passphrase");
        testUrl("http://localhost/", "bitgo:http://localhost:tokentoken:wallet_address:wallet_passphrase");
        testUrl("http://localhost/", "bitgo:localhost:tokentoken:wallet_address:wallet_passphrase");
        testUrl("https://localhost:3080/", "bitgo:https://localhost:3080:tokentoken:wallet_address:wallet_passphrase");
        testUrl("https://localhost/", "bitgo:https://localhost:tokentoken:wallet_address:wallet_passphrase");
    }

    private void testUrl(String expected, String walletLogin) {
        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());
        final IWallet bitgowallet = bitcoinExtension.createWallet(walletLogin, null);
        assertTrue(bitgowallet instanceof BitgoWallet);
        assertEquals(expected, ((BitgoWallet)bitgowallet).getUrl());
    }

    @Test
    void testCreateWalletBitGoFees() {
        // Both fee rate and max fee rate are set -> parse fee rate and max fee rate individually
        doTestCreateWalletBitGoFees(createBitGoWalletUrl(1000, 2000), 1000, 2000);
        // Max fee rate is lower than fee rate -> use fee rate as max fee rate
        doTestCreateWalletBitGoFees(createBitGoWalletUrl(1000, 500), 1000, 1000);
        // Max fee rate is not set -> use fee rate as max fee rate
        doTestCreateWalletBitGoFees(createBitGoWalletUrl(1000, null), 1000, 1000);
        // Neither fee rate nor max fee rate is set -> expect nulls
        doTestCreateWalletBitGoFees(createBitGoWalletUrl(null, null), null, null);
    }

    private String createBitGoWalletUrl(Integer feeRate, Integer maxFeeRate) {
        StringBuilder builder = new StringBuilder("bitgo:http://localhost:3080:tokentoken:wallet_address:wallet_passphrase:2");
        if (feeRate != null) {
            builder.append(":").append(feeRate);
            if (maxFeeRate != null) {
                builder.append(":").append(maxFeeRate);
            }
        }
        return builder.toString();
    }

    private void doTestCreateWalletBitGoFees(String url, Integer expectedFeeRate, Integer expectedMaxFeeRate) {
        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());
        final IWallet wallet = bitcoinExtension.createWallet(url, null);
        assertTrue(wallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet) wallet;
        assertNotNull(bitgoWallet);
        assertEquals(expectedFeeRate, bitgoWallet.getFeeRate());
        assertEquals(expectedMaxFeeRate, bitgoWallet.getMaxFeeRate());
    }

    @Test
    void bitgoFullTokenTest() {
        String wallet = "bitgo:http://localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = null;
        String host = "";
        String fullHost = null;
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        assertEquals("http", protocol);
        assertEquals("//localhost", host);
        assertEquals("http://localhost", fullHost);
        assertEquals("3080", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet, null);
        assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        assertNotNull(bitgoWallet);
        assertEquals("http://localhost:3080/", bitgoWallet.getUrl());
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    void bitgoNoPortTokenTest() {
        String wallet = "bitgo:http://localhost:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = "";
        String host = "";
        String fullHost = "";
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        assertEquals("http", protocol);
        assertEquals("//localhost", host);
        assertEquals("http://localhost", fullHost);
        assertEquals("", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet, null);
        assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        assertNotNull(bitgoWallet);
        assertEquals("http://localhost/", bitgoWallet.getUrl());
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    void bitgoHttpsTokenTest() {
        String wallet = "bitgo:https://localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = "";
        String host = "";
        String fullHost = "";
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        assertEquals("https", protocol);
        assertEquals("//localhost", host);
        assertEquals("https://localhost", fullHost);
        assertEquals("3080", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet, null);
        assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        assertNotNull(bitgoWallet);
        assertEquals("https://localhost:3080/", bitgoWallet.getUrl());
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    void bitgoNoProtocolTokenTest() {
        String wallet = "bitgo:localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        StringTokenizer st = new StringTokenizer(wallet,":");
        String walletType = st.nextToken();
        String first = st.nextToken();
        String protocol = "";
        String host = "";
        String fullHost = "";
        if(first != null && first.startsWith("http")) {
            protocol = first ;
            host = st.nextToken();
            fullHost = protocol + ":" + host;
        } else {
            host = first;
            fullHost = host;
        }

        String port = "";
        String token = "";
        String next = st.nextToken();
        if(next != null && next.length() > 6) {
            token = next;
        } else {
            port = next;
            token = st.nextToken();
        }
        String walletAddress = st.nextToken();
        String walletPassphrase = st.nextToken();

        log.info("wallet = {}, protocol = {}, host = {}, port = {}, fullHost = {}, token = {}, address = {}, passphrase = {}, next = {}", walletType, protocol, host, port, fullHost, token, walletAddress, walletPassphrase, next);

        assertEquals("", protocol);
        assertEquals("localhost", host);
        assertEquals("localhost", fullHost);
        assertEquals("3080", port);

        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());
        final IWallet bitgowallet = bitcoinExtension.createWallet(wallet, null);
        assertTrue(bitgowallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)bitgowallet;
        assertNotNull(bitgoWallet);
        assertEquals("http://localhost:3080/", bitgoWallet.getUrl());
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    void bitgoWalletTest() {
        String address = "bitgo:http://localhost:3080:v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21:5b20e3a9266bbe80095757489d84a6bb:Vranec8586";
        final BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());
        final IWallet wallet = bitcoinExtension.createWallet(address, null);
        assertTrue(wallet instanceof BitgoWallet);
        final BitgoWallet bitgoWallet = (BitgoWallet)wallet;
        assertNotNull(bitgoWallet);
        assertNotNull(bitgoWallet);
        assertEquals("http://localhost:3080/", bitgoWallet.getUrl());
        assertEquals("5b20e3a9266bbe80095757489d84a6bb", bitgoWallet.getWalletId());
        log.info("wallet = " + bitgoWallet);
    }

    @Test
    void bitgoErrorTest() {
        String body = "{\"error\":\"invalid address for network\",\"name\":\"Invalid\",\"requestId\":\"cjjectv6y2zlgill815ppvp4g\",\"message\":\"invalid address for network\"}";
        int index1 = body.indexOf("error") + 8;
        int index2 = body.indexOf(",") - 1;
        String value = body.substring(index1, index2);
        assertNotNull(value);
        assertEquals("invalid address for network", value);

        body = "{\"error\":\"sub-dust-threshold amount for LWcx7VHK3hKDV5KaiE2EQLPpGt9GEVwzdM: 10000\",\"name\":\"Invalid\",\"requestId\":\"cjjebbtly2ew1gmldyr9h2dnk\",\"message\":\"sub-dust-threshold amount for LWcx7VHK3hKDV5KaiE2EQLPpGt9GEVwzdM: 10000\"}";
        index1 = body.indexOf("error") + 8;
        index2 = body.indexOf(",") - 1;
        value = body.substring(index1, index2);
        assertNotNull(value);
        assertEquals("sub-dust-threshold amount for LWcx7VHK3hKDV5KaiE2EQLPpGt9GEVwzdM: 10000", value);

        body = "{\"error\":\"needs unlock\",\"needsOTP\":true,\"needsUnlock\":true,\"name\":\"Response\",\"requestId\":\"cjjebd0lq2ke0g7l73czlu75j\",\"message\":\"needs unlock\"}";
        index1 = body.indexOf("error") + 8;
        index2 = body.indexOf(",") - 1;
        value = body.substring(index1, index2);
        assertNotNull(value);
        assertEquals("needs unlock", value);
    }

    @Test
    void testCreateExchange_validLegacyCoinbase() {
        // accountName, preferredFiatCurrency and paymentMethodName are all optional
        doTestCreateExchange_validLegacyCoinbase(null, null, null);
        doTestCreateExchange_validLegacyCoinbase("accountName", null, null);
        doTestCreateExchange_validLegacyCoinbase("accountName", "CZK", null);
        doTestCreateExchange_validLegacyCoinbase("accountName", "CZK", "paymentMethodName");
    }

    private void doTestCreateExchange_validLegacyCoinbase(String accountName, String preferredFiatCurrency, String paymentMethodName) {
        String prefix = "coinbaseexchange";
        String apiKey = "apiKey";
        String secretKey = "secretKey";
        String paramString = getCoinbaseParams(prefix, apiKey, secretKey, accountName, preferredFiatCurrency, paymentMethodName);

        BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());

        try (MockedStatic<CoinbaseApiFactory> mockedApiFactory = mockStatic(CoinbaseApiFactory.class)) {
            mockedApiFactory.when(CoinbaseApiFactory::createCoinbaseApiLegacy).thenReturn(mock(ICoinbaseAPI.class));

            IExchange exchange = bitcoinExtension.createExchange(paramString);

            assertNotNull(exchange);
            assertTrue(exchange instanceof CoinbaseExchange);
            CoinbaseExchange coinbaseExchange = (CoinbaseExchange) exchange;
            assertEquals(accountName, coinbaseExchange.getAccountName());
            assertEquals(preferredFiatCurrency, coinbaseExchange.getPreferedFiatCurrency());
            assertEquals(paymentMethodName, coinbaseExchange.getPaymentMethodName());
            assertTrue(coinbaseExchange.getApi() instanceof CoinbaseApiWrapperLegacy);
            mockedApiFactory.verify(CoinbaseApiFactory::createCoinbaseApiLegacy);
        }
    }

    @Test
    void testCreateExchange_validCdpCoinbase() {
        // accountName, preferredFiatCurrency and paymentMethodName are all optional
        doTestCreateExchange_validCdpCoinbase(null, null, null);
        doTestCreateExchange_validCdpCoinbase("accountName", null, null);
        doTestCreateExchange_validCdpCoinbase("accountName", "CZK", null);
        doTestCreateExchange_validCdpCoinbase("accountName", "CZK", "paymentMethodName");
    }

    private void doTestCreateExchange_validCdpCoinbase(String accountName, String preferredFiatCurrency, String paymentMethodName) {
        String prefix = "coinbaseexchange2";
        String privateKey = "privateKey";
        String keyName = "keyName";
        String paramString = getCoinbaseParams(prefix, privateKey, keyName, accountName, preferredFiatCurrency, paymentMethodName);

        BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());

        try (MockedStatic<CoinbaseApiFactory> mockedApiFactory = mockStatic(CoinbaseApiFactory.class)) {
            mockedApiFactory.when(CoinbaseApiFactory::createCoinbaseV3Api).thenReturn(mock(ICoinbaseV3Api.class));

            IExchange exchange = bitcoinExtension.createExchange(paramString);

            assertNotNull(exchange);
            assertTrue(exchange instanceof CoinbaseExchange);
            CoinbaseExchange coinbaseExchange = (CoinbaseExchange) exchange;
            assertEquals(accountName, coinbaseExchange.getAccountName());
            assertEquals(preferredFiatCurrency, coinbaseExchange.getPreferedFiatCurrency());
            assertEquals(paymentMethodName, coinbaseExchange.getPaymentMethodName());
            assertTrue(coinbaseExchange.getApi() instanceof CoinbaseApiWrapperCdp);
            mockedApiFactory.verify(CoinbaseApiFactory::createCoinbaseV3Api);
        }
    }

    @Test
    void testCreateExchange_invalidLegacyCoinbase() {
        // Missing mandatory parameter: secretKey
        doTestCreateExchange_invalidCoinbase("coinbaseexchange:apiKey");
        // Missing mandatory parameters: apiKey, secretKey
        doTestCreateExchange_invalidCoinbase("coinbaseexchange");
    }

    @Test
    void testCreateExchange_invalidCdpCoinbase() {
        // Missing mandatory parameter: secretKey
        doTestCreateExchange_invalidCoinbase("coinbaseexchange2:apiKey");
        // Missing mandatory parameters: apiKey, secretKey
        doTestCreateExchange_invalidCoinbase("coinbaseexchange2");
    }

    private void doTestCreateExchange_invalidCoinbase(String paramString) {
        BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());

        IExchange exchange = bitcoinExtension.createExchange(paramString);

        assertNull(exchange);
    }

    @Test
    void testCreateWallet_validLegacyCoinbase() {
        // accountName is optional
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2", CoinbaseWalletV2.class, null, null);
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2", CoinbaseWalletV2.class, null, "");
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2", CoinbaseWalletV2.class, null, "   ");
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2", CoinbaseWalletV2.class, "accountName", "accountName");
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2noforward", CoinbaseWalletV2WithUniqueAddresses.class, null, null);
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2noforward", CoinbaseWalletV2WithUniqueAddresses.class, null, "");
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2noforward", CoinbaseWalletV2WithUniqueAddresses.class, null, "   ");
        doTestCreateWallet_validLegacyCoinbase("coinbasewallet2noforward", CoinbaseWalletV2WithUniqueAddresses.class, "accountName", "accountName");
    }

    private void doTestCreateWallet_validLegacyCoinbase(String prefix,
                                                        Class<?> walletClass,
                                                        String expectedAccountName,
                                                        String accountName) {
        String apiKey = "apiKey";
        String secretKey = "secretKey";
        String paramString = getCoinbaseParams(prefix, apiKey, secretKey, accountName);

        BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());

        try (MockedStatic<CoinbaseApiFactory> mockedApiFactory = mockStatic(CoinbaseApiFactory.class)) {
            mockedApiFactory.when(CoinbaseApiFactory::createCoinbaseV2ApiLegacy).thenReturn(mock(ICoinbaseV2API.class));

            IWallet wallet = bitcoinExtension.createWallet(paramString, null);

            assertNotNull(wallet);
            assertTrue(walletClass.isAssignableFrom(walletClass));
            CoinbaseWalletV2 coinbaseWallet = (CoinbaseWalletV2) wallet;
            assertEquals(expectedAccountName, coinbaseWallet.getAccountName());
            assertTrue(coinbaseWallet.getApi() instanceof CoinbaseV2ApiWrapperLegacy);
            mockedApiFactory.verify(CoinbaseApiFactory::createCoinbaseV2ApiLegacy);
        }
    }

    @Test
    void testCreateWallet_validCdpCoinbase() {
        // accountName is optional
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3", CoinbaseWalletV2.class, null, null);
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3", CoinbaseWalletV2.class, null, "");
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3", CoinbaseWalletV2.class, null, "   ");
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3", CoinbaseWalletV2.class, "accountName", "accountName");
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3noforward", CoinbaseWalletV2WithUniqueAddresses.class, null, null);
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3noforward", CoinbaseWalletV2WithUniqueAddresses.class, null, "");
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3noforward", CoinbaseWalletV2WithUniqueAddresses.class, null, "   ");
        doTestCreateWallet_validCdpCoinbase("coinbasewallet3noforward", CoinbaseWalletV2WithUniqueAddresses.class, "accountName", "accountName");
    }

    private void doTestCreateWallet_validCdpCoinbase(String prefix,
                                                     Class<?> walletClass,
                                                     String expectedAccountName,
                                                     String accountName) {
        String privateKey = "privateKey";
        String keyName = "keyName";
        String paramString = getCoinbaseParams(prefix, privateKey, keyName, accountName);

        BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());

        try (MockedStatic<CoinbaseApiFactory> mockedApiFactory = mockStatic(CoinbaseApiFactory.class)) {
            mockedApiFactory.when(CoinbaseApiFactory::createCoinbaseV3Api).thenReturn(mock(ICoinbaseV3Api.class));

            IWallet wallet = bitcoinExtension.createWallet(paramString, null);

            assertNotNull(wallet);
            assertTrue(walletClass.isAssignableFrom(walletClass));
            CoinbaseWalletV2 coinbaseWallet = (CoinbaseWalletV2) wallet;
            assertEquals(expectedAccountName, coinbaseWallet.getAccountName());
            assertTrue(coinbaseWallet.getApi() instanceof CoinbaseV2ApiWrapperCdp);
            mockedApiFactory.verify(CoinbaseApiFactory::createCoinbaseV3Api);
        }
    }

    @Test
    void testCreateWallet_invalidLegacyCoinbase() {
        // Missing mandatory parameter: secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet2:apiKey");
        // Missing mandatory parameters: apiKey, secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet2");
        // Missing mandatory parameter: secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet2noforward:apiKey");
        // Missing mandatory parameters: apiKey, secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet2noforward");
    }

    @Test
    void testCreateWallet_invalidCdpCoinbase() {
        // Missing mandatory parameter: secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet3:apiKey");
        // Missing mandatory parameters: apiKey, secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet3");
        // Missing mandatory parameter: secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet3noforward:apiKey");
        // Missing mandatory parameters: apiKey, secretKey
        doTestCreateWallet_invalidCoinbase("coinbasewallet3noforward");
    }

    private void doTestCreateWallet_invalidCoinbase(String paramString) {
        BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());

        IWallet wallet = bitcoinExtension.createWallet(paramString, null);

        assertNull(wallet);
    }

    @Test
    void testCreateRateSource_validLegacyCoinbase() {
        doTestCreateRateSource_validLegacyCoinbase("USD", "coinbasers");
        doTestCreateRateSource_validLegacyCoinbase("CZK", "coinbasers:CZK");
    }

    private void doTestCreateRateSource_validLegacyCoinbase(String expectedFiatCurrency, String paramString) {
        BitcoinExtension bitcoinExtension = new BitcoinExtension();
        bitcoinExtension.init(new TestExtensionContext());

        try (MockedStatic<CoinbaseApiFactory> mockedApiFactory = mockStatic(CoinbaseApiFactory.class)) {
            mockedApiFactory.when(CoinbaseApiFactory::createCoinbaseV2ApiLegacy).thenReturn(mock(ICoinbaseV2API.class));

            IRateSource rateSource = bitcoinExtension.createRateSource(paramString);

            assertNotNull(rateSource);
            assertTrue(rateSource instanceof CoinbaseV2RateSource);
            CoinbaseV2RateSource coinbaseRateSource = (CoinbaseV2RateSource) rateSource;
            assertEquals(expectedFiatCurrency, coinbaseRateSource.getPreferredFiatCurrency());
            assertTrue(coinbaseRateSource.getApi() instanceof CoinbaseV2ApiWrapperLegacy);
            mockedApiFactory.verify(CoinbaseApiFactory::createCoinbaseV2ApiLegacy);
        }
    }

    private String getCoinbaseParams(String prefix,
                                     String apiKey,
                                     String secretKey,
                                     String accountName,
                                     String preferredFiatCurrency,
                                     String paymentMethodName) {
        return Stream.of(prefix, apiKey, secretKey, accountName, preferredFiatCurrency, paymentMethodName)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(":"));
    }

    private String getCoinbaseParams(String prefix, String apiKey, String secretKey, String accountName) {
        return getCoinbaseParams(prefix, apiKey, secretKey, accountName, null, null);
    }
}
