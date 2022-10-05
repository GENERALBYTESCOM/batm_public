package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.common.currencies.FiatCurrency;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;

//@Ignore // requires online resources - for manual run only
public class StillmanDigitalExchangeTest {
    private static final String PUBLIC_KEY = "";
    private static final String PRIVATE_KEY = "";
    private static final String BASE_URL = "https://ci.trading.stillmandigital.com/client-api";

    private static StillmanDigitalExchange exchange;

    @BeforeClass
    public static void createExchange() throws GeneralSecurityException {
        exchange = new StillmanDigitalExchange(PUBLIC_KEY, PRIVATE_KEY, FiatCurrency.USD.getCode(), BASE_URL);
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
    public void sendCoinsTest() {
        String result = exchange.sendCoins("mv4rnyY3Su5gjcDNzbMLKBQkBicCtHUtFB", BigDecimal.valueOf(0.001), "ETH", "test");
        System.out.println("Send id: " + result);
    }


}
