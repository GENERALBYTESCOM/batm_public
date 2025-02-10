package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro;

import static com.generalbytes.batm.common.currencies.CryptoCurrency.ADA;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.BCH;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.BTC;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.DOGE;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.ETH;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.LTC;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.TRX;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.USDT;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.XRP;
import static com.generalbytes.batm.common.currencies.FiatCurrency.CHF;
import static com.generalbytes.batm.common.currencies.FiatCurrency.EUR;
import static com.generalbytes.batm.common.currencies.FiatCurrency.GBP;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.stream.Stream;

import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled // requires online resources - for manual run only
class BitpandaProExchangeTest {
    /** exchange sandbox */
    private static final URI API = URI.create("https://api.exchange.waskurzes.com");
    private static final String API_KEY = "YOUR.API.KEY";

    private final IExchangeAdvanced subject = new BitpandaProExchange(API, API_KEY, EUR.getCode());

    @Test
    void shouldFetchCryptoCurrencies() {
        assertNotNull(subject.getCryptoCurrencies());
    }

    @Test
    void shouldFetchFiatCurrencies() {
        assertNotNull(subject.getFiatCurrencies());
    }

    @Test
    void shouldYieldConfiguredPreferredFiatCurrency() {
        assertEquals("EUR", subject.getPreferredFiatCurrency());
    }

    @Test
    void shouldFetchFiatBalances() {
        Stream.of(EUR, CHF, GBP).forEach(currency -> {
            final BigDecimal balance = subject.getFiatBalance(EUR.getCode());
            assertNotNull(balance, "getFiatBalance(" + currency + ")");
            assertTrue(balance.compareTo(BigDecimal.ZERO) >= 0, "negative balance");
        });
    }

    @Test
    void shouldFetchCryptoBalances() {
        Stream.of(BTC, ETH, XRP, ADA, USDT, TRX, BCH, DOGE, LTC ).forEach(currency -> {
            final BigDecimal balance = subject.getCryptoBalance(currency.getCode());
            assertNotNull(balance, "getCryptoBalance(" + currency + ")");
            assertTrue(balance.compareTo(BigDecimal.ZERO) >= 0, "negative balance");
        });
    }

    @Test
    void shouldGetBitcoinDepositAddress() {
        final String address = subject.getDepositAddress(BTC.getCode());
        assertNotNull(address);
    }

    @Test
    void shouldWithdrawBitcoin() {
        final String transaction = subject.sendCoins("3FXtMi8gC2TUgzBoFnBxeRKWz1Gw8bSrbJ", BigDecimal.ONE, BTC.getCode(), "batm-test");
        assertNotNull(transaction);
    }

    @Test
    void shouldSellCoins() {
        final String orderId = subject.sellCoins(new BigDecimal("0.01"), BTC.getCode(), EUR.getCode(), "batm-sell-test");
        assertNotNull(orderId);
    }

    @Test
    void shouldSellCoinsAdvanced() throws InterruptedException {
        final ITask task = subject.createSellCoinsTask(new BigDecimal("0.02"), BTC.getCode(), EUR.getCode(), "batm-sell-advanced-test");
        task.onCreate();
        for (int i = 0; i < 10 && !task.isFinished(); i++) {
            Thread.sleep(1000L);
            task.onDoStep();
        }
        assertNotNull(task.getResult());
    }

    @Test
    void shouldPurchaseCoins() {
        final String orderId = subject.purchaseCoins(new BigDecimal("0.01"), BTC.getCode(), EUR.getCode(), "batm-purchase-test");
        assertNotNull(orderId);
    }

    @Test
    void shouldPurchaseCoinsAdvanced() throws InterruptedException {
        final ITask task = subject.createPurchaseCoinsTask(new BigDecimal("0.02"), BTC.getCode(), EUR.getCode(), "batm-purchase-advanced-test");
        task.onCreate();
        for (int i = 0; i < 10 && !task.isFinished(); i++) {
            Thread.sleep(1000L);
            task.onDoStep();
        }
        assertNotNull(task.getResult());
    }
}
