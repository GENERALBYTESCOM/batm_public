package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.server.extensions.ITask;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

@Ignore // requires online resources - for manual run only
public class StillmanDigitalExchangeTest {
    private static final String PUBLIC_KEY = "9QU20A1IO3QN2L6ND90JBG80";
    private static final String PRIVATE_KEY = "A2CBOQMJKJ69O59ZC9SKVY58M9NCNQQXEOF9W1Y0DSF56GBI";
    private static final String BASE_URL = "https://sandbox-api.stillmandigital.com";

    private static StillmanDigitalExchange exchange;

    @BeforeClass
    public static void createExchange() throws GeneralSecurityException {
        exchange = new StillmanDigitalExchange(PUBLIC_KEY, PRIVATE_KEY, BASE_URL);
    }

    @Test
    public void getFiatBalanceTest() {
        System.out.println("USD balance: " + exchange.getFiatBalance("USD"));
    }

    @Test
    public void getCryptoBalanceTest() {
        System.out.println("Crypto balance: " + exchange.getFiatBalance("BTC"));
    }

    @Test
    public void getRatesTest() {
        System.out.println("Buy rate: " + exchange.getExchangeRateForBuy("BTC", "USD"));
        System.out.println("Sell rate: " + exchange.getExchangeRateForSell("BTC", "USD"));
        System.out.println("Buy rate for 1 BTC: " + exchange.calculateBuyPrice("BTC", "USD", BigDecimal.ONE));
        System.out.println("Sell rate for 1 BTC: " + exchange.calculateSellPrice("BTC", "USD", BigDecimal.ONE));
    }


    @Test
    public void createOrderTest() throws InterruptedException {
        ITask task = exchange.createPurchaseCoinsTask(BigDecimal.valueOf(0.01), "BTC", "USD", null);
        task.onCreate();
        for (int i = 0; i < 10 && !task.isFinished(); i++) {
            Thread.sleep(1000L);
            task.onDoStep();
        }
        assertNotNull(task.getResult());
        System.out.println("Task result: " + task.getResult());
    }


}
