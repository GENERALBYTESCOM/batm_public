package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.CoinbaseDigest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.ICoinbaseAPI;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTimeResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoinbaseApiWrapperLegacyTest {

    private static final String API_KEY = "apiKey";
    private static final String API_VERSION = "apiVersion";
    private static final String SECRET_KEY = "secretKey";
    private ICoinbaseAPI api;
    private CoinbaseDigest coinbaseDigest;
    private CoinbaseApiWrapperLegacy apiWrapper;

    @Before
    public void setup() {
        api = mock(ICoinbaseAPI.class);
        coinbaseDigest = mock(CoinbaseDigest.class);
        apiWrapper = new CoinbaseApiWrapperLegacy(api, API_KEY, SECRET_KEY);
    }

    @Test
    public void testGetPrice() throws IOException {
        CBPriceResponse expectedResponse = mock(CBPriceResponse.class);
        when(api.getPrice(any(), anyString(), anyString())).thenReturn(expectedResponse);

        CBPriceResponse response = apiWrapper.getPrice(API_VERSION, "BTC-CZK", "buy");

        assertEquals(expectedResponse, response);
        verify(api).getPrice(API_VERSION, "BTC-CZK", "buy");
    }

    @Test
    public void testGetNewAddress() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBNewAddressResponse expectedResponse = mock(CBNewAddressResponse.class);
            when(api.getNewAddress(anyString(), anyString(), any(), anyString(), anyString())).thenReturn(expectedResponse);

            CBNewAddressResponse response = apiWrapper.getNewAddress(API_VERSION, "time", "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getNewAddress(API_VERSION, API_KEY, coinbaseDigest, "time", "accountId");
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testListPaymentMethods() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBPaymentMethodsResponse expectedResponse = mock(CBPaymentMethodsResponse.class);
            when(api.listPaymentMethods(any(), anyString(), any(), anyString())).thenReturn(expectedResponse);

            CBPaymentMethodsResponse response = apiWrapper.listPaymentMethods(API_VERSION, "time");

            assertEquals(expectedResponse, response);
            verify(api).listPaymentMethods(API_VERSION, API_KEY, coinbaseDigest, "time");
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testSendCoins() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBSendCoinsResponse expectedResponse = mock(CBSendCoinsResponse.class);
            when(api.sendCoins(any(), anyString(), any(), anyString(), anyString(), any())).thenReturn(expectedResponse);

            CBSendCoinsRequest request = new CBSendCoinsRequest();
            CBSendCoinsResponse response = apiWrapper.sendCoins(API_VERSION, "time", "accountId", request);

            assertEquals(expectedResponse, response);
            verify(api).sendCoins(API_VERSION, API_KEY, coinbaseDigest, "time", "accountId", request);
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testGetBuyOrder() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBOrderResponse expectedResponse = mock(CBOrderResponse.class);
            when(api.getBuyOrder(any(), anyString(), any(), anyString(), anyString(), any())).thenReturn(expectedResponse);

            CBOrderResponse response = apiWrapper.getBuyOrder(API_VERSION, "time", "accountId", "orderId");

            assertEquals(expectedResponse, response);
            verify(api).getBuyOrder(API_VERSION, API_KEY, coinbaseDigest, "time", "accountId", "orderId");
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testGetSellOrder() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBOrderResponse expectedResponse = mock(CBOrderResponse.class);
            when(api.getSellOrder(any(), anyString(), any(), anyString(), anyString(), any())).thenReturn(expectedResponse);

            CBOrderResponse response = apiWrapper.getSellOrder(API_VERSION, "time", "accountId", "orderId");

            assertEquals(expectedResponse, response);
            verify(api).getSellOrder(API_VERSION, API_KEY, coinbaseDigest, "time", "accountId", "orderId");
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testBuyCoins() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBOrderResponse expectedResponse = mock(CBOrderResponse.class);
            when(api.buyCoins(any(), anyString(), any(), anyString(), anyString(), any())).thenReturn(expectedResponse);

            CBOrderRequest request = new CBOrderRequest();
            CBOrderResponse response = apiWrapper.buyCoins(API_VERSION, "time", "accountId", request);

            assertEquals(expectedResponse, response);
            verify(api).buyCoins(API_VERSION, API_KEY, coinbaseDigest, "time", "accountId", request);
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testSellCoins() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBOrderResponse expectedResponse = mock(CBOrderResponse.class);
            when(api.sellCoins(any(), anyString(), any(), anyString(), anyString(), any())).thenReturn(expectedResponse);

            CBOrderRequest request = new CBOrderRequest();
            CBOrderResponse response = apiWrapper.sellCoins(API_VERSION, "time", "accountId", request);

            assertEquals(expectedResponse, response);
            verify(api).sellCoins(API_VERSION, API_KEY, coinbaseDigest, "time", "accountId", request);
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testGetAccounts() throws IOException {
        try (MockedStatic<CoinbaseDigest> mockedCoinbaseDigest = mockStatic(CoinbaseDigest.class)) {
            mockedCoinbaseDigest.when(() -> CoinbaseDigest.createInstance(anyString())).thenReturn(coinbaseDigest);

            CBAccountsResponse expectedResponse = mock(CBAccountsResponse.class);
            when(api.getAccounts(any(), anyString(), any(), anyString(), any())).thenReturn(expectedResponse);

            CBAccountsResponse response = apiWrapper.getAccounts(API_VERSION, "time", "startingAfterAccountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccounts(API_VERSION, API_KEY, coinbaseDigest, "time", "startingAfterAccountId");
            mockedCoinbaseDigest.verify(() -> CoinbaseDigest.createInstance(SECRET_KEY));
        }
    }

    @Test
    public void testGetTime() throws IOException {
        CBTimeResponse expectedResponse = mock(CBTimeResponse.class);
        when(api.getTime(anyString())).thenReturn(expectedResponse);

        CBTimeResponse response = apiWrapper.getTime(API_VERSION);

        assertEquals(expectedResponse, response);
        verify(api).getTime(API_VERSION);
    }

}