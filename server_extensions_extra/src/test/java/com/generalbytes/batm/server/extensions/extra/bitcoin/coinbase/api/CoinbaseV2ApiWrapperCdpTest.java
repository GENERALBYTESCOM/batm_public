package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAddressesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseExchangeRatesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionsResponse;
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

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoinbaseV2ApiWrapperCdpTest {

    private static final String API_VERSION = "2016-07-23";
    private static final String PRIVATE_KEY = "privateKey";
    private static final String KEY_NAME = "keyName";

    private ICoinbaseV3Api api;
    private CoinbaseV2ApiWrapperCdp apiWrapper;

    @Before
    public void setUp() {
        api = mock(ICoinbaseV3Api.class);
        apiWrapper = new CoinbaseV2ApiWrapperCdp(api, PRIVATE_KEY, KEY_NAME);
    }

    @Test
    public void testGetExchangeRates_valid() {
        CBExchangeRatesResponse expectedResponse = mock(CBExchangeRatesResponse.class);
        CoinbaseExchangeRatesResponse apiResponse = mock(CoinbaseExchangeRatesResponse.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExchangeRatesResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getExchangeRates(anyString())).thenReturn(apiResponse);

            CBExchangeRatesResponse response = apiWrapper.getExchangeRates("CZK");

            assertEquals(expectedResponse, response);
            verify(api).getExchangeRates("CZK");
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExchangeRatesResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetExchangeRates_exception() {
        CBExchangeRatesResponse expectedResponse = mock(CBExchangeRatesResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getExchangeRates(anyString())).thenThrow(exception);

            CBExchangeRatesResponse response = apiWrapper.getExchangeRates("CZK");

            assertEquals(expectedResponse, response);
            verify(api).getExchangeRates("CZK");
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBExchangeRatesResponse.class)));
        }
    }

    @Test
    public void testGetAccounts_valid() {
        CBPaginatedResponse<CBAccount> expectedResponse = mock(CBPaginatedResponse.class);
        CoinbaseAccountsResponse apiResponse = mock(CoinbaseAccountsResponse.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getAccounts(any(), any(), anyString())).thenReturn(apiResponse);

            CBPaginatedResponse<CBAccount> response = apiWrapper.getAccounts(API_VERSION, 1000, 100, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccounts(any(CoinbaseCdpDigest.class), eq(100), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetAccounts_exception() {
        CBPaginatedResponse<CBAccount> expectedResponse = mock(CBPaginatedResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getAccounts(any(), any(), anyString())).thenThrow(exception);

            CBPaginatedResponse<CBAccount> response = apiWrapper.getAccounts(API_VERSION, 1000, 100, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccounts(any(CoinbaseCdpDigest.class), eq(100), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBPaginatedResponse.class)));
        }
    }

    @Test
    public void testGetAccountAddresses_valid() {
        CBAddressesResponse expectedResponse = mock(CBAddressesResponse.class);
        CoinbaseAddressesResponse apiResponse = mock(CoinbaseAddressesResponse.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getAddresses(any(), anyString())).thenReturn(apiResponse);

            CBAddressesResponse response = apiWrapper.getAccountAddresses(API_VERSION, 1000, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAddresses(any(CoinbaseCdpDigest.class), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetAccountAddresses_exception() {
        CBAddressesResponse expectedResponse = mock(CBAddressesResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getAddresses(any(), anyString())).thenThrow(exception);

            CBAddressesResponse response = apiWrapper.getAccountAddresses(API_VERSION, 1000, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAddresses(any(CoinbaseCdpDigest.class), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBAddressesResponse.class)));
        }
    }

    @Test
    public void testGetAccount_valid() {
        CBAccountResponse expectedResponse = mock(CBAccountResponse.class);
        CoinbaseAccountResponse apiResponse = mock(CoinbaseAccountResponse.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapAccountResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            when(api.getAccount(any(), anyString())).thenReturn(apiResponse);

            CBAccountResponse response = apiWrapper.getAccount(API_VERSION, 1000, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccount(any(CoinbaseCdpDigest.class), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapAccountResponseToLegacyResponse(apiResponse));
        }
    }

    @Test
    public void testGetAccount_exception() {
        CBAccountResponse expectedResponse = mock(CBAccountResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getAccount(any(), anyString())).thenThrow(exception);

            CBAccountResponse response = apiWrapper.getAccount(API_VERSION, 1000, "accountId");

            assertEquals(expectedResponse, response);
            verify(api).getAccount(any(CoinbaseCdpDigest.class), eq("accountId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBAccountResponse.class)));
        }
    }

    @Test
    public void testSend_valid() {
        CBSendResponse expectedResponse = mock(CBSendResponse.class);
        CoinbaseTransactionResponse apiResponse = mock(CoinbaseTransactionResponse.class);
        CoinbaseSendCoinsRequest request = mock(CoinbaseSendCoinsRequest.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapTransactionResponseToLegacySendResponse(any())).thenReturn(expectedResponse);
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapLegacySendRequestToRequest(any())).thenReturn(request);
            when(api.sendCoins(any(), anyString(), any())).thenReturn(apiResponse);

            CBSendRequest legacyRequest = mock(CBSendRequest.class);
            CBSendResponse response = apiWrapper.send(API_VERSION, 1000, "accountId", legacyRequest);

            assertEquals(expectedResponse, response);
            verify(api).sendCoins(any(CoinbaseCdpDigest.class), eq("accountId"), eq(request));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapTransactionResponseToLegacySendResponse(apiResponse));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapLegacySendRequestToRequest(legacyRequest));
        }
    }

    @Test
    public void testSend_exception() {
        CBSendResponse expectedResponse = mock(CBSendResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);
        CoinbaseSendCoinsRequest request = mock(CoinbaseSendCoinsRequest.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapLegacySendRequestToRequest(any())).thenReturn(request);
            when(api.sendCoins(any(), anyString(), any())).thenThrow(exception);

            CBSendRequest legacyRequest = mock(CBSendRequest.class);
            CBSendResponse response = apiWrapper.send(API_VERSION, 1000, "accountId", legacyRequest);

            assertEquals(expectedResponse, response);
            verify(api).sendCoins(any(CoinbaseCdpDigest.class), eq("accountId"), eq(request));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBSendResponse.class)));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapLegacySendRequestToRequest(legacyRequest));
        }
    }

    @Test
    public void testCreateAddress_valid() {
        CBCreateAddressResponse expectedResponse = mock(CBCreateAddressResponse.class);
        CoinbaseCreateAddressResponse apiResponse = mock(CoinbaseCreateAddressResponse.class);
        CoinbaseCreateAddressRequest request = mock(CoinbaseCreateAddressRequest.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapCreateAddressResponseToLegacyResponse(any())).thenReturn(expectedResponse);
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapLegacyCreateAddressRequestToRequest(any())).thenReturn(request);
            when(api.createAddress(any(), anyString(), any())).thenReturn(apiResponse);

            CBCreateAddressRequest legacyRequest = mock(CBCreateAddressRequest.class);
            CBCreateAddressResponse response = apiWrapper.createAddress(API_VERSION, 1000, "accountId", legacyRequest);

            assertEquals(expectedResponse, response);
            verify(api).createAddress(any(CoinbaseCdpDigest.class), eq("accountId"), eq(request));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapCreateAddressResponseToLegacyResponse(apiResponse));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapLegacyCreateAddressRequestToRequest(legacyRequest));
        }
    }

    @Test
    public void testCreateAddress_exception() {
        CBCreateAddressResponse expectedResponse = mock(CBCreateAddressResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);
        CoinbaseCreateAddressRequest request = mock(CoinbaseCreateAddressRequest.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapLegacyCreateAddressRequestToRequest(any())).thenReturn(request);
            when(api.createAddress(any(), anyString(), any())).thenThrow(exception);

            CBCreateAddressRequest legacyRequest = mock(CBCreateAddressRequest.class);
            CBCreateAddressResponse response = apiWrapper.createAddress(API_VERSION, 1000, "accountId", legacyRequest);

            assertEquals(expectedResponse, response);
            verify(api).createAddress(any(CoinbaseCdpDigest.class), eq("accountId"), eq(request));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBCreateAddressResponse.class)));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapLegacyCreateAddressRequestToRequest(legacyRequest));
        }
    }

    @Test
    public void testGetAddressTransactions_valid() {
        CBPaginatedResponse<CBTransaction> expectedResponse = mock(CBPaginatedResponse.class);
        CoinbaseTransactionsResponse apiResponse = mock(CoinbaseTransactionsResponse.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(any())).thenReturn(expectedResponse);
            when(api.getAddressTransactions(any(), anyString(), anyString(), any(), any())).thenReturn(apiResponse);

            CBPaginatedResponse<CBTransaction> response = apiWrapper.getAddressTransactions(API_VERSION, 1000, "accountId",
                    "addressId", 100, "startingAfterTransactionId");

            assertEquals(expectedResponse, response);
            verify(api).getAddressTransactions(any(CoinbaseCdpDigest.class), eq("accountId"), eq("addressId"), eq(100),
                    eq("startingAfterTransactionId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(apiResponse));
        }
    }

    @Test
    public void testGetAddressTransactions_exception() {
        CBPaginatedResponse<CBTransaction> expectedResponse = mock(CBPaginatedResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getAddressTransactions(any(), anyString(), anyString(), any(), any())).thenThrow(exception);

            CBPaginatedResponse<CBTransaction> response = apiWrapper.getAddressTransactions(API_VERSION, 1000, "accountId",
                    "addressId", 100, "startingAfterTransactionId");

            assertEquals(expectedResponse, response);
            verify(api).getAddressTransactions(any(CoinbaseCdpDigest.class), eq("accountId"), eq("addressId"), eq(100),
                    eq("startingAfterTransactionId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBPaginatedResponse.class)));
        }
    }

    @Test
    public void testGetAddresses_valid() {
        CBPaginatedResponse<CBAddress> expectedResponse = mock(CBPaginatedResponse.class);
        CoinbaseAddressesResponse apiResponse = mock(CoinbaseAddressesResponse.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(any())).thenReturn(expectedResponse);
            when(api.getAddresses(any(), anyString(), any(), any())).thenReturn(apiResponse);

            CBPaginatedResponse<CBAddress> response = apiWrapper.getAddresses(API_VERSION, 1000, "accountId", 100, "addressId");

            assertEquals(expectedResponse, response);
            verify(api).getAddresses(any(CoinbaseCdpDigest.class), eq("accountId"), eq(100), eq("addressId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(apiResponse));
        }
    }

    @Test
    public void testGetAddresses_exception() {
        CBPaginatedResponse<CBAddress> expectedResponse = mock(CBPaginatedResponse.class);
        CoinbaseApiException exception = mock(CoinbaseApiException.class);

        try (MockedStatic<CoinbaseV2ApiMapper> mockedMapper = mockStatic(CoinbaseV2ApiMapper.class)) {
            mockedMapper.when(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(any(), any())).thenReturn(expectedResponse);
            when(api.getAddresses(any(), anyString(), any(), any())).thenThrow(exception);

            CBPaginatedResponse<CBAddress> response = apiWrapper.getAddresses(API_VERSION, 1000, "accountId", 100, "addressId");

            assertEquals(expectedResponse, response);
            verify(api).getAddresses(any(CoinbaseCdpDigest.class), eq("accountId"), eq(100), eq("addressId"));
            mockedMapper.verify(() -> CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(eq(exception), any(CBPaginatedResponse.class)));
        }
    }

    @Test
    public void testCredentialsValidation() {
        doTestCredentialsValidationForEachCredential(apiWrapper -> apiWrapper.getAccounts(
                API_VERSION, 100L, 100, null));
        doTestCredentialsValidationForEachCredential(apiWrapper -> apiWrapper.getAccountAddresses(
                API_VERSION, 100L, "accountId"));
        doTestCredentialsValidationForEachCredential(apiWrapper -> apiWrapper.getAccount(
                API_VERSION, 100L, "accountId"));
        doTestCredentialsValidationForEachCredential(apiWrapper -> apiWrapper.send(
                API_VERSION, 100L, "accountId", new CBSendRequest()));
        doTestCredentialsValidationForEachCredential(apiWrapper -> apiWrapper.createAddress(
                API_VERSION, 100L, "accountId", new CBCreateAddressRequest("address")));
        doTestCredentialsValidationForEachCredential(apiWrapper -> apiWrapper.getAddressTransactions(
                API_VERSION, 100L, "accountId", "addressId", 100, null));
        doTestCredentialsValidationForEachCredential(apiWrapper -> apiWrapper.getAddresses(
                API_VERSION, 100L, "accountId", 100, null));
    }

    private void doTestCredentialsValidationForEachCredential(Consumer<CoinbaseV2ApiWrapperCdp> testCall) {
        doTestCredentialsValidation("privateKey", null, "keyName", testCall);
        doTestCredentialsValidation("keyName", "privateKey", null, testCall);
    }

    private void doTestCredentialsValidation(String expectedInvalidFieldName,
                                             String privateKey,
                                             String keyName,
                                             Consumer<CoinbaseV2ApiWrapperCdp> testCall) {
        CoinbaseV2ApiWrapperCdp apiWrapper = new CoinbaseV2ApiWrapperCdp(api, privateKey, keyName);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> testCall.accept(apiWrapper));

        assertEquals(expectedInvalidFieldName + " cannot be null", exception.getMessage());
    }
}