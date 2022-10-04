package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.common.currencies.FiatCurrency;
import org.junit.BeforeClass;
import org.junit.Test;

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


}
