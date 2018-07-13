package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.server.extensions.Converters;
import com.generalbytes.batm.server.extensions.Currencies;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;


public class BitgoWalletTest {

    private static final Logger log = LoggerFactory.getLogger(BitgoWalletTest.class);

    private static IBitgoAPI api;

    private static BitgoWallet wallet;

    @BeforeClass
    public static void setup() {
        System.setProperty("org.slf4j.simpleLogger.log.batm", "trace");
        System.setProperty("org.slf4j.simpleLogger.log.si.mazi.rescu","trace");

        api = RestProxyFactory.createProxy(IBitgoAPI.class, "http://localhost:3080/api");

        String host = "http://localhost";
        String port = "3080";
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

        wallet = new BitgoWallet(host, port, token, walletId, walletPassphrase);
    }

    @Test
    @Ignore
    public void getCryptAddressTest() {
        String coin = Currencies.TBTC;
        String host = "https://test.bitgo.com";
        String port = null;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

        final BitgoWallet remotewallet = new BitgoWallet(host, port, token, walletId, walletPassphrase);
        final String address = remotewallet.getCryptoAddress(coin);
        Assert.assertNotNull(address);
        Assert.assertEquals("2N2WR6aVSEgq5ZLTED9vHvCWFdAMf6yhebd", address);
    }

    @Test
    @Ignore
    public void getCryptBalanceTest() {
        String coin = Currencies.TBTC;
        String host = "https://test.bitgo.com";
        String port = null;
        String token = "v2x8d5e9e46379dc328b2039a400a12b04ea986689b38107fd84cd339bc89e3fb21";
        String walletId = "5b20e3a9266bbe80095757489d84a6bb";
        String walletPassphrase = "JSZSuGNlHfgqPHjrp0eO";

        final BitgoWallet remotewallet = new BitgoWallet(host, port, token, walletId, walletPassphrase);
        BigDecimal balance = remotewallet.getCryptoBalance(coin);
        Assert.assertNotNull(balance);
        log.info("balance = {}", balance);
    }

    @Test
    @Ignore("Local instance of bitgo-express is required to run")
    public void sendCoinsTest() {
        String destinationAddress = "2N5q4MwNSUxbAtaidhRgkiDrbwVR4yCZDhi";
        String coin = Currencies.TBTC;
        Integer amountInt = 10000;
        BigDecimal amount = BigDecimal.valueOf(amountInt).divide(Converters.TBTC);
        String description = null;

        String result = wallet.sendCoins(destinationAddress, amount, coin, description);
        log.info("send coins status = {}", result);
    }
}
