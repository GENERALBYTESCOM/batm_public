package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.okx;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.knowm.xchange.dto.account.AccountInfo;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OkxExchangeTest {

    private OkxExchange okxExchange;

    @BeforeEach
    void setUp() {
        okxExchange = new OkxExchange("USD");
    }

    @Test
    void testSupportedCryptoCurrencies() {
        Set<String> expectedCryptoCurrencies = Set.of(
            CryptoCurrency.BTC.getCode(),
            CryptoCurrency.DOGE.getCode(),
            CryptoCurrency.ETH.getCode(),
            CryptoCurrency.LTC.getCode(),
            CryptoCurrency.USDT.getCode()
        );
        assertEquals(expectedCryptoCurrencies, okxExchange.getCryptoCurrencies());
    }

    @Test
    void testSupportedFiatCurrencies() {
        Set<String> expectedFiatCurrencies = Set.of(
            FiatCurrency.BRL.getCode(),
            FiatCurrency.EUR.getCode(),
            FiatCurrency.AUD.getCode(),
            FiatCurrency.AED.getCode(),
            FiatCurrency.SGD.getCode(),
            FiatCurrency.USD.getCode()
        );
        assertEquals(expectedFiatCurrencies, okxExchange.getFiatCurrencies());
    }

    @Test
    void testGetWallet() {
        AccountInfo accountInfo = mock(AccountInfo.class);
        okxExchange.getWallet(accountInfo, "not important");
        verify(accountInfo).getWallet("trading");
    }

    @Test
    void testGetAllowedCallsPerSecond() {
        assertEquals(10, okxExchange.getAllowedCallsPerSecond());
    }

    @ParameterizedTest
    @ValueSource(strings = {"someResult", "", " "})
    @NullSource
    void testIsWithdrawSuccessful(String input) {
        assertEquals(input != null, okxExchange.isWithdrawSuccessful(input));
    }
}