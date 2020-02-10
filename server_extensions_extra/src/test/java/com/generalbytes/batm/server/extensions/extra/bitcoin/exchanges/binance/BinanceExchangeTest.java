package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Ignore // requires online resources - for manual run only
@RunWith(Parameterized.class)
public class BinanceExchangeTest {

    private final IExchangeAdvanced exchange;

    // gets params from @Parameterized.Parameters annotated class
    public BinanceExchangeTest(IExchangeAdvanced exchange) {
        this.exchange = exchange;
    }

    @Parameterized.Parameters
    public static Collection params() {
        return Arrays.asList(new Object[][]{
//            {new BinanceUsExchange("", "", "USD")},
//            {new BinanceComExchange("", "", "USD")},
//            {new BinanceJerseyExchange("", "", "EUR")},
        });
    }

    // this tests getTradableAmount(). If amount sent to exchange had too many decimal places, it throwed "Filter failure: LOT_SIZE" message
    @Test
    public void testLotSizeSell() {
        try {
            String result = exchange.sellCoins(new BigDecimal("9.14155797"), "BTC", "USD", "");
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertEquals("Account has insufficient balance for requested action.", e.getMessage());
        }
    }

    @Test
    public void testLotSizeBuy() {
        try {
            String result = exchange.purchaseCoins(new BigDecimal("9.14155797"), "BTC", "USD", "");
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertEquals("Account has insufficient balance for requested action.", e.getMessage());
        }
    }

    @Test
    public void testInsufficientBalance() {
        try {
            String result = exchange.sellCoins(new BigDecimal("10"), "BTC", "USD", "");
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertEquals("Account has insufficient balance for requested action.", e.getMessage());
        }
    }


    @Test
    public void test() {
//        BinanceUsExchange rs = new BinanceUsExchange("USD");
//        System.out.println(rs.getExchangeRateLast("BTC", "USD"));
//        System.out.println(rs.getExchangeRateLast("LTC", "USD"));
//        System.out.println(rs.calculateBuyPrice("BTC", "USD", new BigDecimal("1")));
//        System.out.println(rs.calculateSellPrice("BTC", "USD", new BigDecimal("1")));

        System.out.println(exchange.getCryptoBalance("LTC"));
        System.out.println(exchange.getFiatBalance("USD"));
        System.out.println(exchange.getDepositAddress("LTC"));

//        System.out.println(exchange.sellCoins(new BigDecimal("0.14155797"), "BTC", "USD", ""));
//        System.out.println(xch.purchaseCoins(new BigDecimal("1"), "LTC", "EUR", ""));
//        System.out.println(xch.sendCoins("", new BigDecimal("0.01"), "LTC", ""));
    }

}