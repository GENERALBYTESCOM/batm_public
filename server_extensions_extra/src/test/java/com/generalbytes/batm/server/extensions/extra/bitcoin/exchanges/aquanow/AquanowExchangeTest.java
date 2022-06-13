package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.BitcoinExtension;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.AquaNowExchange;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.knowm.xchange.utils.nonce.CurrentTimeIncrementalNonceFactory;
import si.mazi.rescu.RestInvocation;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.generalbytes.batm.common.currencies.CryptoCurrency.*;
import static com.generalbytes.batm.common.currencies.FiatCurrency.*;
import static com.generalbytes.batm.common.currencies.FiatCurrency.EUR;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;


@Ignore // requires online resources - for manual run only
//@RunWith(Parameterized.class)
public class AquanowExchangeTest {

    BitcoinExtension bitcoinExtension = new BitcoinExtension();

    private IExchangeAdvanced subject = (IExchangeAdvanced) bitcoinExtension.createExchange("aquanow:apikey:apisecret");


    @Test
    public void shouldFetchCryptoCurrencies() {
        System.out.println(subject.getCryptoCurrencies());
        assertNotNull(subject.getCryptoCurrencies());
    }

    @Test
    public void shouldFetchFiatCurrencies() {
        System.out.println(subject.getFiatCurrencies());
        assertNotNull(subject.getFiatCurrencies());
    }

    @Test
    public void shouldYieldConfiguredPreferredFiatCurrency() {
        assertEquals("CAD", subject.getPreferredFiatCurrency());
    }


    @Test
    public void shouldSellCoinsAdvanced() throws InterruptedException {
        final ITask task = subject.createSellCoinsTask(new BigDecimal("0.01"), "BTC", "CAD", "batm-sell-advanced-test");
        task.onCreate();
        for (int i = 0; i < 10 && !task.isFinished(); i++) {
            Thread.sleep(1000L);
            task.onDoStep();
            System.out.println(task.getResult());
        }
        System.out.println("result: "+task.getResult());
        assertTrue(task.isFinished());
    }


    @Test
    public void shouldPurchaseCoinsAdvanced() throws InterruptedException {
        final ITask task = subject.createPurchaseCoinsTask(new BigDecimal("0.01"), "BTC", "CAD","batm-purchase-advanced-test");
        task.onCreate();
        for (int i = 0; i < 10 && !task.isFinished(); i++) {
            Thread.sleep(1000L);
            task.onDoStep();
        }
        System.out.println("result"+task.getResult());
        assertTrue(task.isFinished());
    }

    @Test
    public void shouldGetBitcoinDepositAddress() {
        final String address = subject.getDepositAddress(BTC.getCode());
        System.out.println("address: "+ address);
        assertNotNull(address);
    }


    @Test
    public void shouldFetchFiatBalances() {
        Stream.of(CAD).forEach(currency -> {
            final BigDecimal balance = subject.getFiatBalance(CAD.getCode());
            assertNotNull("getFiatBalance(" + currency + ")", balance);
            assertTrue("negative balance", balance.compareTo(BigDecimal.ZERO) >= 0);
        });
    }

    @Test
    public void shouldFetchCryptoBalances() {
        Stream.of(BTC, BCH ).forEach(currency -> {
            final BigDecimal balance = subject.getCryptoBalance(currency.getCode());
            System.out.println(balance);
            assertNotNull("getCryptoBalance(" + currency + ")", balance);
            assertTrue("negative balance", balance.compareTo(BigDecimal.ZERO) >= 0);
        });
    }

    @Test
    public void shouldWithdrawBitcoin() {
        final String transaction = subject.sendCoins("moVHeF1GjAVG5kaoZQpVg7v8nJLMvYgjNP", new BigDecimal("0.02"), BTC.getCode(), "batm-test");
        System.out.println(transaction);
        //assertNotNull(transaction);
}



}