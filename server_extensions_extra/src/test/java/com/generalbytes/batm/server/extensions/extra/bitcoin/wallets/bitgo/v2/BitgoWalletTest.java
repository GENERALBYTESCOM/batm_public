package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.generalbytes.batm.server.extensions.Converters;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ICanSendMany;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class BitgoWalletTest {

    private static final Logger log = LoggerFactory.getLogger(BitgoWalletTest.class);

    private static IBitgoAPI api;

    private static BitgoWallet wallet;

    private static BitgoWallet walletParams;

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

    @BeforeClass
    public static void setup() {
        setLoggerLevel("batm", "trace");
        setLoggerLevel("si.mazi.rescu","trace");

        api = RestProxyFactory.createProxy(IBitgoAPI.class, "http://localhost:3080/");

        String scheme = "http";
        String host = "localhost";
        int port = 3080;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";
        int numBlocks = 10;

        wallet = new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase);
        walletParams = new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase, numBlocks);
    }

    @Test
    @Ignore
    public void getCryptAddressTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        String scheme = "https";
        String host = "test.bitgo.com";
        int port = 443;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

        final BitgoWallet remotewallet = new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase);
        final String address = remotewallet.getCryptoAddress(coin);
        Assert.assertNotNull(address);
        Assert.assertEquals("2N2WR6aVSEgq5ZLTED9vHvCWFdAMf6yhebd", address);
    }

    @Test
    @Ignore
    public void getCryptBalanceTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        String scheme = "https";
        String host = "test.bitgo.com";
        int port = 443;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

        final BitgoWallet remotewallet = new BitgoWallet(scheme, host, port, token, walletId, walletPassphrase);
        BigDecimal balance = remotewallet.getCryptoBalance(coin);
        Assert.assertNotNull(balance);
        log.info("balance = {}", balance);
    }

    @Test
    @Ignore("Local instance of bitgo-express is required to run")
    public void sendCoinsTest() {
        String destinationAddress = "2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi";
        String coin = CryptoCurrency.TBTC.getCode();
        Integer amountInt = 10000;
        BigDecimal amount = BigDecimal.valueOf(amountInt).divide(Converters.TBTC);
        String description = null;

        String result = wallet.sendCoins(destinationAddress, amount, coin, description);
        log.info("send coins status = {}", result);
    }

    @Test
    @Ignore("Local instance of bitgo-express is required to run")
    public void sendManyTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        Integer amountInt = 10000;
        BigDecimal amount = BigDecimal.valueOf(amountInt).divide(Converters.TBTC);
        String description = null;

        String result = ((ICanSendMany) wallet).sendMany(Arrays.asList(
            new ICanSendMany.Transfer("2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi", new BigDecimal("0.0001")),
            new ICanSendMany.Transfer("2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi", new BigDecimal("0.002"))
        ), coin, "test send to self");
        log.info("send coins status = {}", result);
    }

    @Test
    @Ignore("Local instance of bitgo-express is required to run")
    public void sendCoinsNumBlocksTest() {
        String destinationAddress = "2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi";
        String coin = CryptoCurrency.TBTC.getCode();
        Integer amountInt = 10000;
        BigDecimal amount = BigDecimal.valueOf(amountInt).divide(Converters.TBTC);
        String description = "CAS: uses the numBlocks API parameter";

        String result = walletParams.sendCoins(destinationAddress, amount, coin, description);
        log.info("send coins with numBlocks parameter status = {}", result);
    }

    @Test
    public void convertBalance() {
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("15.50581145"), CryptoCurrency.USDT.getCode())).isEqualTo("15505811");

        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.00000001"), CryptoCurrency.BTC.getCode())).isEqualTo("1");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.00000001000"), CryptoCurrency.LTC.getCode())).isEqualTo("1");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000000000000001000"), CryptoCurrency.ETH.getCode())).isEqualTo("1");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000001000"), CryptoCurrency.USDT.getCode())).isEqualTo("1");

        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000000000"), CryptoCurrency.BTC.getCode())).isEqualTo("0");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000009"), CryptoCurrency.BTC.getCode())).isEqualTo("0");
        Assertions.assertThat(wallet.toSatoshis(new BigDecimal("0.000000019"), CryptoCurrency.BTC.getCode())).isEqualTo("1");

        Assertions.assertThat(wallet.fromSatoshis(CryptoCurrency.BTC.getCode(), new BigDecimal("1.000"))).isEqualByComparingTo("0.00000001");
        Assertions.assertThat(wallet.fromSatoshis(CryptoCurrency.BTC.getCode(), new BigDecimal("1.999"))).isEqualByComparingTo("0.00000001");

        Assert.assertThrows(IllegalArgumentException.class, () -> wallet.fromSatoshis("unknown", BigDecimal.ONE));
        for (String cryptoCurrency : wallet.getCryptoCurrencies()) {
            Assert.assertEquals(BigDecimal.ONE, wallet.fromSatoshis(cryptoCurrency, new BigDecimal(wallet.toSatoshis(BigDecimal.ONE, cryptoCurrency))));
        }
    }
}
