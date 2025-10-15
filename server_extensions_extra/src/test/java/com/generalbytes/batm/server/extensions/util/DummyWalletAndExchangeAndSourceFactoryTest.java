package com.generalbytes.batm.server.extensions.util;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DummyWalletAndExchangeAndSourceFactoryTest {

    private DummyWalletAndExchangeAndSourceFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DummyWalletAndExchangeAndSourceFactory();
    }

    @Test
    void createDummyWithFiatCurrencyAndAddress() {
        String parameterString = "CZK:address";
        StringTokenizer parameters = new StringTokenizer(parameterString, ":");

        DummyExchangeAndWalletAndSource result = factory.createDummyWithFiatCurrencyAndAddress(parameters, CryptoCurrency.USDC);

        assertEquals(Set.of(CryptoCurrency.USDC.getCode()), result.getCryptoCurrencies());
        assertEquals(Set.of("CZK"), result.getFiatCurrencies());
        assertEquals("address", result.getCryptoAddress(CryptoCurrency.USDC.getCode()));
    }

    @Test
    void createDummyWithFiatCurrencyAndAddress_noAddress() {
        String parameterString = "CZK";
        StringTokenizer parameters = new StringTokenizer(parameterString, ":");

        assertThrows(NoSuchElementException.class, () -> factory.createDummyWithFiatCurrencyAndAddress(parameters, CryptoCurrency.USDC));
    }

    @Test
    void createDummyWithFiatCurrencyAndAddress_noFiatCurrency() {
        String parameterString = "";
        StringTokenizer parameters = new StringTokenizer(parameterString, ":");

        assertThrows(NoSuchElementException.class, () -> factory.createDummyWithFiatCurrencyAndAddress(parameters, CryptoCurrency.USDC));
    }
}