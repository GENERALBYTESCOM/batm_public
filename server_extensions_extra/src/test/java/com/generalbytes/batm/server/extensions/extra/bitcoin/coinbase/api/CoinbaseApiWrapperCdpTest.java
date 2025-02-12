package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseServerTimeResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoinbaseApiWrapperCdpTest {

    private static final String API_VERSION = "does_not_matter_here";
    private static final String PRIVATE_KEY = "privateKey";
    private static final String KEY_NAME = "keyName";

    private ICoinbaseV3Api api;
    private CoinbaseApiWrapper apiWrapper;

    @Before
    public void setUp() {
        api = mock(ICoinbaseV3Api.class);
        apiWrapper = new CoinbaseApiWrapperCdp(api, PRIVATE_KEY, KEY_NAME);
    }

    @Test
    public void testGetPrice_valid() throws IOException {
        CBPriceResponse expectedResponse = mock(CBPriceResponse.class);
        CoinbasePriceResponse apiResponse = mock(CoinbasePriceResponse.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapPriceResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getPrice(anyString(), anyString())).thenReturn(apiResponse);

            CBPriceResponse response = apiWrapper.getPrice(API_VERSION, "BTC-USD", "buy");

            assertEquals(expectedResponse, response);
            verify(api).getPrice("BTC-USD", "buy");
            mockedMapper.verify(() -> CoinbaseApiMapper.mapPriceResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetPrice_exception() throws IOException {
        CBPriceResponse expectedResponse = mock(CBPriceResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getPrice(anyString(), anyString())).thenThrow(exception);

            CBPriceResponse response = apiWrapper.getPrice(API_VERSION, "BTC-USD", "buy");

            assertEquals(expectedResponse, response);
            verify(api).getPrice("BTC-USD", "buy");
            mockedMapper.verify(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBPriceResponse.class)));
        }
    }

    @Test
    public void testGetNewAddress_valid() throws IOException {
        CBNewAddressResponse expectedResponse = mock(CBNewAddressResponse.class);
        CoinbaseCreateAddressResponse apiResponse = mock(CoinbaseCreateAddressResponse.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapCreateAddressResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.createAddress(any(), anyString(), any())).thenReturn(apiResponse);

            CBNewAddressResponse response = apiWrapper.getNewAddress(API_VERSION, "coinbaseTime", "accountId");

            assertEquals(expectedResponse, response);
            verify(api).createAddress(any(CoinbaseCdpDigest.class), eq("accountId"), eq(null));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapCreateAddressResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetNewAddress_exception() throws IOException {
        CBNewAddressResponse expectedResponse = mock(CBNewAddressResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.createAddress(any(), anyString(), any())).thenThrow(exception);

            CBNewAddressResponse response = apiWrapper.getNewAddress(API_VERSION, "coinbaseTime", "accountId");

            assertEquals(expectedResponse, response);
            verify(api).createAddress(any(CoinbaseCdpDigest.class), eq("accountId"), eq(null));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBNewAddressResponse.class)));
        }
    }

    @Test
    public void testListPaymentMethods_valid() throws IOException {
        CBPaymentMethodsResponse expectedResponse = mock(CBPaymentMethodsResponse.class);
        CoinbasePaymentMethodsResponse apiResponse = mock(CoinbasePaymentMethodsResponse.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapPaymentMethodsResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getPaymentMethods(any())).thenReturn(apiResponse);

            CBPaymentMethodsResponse response = apiWrapper.listPaymentMethods(API_VERSION, "coinbaseTime");

            assertEquals(expectedResponse, response);
            verify(api).getPaymentMethods(any(CoinbaseCdpDigest.class));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapPaymentMethodsResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testListPaymentMethods_exception() throws IOException {
        CBPaymentMethodsResponse expectedResponse = mock(CBPaymentMethodsResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getPaymentMethods(any())).thenThrow(exception);

            CBPaymentMethodsResponse response = apiWrapper.listPaymentMethods(API_VERSION, "coinbaseTime");

            assertEquals(expectedResponse, response);
            verify(api).getPaymentMethods(any(CoinbaseCdpDigest.class));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBPaymentMethodsResponse.class)));
        }
    }

    @Test
    public void testSendCoins_valid() throws IOException {
        CBSendCoinsResponse expectedResponse = mock(CBSendCoinsResponse.class);
        CoinbaseTransactionResponse apiResponse = mock(CoinbaseTransactionResponse.class);
        CoinbaseSendCoinsRequest request = mock(CoinbaseSendCoinsRequest.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapLegacySendCoinsRequestToRequest(any())).thenReturn(request);
            mockedMapper.when(() -> CoinbaseApiMapper.mapSendCoinsResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.sendCoins(any(), anyString(), any())).thenReturn(apiResponse);

            CBSendCoinsRequest legacyRequest = new CBSendCoinsRequest();
            CBSendCoinsResponse response = apiWrapper.sendCoins(API_VERSION, "coinbaseTime", "accountId", legacyRequest);

            assertEquals(expectedResponse, response);
            verify(api).sendCoins(any(CoinbaseCdpDigest.class), eq("accountId"), eq(request));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapLegacySendCoinsRequestToRequest(legacyRequest));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapSendCoinsResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testSendCoins_exception() throws IOException {
        CBSendCoinsResponse expectedResponse = mock(CBSendCoinsResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);
        CoinbaseSendCoinsRequest request = mock(CoinbaseSendCoinsRequest.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapLegacySendCoinsRequestToRequest(any())).thenReturn(request);
            mockedMapper.when(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.sendCoins(any(), anyString(), any())).thenThrow(exception);

            CBSendCoinsRequest legacyRequest = new CBSendCoinsRequest();
            CBSendCoinsResponse response = apiWrapper.sendCoins(API_VERSION, "coinbaseTime", "accountId", legacyRequest);

            assertEquals(expectedResponse, response);
            verify(api).sendCoins(any(CoinbaseCdpDigest.class), eq("accountId"), eq(request));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapLegacySendCoinsRequestToRequest(legacyRequest));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBSendCoinsResponse.class)));
        }
    }

    @Test
    public void testGetAccounts_valid() throws IOException {
        CBAccountsResponse expectedResponse = mock(CBAccountsResponse.class);
        CoinbaseAccountsResponse apiResponse = mock(CoinbaseAccountsResponse.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getAccounts(any(), any(), any())).thenReturn(apiResponse);

            CBAccountsResponse response = apiWrapper.getAccounts(API_VERSION, "coinbaseTime", "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccounts(any(CoinbaseCdpDigest.class), eq(null), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetAccounts_exception() throws IOException {
        CBAccountsResponse expectedResponse = mock(CBAccountsResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getAccounts(any(), any(), any())).thenThrow(exception);

            CBAccountsResponse response = apiWrapper.getAccounts(API_VERSION, "coinbaseTime", "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccounts(any(CoinbaseCdpDigest.class), eq(null), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBAccountsResponse.class)));
        }
    }

    @Test
    public void testGetTime_valid() throws IOException {
        CBTimeResponse expectedResponse = mock(CBTimeResponse.class);
        CoinbaseServerTimeResponse apiResponse = mock(CoinbaseServerTimeResponse.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapServerTimeResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getServerTime()).thenReturn(apiResponse);

            CBTimeResponse response = apiWrapper.getTime(API_VERSION);

            assertEquals(expectedResponse, response);
            verify(api).getServerTime();
            mockedMapper.verify(() -> CoinbaseApiMapper.mapServerTimeResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetTime_exception() throws IOException {
        CBTimeResponse expectedResponse = mock(CBTimeResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseApiMapper> mockedMapper = mockStatic(CoinbaseApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getServerTime()).thenThrow(exception);

            CBTimeResponse response = apiWrapper.getTime(API_VERSION);

            assertEquals(expectedResponse, response);
            verify(api).getServerTime();
            mockedMapper.verify(() -> CoinbaseApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBTimeResponse.class)));
        }
    }

}