package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.Converters;
import com.generalbytes.batm.server.extensions.ICanSendMany;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCoinRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoSendManyRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class BitgoWalletTest {

    private static final Logger log = LoggerFactory.getLogger(BitgoWalletTest.class);

    private static BitgoWallet wallet;

    private static void setLoggerLevel(String name, String level) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            List<ch.qos.logback.classic.Logger> loggers = loggerContext.getLoggerList();

            for (ch.qos.logback.classic.Logger logger : loggers) {
                if (logger.getName().startsWith(name)) {
                    logger.setLevel(Level.toLevel(level));
                }
            }
        } catch (Throwable e) {
            log.error("batm.master.ServerUtil - setLoggerLevel");
            log.error("setLoggerLevel", e);
        }
    }

    @BeforeEach
    void setUp() {
        setLoggerLevel("batm", "trace");
        setLoggerLevel("si.mazi.rescu", "trace");

        RestProxyFactory.createProxy(IBitgoAPI.class, "http://localhost:3080/");

        String scheme = "http";
        String host = "localhost";
        int port = 3080;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";
        int numBlocks = 10;

        wallet = new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase, numBlocks);
    }

    @Test
    @Disabled
    void getCryptAddressTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        String scheme = "https";
        String host = "test.bitgo.com";
        int port = 443;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

        final BitgoWallet remotewallet = new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase, 2);
        final String address = remotewallet.getCryptoAddress(coin);
        assertNotNull(address);
        assertEquals("2N2WR6aVSEgq5ZLTED9vHvCWFdAMf6yhebd", address);
    }

    @Test
    @Disabled
    void getCryptBalanceTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        String scheme = "https";
        String host = "test.bitgo.com";
        int port = 443;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

        final BitgoWallet remotewallet = new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase, 2);
        BigDecimal balance = remotewallet.getCryptoBalance(coin);
        assertNotNull(balance);
        log.info("balance = {}", balance);
    }

    @Test
    @Disabled("Local instance of bitgo-express is required to run")
    void sendCoinsTest() {
        String destinationAddress = "2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi";
        String coin = CryptoCurrency.TBTC.getCode();
        int amountInt = 10000;
        BigDecimal amount = BigDecimal.valueOf(amountInt).divide(Converters.TBTC);
        String description = null;

        String result = wallet.sendCoins(destinationAddress, amount, coin, description);
        log.info("send coins status = {}", result);
    }

    @Test
    @Disabled("Local instance of bitgo-express is required to run")
    void sendManyTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        int amountInt = 10000;
        BigDecimal amount = BigDecimal.valueOf(amountInt).divide(Converters.TBTC);
        String description = null;

        String result = ((ICanSendMany) wallet).sendMany(Arrays.asList(
            new ICanSendMany.Transfer("2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi", new BigDecimal("0.0001")),
            new ICanSendMany.Transfer("2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi", new BigDecimal("0.002"))
        ), coin, "test send to self");
        log.info("send coins status = {}", result);
    }

    @Test
    @Disabled("Local instance of bitgo-express is required to run")
    void sendCoinsNumBlocksTest() {
        String destinationAddress = "2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi";
        String coin = CryptoCurrency.TBTC.getCode();
        int amountInt = 10000;
        BigDecimal amount = BigDecimal.valueOf(amountInt).divide(Converters.TBTC);
        String description = "CAS: uses the numBlocks API parameter";

        String result = wallet.sendCoins(destinationAddress, amount, coin, description);
        log.info("send coins with numBlocks parameter status = {}", result);
    }

    @Test
    void convertBalance() {
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("15.50581145"), CryptoCurrency.USDT.getCode())).isEqualTo("15505811");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("15.50581145"), CryptoCurrency.USDTTRON.getCode())).isEqualTo("15505811");

        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.00000001"), CryptoCurrency.BTC.getCode())).isEqualTo("1");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.00000001000"), CryptoCurrency.LTC.getCode())).isEqualTo("1");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000000000000001000"), CryptoCurrency.ETH.getCode())).isEqualTo("1");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000001000"), CryptoCurrency.USDT.getCode())).isEqualTo("1");

        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000000000"), CryptoCurrency.BTC.getCode())).isEqualTo("0");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000009"), CryptoCurrency.BTC.getCode())).isEqualTo("0");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000019"), CryptoCurrency.BTC.getCode())).isEqualTo("1");

        Assertions.assertThat(wallet.fromSatoshis(CryptoCurrency.BTC.getCode(), new BigDecimal("1.000"))).isEqualByComparingTo("0.00000001");
        Assertions.assertThat(wallet.fromSatoshis(CryptoCurrency.BTC.getCode(), new BigDecimal("1.999"))).isEqualByComparingTo("0.00000001");

        Exception exception = assertThrows(NullPointerException.class, () -> wallet.fromSatoshis("unknown", BigDecimal.ONE));
        Assertions.assertThat(exception.getMessage()).contains("not supported");
        for (String cryptoCurrency : wallet.getCryptoCurrencies()) {
            assertEquals(BigDecimal.ONE, wallet.fromSatoshis(cryptoCurrency, new BigDecimal(wallet.toSatoshis(BigDecimal.ONE, cryptoCurrency))));
        }
    }

    private static Stream<Arguments> testSendCoinsAndSendMany_arguments() {
        return Stream.of(
            arguments("BCH", "bch", "100000000", null, null),
            arguments("BTC", "btc", "100000000", null, null),
            arguments("ETH", "eth", "1000000000000000000", null, null),
            arguments("LTC", "ltc", "100000000", null, null),
            arguments("USDT", "usdt", "1000000", "transfer", null),
            arguments("USDTTRON", "trx:usdt", "1000000", null, null),
            arguments("XRP", "xrp", "1000000", null, null),
            arguments("TBTC", "tbtc", "100000000", null, null),
            arguments("USDC", "usdc", "1000000", "transfer", null),
            arguments("SOL", "sol", "1000000000", "transfer", null),
            arguments("USDCSOL", "sol:usdc", "1000000", "transfer", "sol:usdc")
        );
    }

    @ParameterizedTest
    @MethodSource("testSendCoinsAndSendMany_arguments")
    void testSendCoins(String cryptocurrency,
                       String expectedBitGoCryptocurrency,
                       String expectedAmount,
                       String expectedType,
                       String expectedTokenName) throws IOException {
        IBitgoAPI api = mock(IBitgoAPI.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(eq(IBitgoAPI.class), anyString(), any())).thenReturn(api);

            BitgoWallet bitgoWallet = new BitgoWallet(
                "http", "host", 1234, "token", "walletId", "walletPassphrase", 2, 3, 4
            );
            bitgoWallet.sendCoins("destinationAddress", BigDecimal.ONE, cryptocurrency, "description");

            ArgumentCaptor<BitGoCoinRequest> requestCaptor = ArgumentCaptor.forClass(BitGoCoinRequest.class);
            verify(api).sendCoins(eq(expectedBitGoCryptocurrency), eq("walletId"), requestCaptor.capture());

            BitGoCoinRequest request = requestCaptor.getValue();
            assertEquals("destinationAddress", request.address());
            assertEquals(expectedAmount, request.amount());
            assertEquals("walletPassphrase", request.walletPassphrase());
            assertEquals(2, request.numBlocks());
            assertEquals("description", request.comment());
            assertEquals(3, request.feeRate());
            assertEquals(4, request.maxFeeRate());
            assertEquals(expectedType, request.type());
            assertEquals(expectedTokenName, request.tokenName());
        }
    }

    @ParameterizedTest
    @MethodSource("testSendCoinsAndSendMany_arguments")
    void testSendMany(String cryptocurrency,
                      String expectedBitGoCryptocurrency,
                      String expectedAmount,
                      String expectedType,
                      String expectedTokenName) throws IOException {
        IBitgoAPI api = mock(IBitgoAPI.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class)) {
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(eq(IBitgoAPI.class), anyString(), any())).thenReturn(api);

            BitgoWallet bitgoWallet = new BitgoWallet("http", "host", 1234, "token", "walletId", "walletPassphrase", 2);
            List<ICanSendMany.Transfer> transfers = List.of(new ICanSendMany.Transfer("destinationAddress", BigDecimal.ONE));

            bitgoWallet.sendMany(transfers, cryptocurrency, "description");

            ArgumentCaptor<BitGoSendManyRequest> requestCaptor = ArgumentCaptor.forClass(BitGoSendManyRequest.class);
            verify(api).sendMany(eq(expectedBitGoCryptocurrency), eq("walletId"), requestCaptor.capture());

            BitGoSendManyRequest request = requestCaptor.getValue();

            assertEquals(1, request.recipients().size());
            BitGoSendManyRequest.BitGoRecipient recipient = request.recipients().get(0);
            assertEquals("destinationAddress", recipient.address());
            assertEquals(expectedAmount, recipient.amount());
            assertEquals(expectedTokenName, recipient.tokenName());
            assertEquals("walletPassphrase", request.walletPassphrase());
            assertEquals(2, request.numBlocks());
            assertEquals("description", request.comment());
            assertEquals(expectedType, request.type());
        }
    }

}
