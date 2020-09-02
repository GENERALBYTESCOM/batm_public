package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.List;

public class CryptXWalletTest {

    private static final Logger log = LoggerFactory.getLogger(CryptXWalletTest.class);

    private static ICryptXAPI api;

    private static CryptXWithUniqueAddresses wallet;

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

        api = RestProxyFactory.createProxy(ICryptXAPI.class, "http://localhost:3080/");

        String scheme = "http";
        String host = "10.10.2.5";
        int port = 8080;
        String token = "e0b6d079081282674449888d78e307304eb5533ef1b8dd4134afa2508c48029a";
        String walletId = "86823de0-e06c-4cb0-9894-c72bf71948a2";

        wallet = new CryptXWithUniqueAddresses(scheme, host, port, token, walletId);
    }

    @Test
    public void getCryptoAddressTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        String cryptoAddress = wallet.getCryptoAddress(coin);
        Assert.assertNotNull(cryptoAddress);
        Assert.assertEquals(cryptoAddress, "tb1ql64rl90ar2rlt5sk3xxn03ur8l3ef23zegtqqy");
    }

    @Test
    public void getCryptoBalanceTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        BigDecimal balance = wallet.getCryptoBalance(coin);
        Assert.assertNotNull(balance);
        Assert.assertEquals(balance, BigDecimal.valueOf(10033695, 8));
    }

    @Test
    public void createCryptoAddressTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        String batm_test = wallet.generateNewDepositCryptoAddress(coin, "BATM_TEST");
        Assert.assertNotNull(batm_test);
    }

    @Test
    public void sendCoinsTest() {
        String coin = CryptoCurrency.TBTC.getCode();
        String txId = wallet.sendCoins("tb1qjfejvrtghh2zds8u6qrndt6dlu3fmf32xvpyg8", BigDecimal.valueOf(10, 8), coin, "UNIQUE_1");
        Assert.assertNotNull(txId);
    }

}
