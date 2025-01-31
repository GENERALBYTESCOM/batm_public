package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.CBDigest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.ICoinbaseV2APILegacy;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccountResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddressesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBExchangeRatesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPaginatedResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBTransaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoinbaseV2ApiWrapperLegacyTest {

    private static final String API_KEY = "apiKey";
    private static final String API_VERSION = "apiVersion";
    private static final String SECRET_KEY = "secretKey";
    private ICoinbaseV2APILegacy api;
    private CBDigest coinbaseDigest;
    private CoinbaseV2ApiWrapperLegacy apiWrapper;

    @Before
    public void setup() {
        api = mock(ICoinbaseV2APILegacy.class);
        coinbaseDigest = mock(CBDigest.class);
        apiWrapper = new CoinbaseV2ApiWrapperLegacy(api, API_KEY, SECRET_KEY);
    }

    @Test
    public void testGetExchangeRates() {
        CBExchangeRatesResponse expectedResponse = mock(CBExchangeRatesResponse.class);
        when(api.getExchangeRates(anyString())).thenReturn(expectedResponse);

        CBExchangeRatesResponse response = api.getExchangeRates("CZK");

        assertEquals(expectedResponse, response);
        verify(api).getExchangeRates("CZK");
    }

    @Test
    public void testGetAccounts() {
        try (MockedStatic<CBDigest> mockedCbDigest = mockStatic(CBDigest.class)) {
            mockedCbDigest.when(() -> CBDigest.createInstance(anyString(), anyLong())).thenReturn(coinbaseDigest);

            CBPaginatedResponse<CBAccount> expectedResponse = mock(CBPaginatedResponse.class);
            when(api.getAccounts(any(), anyString(), any(), anyLong(), anyInt(), anyString())).thenReturn(expectedResponse);

            CBPaginatedResponse<CBAccount> response = apiWrapper.getAccounts(API_VERSION, 1L, 10, "startingAfterAccountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccounts(API_KEY, API_VERSION, coinbaseDigest, 1L, 10, "startingAfterAccountId");
            mockedCbDigest.verify(() -> CBDigest.createInstance(SECRET_KEY, 1L));
        }
    }

    @Test
    public void testGetAccountAddresses() {
        try (MockedStatic<CBDigest> mockedCbDigest = mockStatic(CBDigest.class)) {
            mockedCbDigest.when(() -> CBDigest.createInstance(anyString(), anyLong())).thenReturn(coinbaseDigest);

            CBAddressesResponse expectedResponse = mock(CBAddressesResponse.class);
            when(api.getAccountAddresses(any(), anyString(), any(), anyLong(), anyString())).thenReturn(expectedResponse);

            CBAddressesResponse response = apiWrapper.getAccountAddresses(API_VERSION, 1L, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccountAddresses(API_KEY, API_VERSION, coinbaseDigest, 1L, "accountId");
            mockedCbDigest.verify(() -> CBDigest.createInstance(SECRET_KEY, 1L));
        }
    }

    @Test
    public void testGetAccount() {
        try (MockedStatic<CBDigest> mockedCbDigest = mockStatic(CBDigest.class)) {
            mockedCbDigest.when(() -> CBDigest.createInstance(anyString(), anyLong())).thenReturn(coinbaseDigest);

            CBAccountResponse expectedResponse = mock(CBAccountResponse.class);
            when(api.getAccount(any(), anyString(), any(), anyLong(), anyString())).thenReturn(expectedResponse);

            CBAccountResponse response = apiWrapper.getAccount(API_VERSION, 1L, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccount(API_KEY, API_VERSION, coinbaseDigest, 1L, "accountId");
            mockedCbDigest.verify(() -> CBDigest.createInstance(SECRET_KEY, 1L));
        }
    }

    @Test
    public void testSend() {
        try (MockedStatic<CBDigest> mockedCbDigest = mockStatic(CBDigest.class)) {
            mockedCbDigest.when(() -> CBDigest.createInstance(anyString(), anyLong())).thenReturn(coinbaseDigest);

            CBSendResponse expectedResponse = mock(CBSendResponse.class);
            when(api.send(any(), anyString(), any(), anyLong(), anyString(), any())).thenReturn(expectedResponse);

            CBSendRequest request = new CBSendRequest();
            CBSendResponse response = apiWrapper.send(API_VERSION, 1L, "accountId", request);

            assertEquals(expectedResponse, response);
            verify(api).send(API_KEY, API_VERSION, coinbaseDigest, 1L, "accountId", request);
            mockedCbDigest.verify(() -> CBDigest.createInstance(SECRET_KEY, 1L));
        }
    }

    @Test
    public void testCreateAddress() {
        try (MockedStatic<CBDigest> mockedCbDigest = mockStatic(CBDigest.class)) {
            mockedCbDigest.when(() -> CBDigest.createInstance(anyString(), anyLong())).thenReturn(coinbaseDigest);

            CBCreateAddressResponse expectedResponse = mock(CBCreateAddressResponse.class);
            when(api.createAddress(any(), anyString(), any(), anyLong(), anyString(), any())).thenReturn(expectedResponse);

            CBCreateAddressRequest request = new CBCreateAddressRequest("address");
            CBCreateAddressResponse response = apiWrapper.createAddress(API_VERSION, 1L, "accountId", request);

            assertEquals(expectedResponse, response);
            verify(api).createAddress(API_KEY, API_VERSION, coinbaseDigest, 1L, "accountId", request);
            mockedCbDigest.verify(() -> CBDigest.createInstance(SECRET_KEY, 1L));
        }
    }

    @Test
    public void testGetAddressTransactions() {
        try (MockedStatic<CBDigest> mockedCbDigest = mockStatic(CBDigest.class)) {
            mockedCbDigest.when(() -> CBDigest.createInstance(anyString(), anyLong())).thenReturn(coinbaseDigest);

            CBPaginatedResponse<CBTransaction> expectedResponse = mock(CBPaginatedResponse.class);
            when(api.getAddressTransactions(any(), anyString(), any(), anyLong(), anyString(), anyString(), anyInt(), anyString()))
                    .thenReturn(expectedResponse);

            CBPaginatedResponse<CBTransaction> response = apiWrapper.getAddressTransactions(API_VERSION, 1L, "accountId",
                    "addressId", 10, "startingAfterTransactionId");

            assertEquals(expectedResponse, response);
            verify(api).getAddressTransactions(API_KEY, API_VERSION, coinbaseDigest, 1L, "accountId",
                    "addressId", 10, "startingAfterTransactionId");
            mockedCbDigest.verify(() -> CBDigest.createInstance(SECRET_KEY, 1L));
        }
    }

    @Test
    public void testGetAddresses() {
        try (MockedStatic<CBDigest> mockedCbDigest = mockStatic(CBDigest.class)) {
            mockedCbDigest.when(() -> CBDigest.createInstance(anyString(), anyLong())).thenReturn(coinbaseDigest);

            CBPaginatedResponse<CBAddress> expectedResponse = mock(CBPaginatedResponse.class);
            when(api.getAddresses(any(), anyString(), any(), anyLong(), anyString(), anyInt(), anyString())).thenReturn(expectedResponse);

            CBPaginatedResponse<CBAddress> response = apiWrapper.getAddresses(API_VERSION, 1L, "accountId", 10, "startingAfterAddressId");

            assertEquals(expectedResponse, response);
            verify(api).getAddresses(API_KEY, API_VERSION, coinbaseDigest, 1L, "accountId", 10, "startingAfterAddressId");
            mockedCbDigest.verify(() -> CBDigest.createInstance(SECRET_KEY, 1L));
        }
    }

}