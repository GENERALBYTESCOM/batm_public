package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitstamp;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ITask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BitstampExchangeTest {

    @Mock
    private AccountService accountService;
    @Mock
    private MarketDataService marketDataService;
    @Mock
    private TradeService tradeService;

    private Exchange knowmExchange;
    private BitstampExchange exchange;

    @BeforeEach
    void setUp() {
        exchange = new BitstampExchange("CZK", "userId", "key", "secret");

        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(org.knowm.xchange.bitstamp.BitstampExchange.class);
        try (
            MockedConstruction<org.knowm.xchange.bitstamp.BitstampExchange> mockedExchangeConstruction = mockExchangeConstruction(exchangeSpecification)) {
            exchange.getCryptoBalance("ETH"); // Trigger knowm exchange creation

            knowmExchange = mockedExchangeConstruction.constructed().get(0);
        }
    }

    private MockedConstruction<org.knowm.xchange.bitstamp.BitstampExchange> mockExchangeConstruction(ExchangeSpecification exchangeSpecification) {
        return mockConstruction(org.knowm.xchange.bitstamp.BitstampExchange.class, (mock, context) -> {
            when(mock.getExchangeSpecification()).thenReturn(exchangeSpecification);
            when(mock.getAccountService()).thenReturn(accountService);
            AccountInfo accountInfo = mock(AccountInfo.class);
            when(accountService.getAccountInfo()).thenReturn(accountInfo);
            Wallet wallet = mock(Wallet.class);
            when(accountInfo.getWallet()).thenReturn(wallet);
            Balance balance = mock(Balance.class);
            when(wallet.getBalance(any())).thenReturn(balance);
            when(balance.getAvailable()).thenReturn(BigDecimal.valueOf(100_000_000L));
        });
    }

    @Test
    void testSendCoins_unsupportedCryptoCurrency() {
        assertNull(exchange.sendCoins("destinationAddress", BigDecimal.TEN, "unsupportedCryptoCurrency", "test"));
    }

    @Test
    void testGetCryptoCurrencies() {
        Set<String> cryptoCurrencies = exchange.getCryptoCurrencies();

        assertNotNull(cryptoCurrencies);
        assertEquals(5, cryptoCurrencies.size());
        assertTrue(cryptoCurrencies.contains(CryptoCurrency.BTC.getCode()));
        assertTrue(cryptoCurrencies.contains(CryptoCurrency.ETH.getCode()));
        assertTrue(cryptoCurrencies.contains(CryptoCurrency.LTC.getCode()));
        assertTrue(cryptoCurrencies.contains(CryptoCurrency.BCH.getCode()));
        assertTrue(cryptoCurrencies.contains(CryptoCurrency.XRP.getCode()));
    }

    private static Object[][] provideInvalidParametersForCreateTask() {
        return new Object[][]{
            {null, null},
            {null, "USD"},
            {"BTC", null},
            {"unsupportedCryptoCurrency", "USD"},
            {"BTC", "unsupportedFiatCurrency"},
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParametersForCreateTask")
    void testCreatePurchaseCoinsTask_invalidParameters(String cryptocurrency, String fiatCurrency) {
        ITask task = exchange.createPurchaseCoinsTask(BigDecimal.ONE, cryptocurrency, fiatCurrency, null);

        assertNull(task);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParametersForCreateTask")
    void testCreateSellCoinsTask_invalidParameters(String cryptocurrency, String fiatCurrency) {
        ITask task = exchange.createSellCoinsTask(BigDecimal.ONE, cryptocurrency, fiatCurrency, null);

        assertNull(task);
    }

    private static Object[][] provideCryptocurrenciesAndAmounts() {
        return new Object[][]{
            {CryptoCurrency.BTC.getCode(), BigDecimal.valueOf(0.002012945678)},
            {CryptoCurrency.ETH.getCode(), BigDecimal.valueOf(0.002012)}, // floored to 6 decimals
        };
    }

    @ParameterizedTest
    @MethodSource("provideCryptocurrenciesAndAmounts")
    void testCreatePurchaseCoinsTask(String cryptocurrency, BigDecimal expectedAmount) throws IOException {
        when(knowmExchange.getMarketDataService()).thenReturn(marketDataService);
        when(knowmExchange.getTradeService()).thenReturn(tradeService);
        CurrencyPair currencyPair = new CurrencyPair(cryptocurrency, "USD");
        OrderBook orderBook = mock(OrderBook.class);
        when(marketDataService.getOrderBook(currencyPair)).thenReturn(orderBook);
        when(orderBook.getAsks()).thenReturn(List.of(
            new LimitOrder(LimitOrder.OrderType.ASK, BigDecimal.valueOf(10), currencyPair, "", null, BigDecimal.valueOf(12))
        ));
        when(tradeService.placeLimitOrder(any(LimitOrder.class))).thenReturn("testOrderId");

        ITask task = exchange.createPurchaseCoinsTask(BigDecimal.valueOf(0.002012945678), cryptocurrency, "USD", "test");
        assertNotNull(task);
        assertTrue(task.onCreate());

        ArgumentCaptor<LimitOrder> limitOrderCaptor = ArgumentCaptor.forClass(LimitOrder.class);
        verify(tradeService).placeLimitOrder(limitOrderCaptor.capture());
        LimitOrder order = limitOrderCaptor.getValue();
        assertEquals(expectedAmount, order.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(12), order.getLimitPrice());
        assertEquals(cryptocurrency, order.getCurrencyPair().getBase().getCurrencyCode());
        assertEquals("USD", order.getCurrencyPair().getCounter().getCurrencyCode());
        assertEquals("", order.getId());
    }

    @ParameterizedTest
    @MethodSource("provideCryptocurrenciesAndAmounts")
    void testCreateSellCoinsTask(String cryptocurrency, BigDecimal expectedAmount) throws IOException {
        when(knowmExchange.getMarketDataService()).thenReturn(marketDataService);
        when(knowmExchange.getTradeService()).thenReturn(tradeService);
        CurrencyPair currencyPair = new CurrencyPair(cryptocurrency, "USD");
        OrderBook orderBook = mock(OrderBook.class);
        when(marketDataService.getOrderBook(currencyPair)).thenReturn(orderBook);
        when(orderBook.getBids()).thenReturn(List.of(
            new LimitOrder(LimitOrder.OrderType.BID, BigDecimal.valueOf(10), currencyPair, "", null, BigDecimal.valueOf(12))
        ));
        when(tradeService.placeLimitOrder(any(LimitOrder.class))).thenReturn("testOrderId");

        ITask task = exchange.createSellCoinsTask(BigDecimal.valueOf(0.002012945678), cryptocurrency, "USD", "test");
        assertNotNull(task);
        assertTrue(task.onCreate());

        ArgumentCaptor<LimitOrder> limitOrderCaptor = ArgumentCaptor.forClass(LimitOrder.class);
        verify(tradeService).placeLimitOrder(limitOrderCaptor.capture());
        LimitOrder order = limitOrderCaptor.getValue();
        assertEquals(expectedAmount, order.getOriginalAmount());
        assertEquals(BigDecimal.valueOf(12), order.getLimitPrice());
        assertEquals(cryptocurrency, order.getCurrencyPair().getBase().getCurrencyCode());
        assertEquals("USD", order.getCurrencyPair().getCounter().getCurrencyCode());
        assertEquals("", order.getId());
    }
}