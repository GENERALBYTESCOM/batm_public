package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled // requires online resources - for manual run only
class BinanceExchangeTest {

    private final IExchangeAdvanced exchange;

    // gets params from @Parameterized.Parameters annotated class
    public BinanceExchangeTest(IExchangeAdvanced exchange) {
        this.exchange = exchange;
    }

    // this tests getTradableAmount(). If amount sent to exchange had too many decimal places, it throwed "Filter failure: LOT_SIZE" message
    @Test
    void testLotSizeSell() {
        try {
            String result = exchange.sellCoins(new BigDecimal("9.14155797"), "BTC", "USD", "");
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertEquals("Account has insufficient balance for requested action.", e.getMessage());
        }
    }

    @Test
    void testLotSizeBuy() {
        try {
            String result = exchange.purchaseCoins(new BigDecimal("9.14155797"), "BTC", "USD", "");
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertEquals("Account has insufficient balance for requested action.", e.getMessage());
        }
    }

    @Test
    void testInsufficientBalance() {
        try {
            String result = exchange.sellCoins(new BigDecimal("10"), "BTC", "USD", "");
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertEquals("Account has insufficient balance for requested action.", e.getMessage());
        }
    }


    @Test
    void test() {
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