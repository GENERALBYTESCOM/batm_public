package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.dto.account.BinanceCurrencyInfo;
import org.knowm.xchange.binance.service.BinanceAccountService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.service.trade.params.NetworkWithdrawFundsParams;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BinanceExchangeSendCoinsTest {

    private static final String ADDRESS = "0x0000000000000000000000000000000000000001";

    private final Exchange xchange = mock(Exchange.class);
    private final BinanceAccountService accountService = mock(BinanceAccountService.class);
    private final BinanceComExchange binance = new BinanceComExchange("apiKey", "secretKey", "USD");

    @BeforeEach
    void setUp() throws Exception {
        ExchangeMetaData metaData = mock(ExchangeMetaData.class);
        when(metaData.getCurrencies()).thenReturn(Map.of(Currency.USDT, new CurrencyMetaData(8, new BigDecimal("0.01"))));
        when(xchange.getExchangeMetaData()).thenReturn(metaData);
        when(xchange.getAccountService()).thenReturn(accountService);
        when(accountService.currencyInfos()).thenReturn(List.of(BinanceCurrencyInfo.builder()
            .currency(Currency.USDT)
            .networks(List.of(
                network("BSC", "0.01", true),
                network("ETH", "0.40", false),
                network("TRX", "1.50", false)))
            .build()));
        when(accountService.withdrawFunds(any(WithdrawFundsParams.class))).thenReturn("withdrawalId");

        // getWithdrawalFee() reads the field directly, not via getExchange()
        Field exchangeField = XChangeExchange.class.getDeclaredField("exchange");
        exchangeField.setAccessible(true);
        exchangeField.set(binance, xchange);
    }

    @ParameterizedTest
    @CsvSource({
        "USDT,     187.64689292, ETH, 188.046892",
        "USDTTRON, 412.69417411, TRX, 414.194174",
    })
    void testSendCoins_networkFee(String cryptoCurrency, BigDecimal receiptAmount, String network, BigDecimal expectedAmount)
        throws Exception {
        String result = binance.sendCoins(ADDRESS, receiptAmount, cryptoCurrency, "txId");

        assertThat(result).isEqualTo("success");
        ArgumentCaptor<WithdrawFundsParams> params = ArgumentCaptor.forClass(WithdrawFundsParams.class);
        verify(accountService, times(1)).withdrawFunds(params.capture());
        NetworkWithdrawFundsParams withdrawal = (NetworkWithdrawFundsParams) params.getValue();
        assertThat(withdrawal.getNetwork()).isEqualTo(network);
        assertThat(withdrawal.getAmount()).isEqualByComparingTo(expectedAmount);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USDT", "USDTTRON"})
    void testSendCoins_networkFeeUnavailable(String cryptoCurrency) throws Exception {
        when(accountService.currencyInfos()).thenThrow(new IOException("Binance unavailable"));

        catchThrowable(() -> binance.sendCoins(ADDRESS, new BigDecimal("100"), cryptoCurrency, "txId"));

        verify(accountService, never()).withdrawFunds(any(WithdrawFundsParams.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"USDT", "USDTTRON"})
    void testSendCoins_networkMissing(String cryptoCurrency) throws Exception {
        when(accountService.currencyInfos()).thenReturn(List.of(BinanceCurrencyInfo.builder()
            .currency(Currency.USDT)
            .networks(List.of(network("BSC", "0.01", true)))
            .build()));

        catchThrowable(() -> binance.sendCoins(ADDRESS, new BigDecimal("100"), cryptoCurrency, "txId"));

        verify(accountService, never()).withdrawFunds(any(WithdrawFundsParams.class));
    }

    private static BinanceCurrencyInfo.Network network(String id, String withdrawFee, boolean isDefault) {
        return BinanceCurrencyInfo.Network.builder().id(id).withdrawFee(new BigDecimal(withdrawFee)).isDefault(isDefault).build();
    }
}
