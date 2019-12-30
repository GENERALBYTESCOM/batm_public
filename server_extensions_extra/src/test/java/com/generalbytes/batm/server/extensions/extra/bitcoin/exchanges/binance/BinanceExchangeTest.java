package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import org.junit.Ignore;
import org.junit.Test;

public class BinanceExchangeTest {
    @Ignore
    @Test
    public void test() {
//        BinanceUsExchange rs = new BinanceUsExchange("USD");
//        System.out.println(rs.getExchangeRateLast("BTC", "USD"));
//        System.out.println(rs.getExchangeRateLast("LTC", "USD"));
//        System.out.println(rs.calculateBuyPrice("BTC", "USD", new BigDecimal("1")));
//        System.out.println(rs.calculateSellPrice("BTC", "USD", new BigDecimal("1")));

        for (IExchangeAdvanced xch : new IExchangeAdvanced[]{
            new BinanceUsExchange("", "", "USD"),
            new BinanceJerseyExchange("", "", "EUR"),
            new BinanceComExchange("", "", "USD"),
        }) {
            System.out.println(xch.getCryptoBalance("LTC"));
            System.out.println(xch.getFiatBalance("USD"));

            System.out.println(xch.getDepositAddress("LTC"));

//        System.out.println(xch.sellCoins(new BigDecimal("1"), "LTC", "EUR", ""));
//        System.out.println(xch.purchaseCoins(new BigDecimal("1"), "LTC", "EUR", ""));
//        System.out.println(xch.sendCoins("", new BigDecimal("0.01"), "LTC", ""));
        }
    }
}