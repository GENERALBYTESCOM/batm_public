package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAddressesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAmount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseApiError;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseExchangeRates;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseExchangeRatesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseNetwork;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePagination;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransaction;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionAmount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccountResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddressesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBError;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBExchangeRatesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPaginatedResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBTransaction;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBWarning;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CoinbaseV2ApiMapperTest {

    @Test
    public void testMapExceptionToLegacyResponse() {
        CoinbaseApiError[] errors = new CoinbaseApiError[]{
            createError("error1", "Error 1", "Error Url 1"),
            createError("error2", "Error 2", "Error Url 2")
        };
        CoinbaseApiError[] warnings = new CoinbaseApiError[]{
            createError("warning1", "Warning 1", "Warning Url 1"),
            createError("warning2", "Warning 2", "Warning Url 2")
        };
        CoinbaseApiException exception = new CoinbaseApiException("error", 0, "message", errors, warnings);

        CBExchangeRatesResponse response = CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(exception, new CBExchangeRatesResponse());

        assertNotNull(response);
        assertNull(response.getData());
        assertCbErrors(errors, response.getErrors());
        assertCbWarnings(warnings, response.getWarnings());
    }

    @Test
    public void testMapExceptionToLegacyResponse_emptyErrors() {
        CoinbaseApiError[] errors = new CoinbaseApiError[0];
        CoinbaseApiError[] warnings = new CoinbaseApiError[0];
        CoinbaseApiException exception = new CoinbaseApiException("error", 0, "message", errors, warnings);

        CBExchangeRatesResponse response = CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(exception, new CBExchangeRatesResponse());

        assertNotNull(response);
        assertNull(response.getData());
        assertCbErrors(errors, response.getErrors());
        assertCbWarnings(warnings, response.getWarnings());
    }

    @Test
    public void testMapExceptionToLegacyResponse_nullErrors() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 0, "message", null, null);

        CBExchangeRatesResponse response = CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(exception, new CBExchangeRatesResponse());

        assertNotNull(response);
        assertNull(response.getData());
        assertNull(response.getErrors());
        assertNull(response.getWarnings());
    }

    @Test
    public void testMapExceptionToLegacyResponse_nullException() {
        CBExchangeRatesResponse response = CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(null, new CBExchangeRatesResponse());

        assertNotNull(response);
        assertNull(response.getData());
        assertNull(response.getErrors());
        assertNull(response.getWarnings());
    }

    @Test
    public void testMapExceptionToLegacyResponse_nullResponse() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 0, "message", null, null);

        assertNull(CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(exception, null));
    }

    @Test
    public void testMapExchangeRatesResponseToLegacyResponse() {
        CoinbaseExchangeRates exchangeRates = new CoinbaseExchangeRates();
        exchangeRates.setFiatCurrency("CZK");
        exchangeRates.setRatesPerCryptocurrency(new HashMap<>(ImmutableMap.of(
            "BTC", new BigDecimal("100"),
            "ETH", new BigDecimal("10")
        )));

        CoinbaseExchangeRatesResponse response = new CoinbaseExchangeRatesResponse();
        response.setExchangeRates(exchangeRates);

        CBExchangeRatesResponse legacyResponse = CoinbaseV2ApiMapper.mapExchangeRatesResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getData());
        assertEquals(exchangeRates.getFiatCurrency(), legacyResponse.getData().getCurrency());
        assertEquals(exchangeRates.getRatesPerCryptocurrency(), legacyResponse.getData().getRates());
    }

    @Test
    public void testMapExchangeRatesResponseToLegacyResponse_nullExchangeRates() {
        CBExchangeRatesResponse legacyResponse = CoinbaseV2ApiMapper.mapExchangeRatesResponseToLegacyResponse(new CoinbaseExchangeRatesResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapExchangeRatesResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapExchangeRatesResponseToLegacyResponse(null));
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse() {
        CoinbaseAmount balance = new CoinbaseAmount();
        balance.setCurrency("CZK");
        balance.setAmount(BigDecimal.TEN);

        CoinbaseCurrency currency = new CoinbaseCurrency();
        currency.setCode("ETH");

        CoinbaseAccount account = new CoinbaseAccount();
        account.setId("id");
        account.setName("name");
        account.setType("type");
        account.setCurrency(currency);
        account.setBalance(balance);
        account.setPrimary(true);
        account.setCreatedAt("created_at");
        account.setUpdatedAt("updated_at");
        account.setResourcePath("resource_path");

        CoinbasePagination pagination = new CoinbasePagination();
        pagination.setNextUri("next_uri");

        CoinbaseAccountsResponse response = new CoinbaseAccountsResponse();
        response.setPagination(pagination);
        response.setAccounts(Collections.singletonList(account));

        CBPaginatedResponse<CBAccount> legacyResponse = CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getPagination());
        assertEquals(pagination.getNextUri(), legacyResponse.getPagination().getNext_uri());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        assertEquals(account.getId(), legacyResponse.getData().get(0).getId());
        assertEquals(account.getName(), legacyResponse.getData().get(0).getName());
        assertEquals(account.getType(), legacyResponse.getData().get(0).getType());
        assertNotNull(legacyResponse.getData().get(0).getCurrency());
        assertEquals(account.getCurrency().getCode(), legacyResponse.getData().get(0).getCurrency().getCode());
        assertNotNull(legacyResponse.getData().get(0).getBalance());
        assertEquals(account.getBalance().getAmount(), legacyResponse.getData().get(0).getBalance().getAmount());
        assertEquals(account.getBalance().getCurrency(), legacyResponse.getData().get(0).getBalance().getCurrency());
        assertEquals(account.getCreatedAt(), legacyResponse.getData().get(0).getCreated_at());
        assertEquals(account.getUpdatedAt(), legacyResponse.getData().get(0).getUpdated_at());
        assertEquals(account.getResource(), legacyResponse.getData().get(0).getResource());
        assertEquals(account.getResourcePath(), legacyResponse.getData().get(0).getResource_path());
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_nulls() {
        CoinbaseAccount account = new CoinbaseAccount();
        account.setId("id");
        account.setName("name");
        account.setType("type");
        account.setCurrency(null);
        account.setBalance(null);
        account.setPrimary(true);
        account.setCreatedAt("created_at");
        account.setUpdatedAt("updated_at");
        account.setResourcePath("resource_path");


        CoinbaseAccountsResponse response = new CoinbaseAccountsResponse();
        response.setPagination(null);
        response.setAccounts(Collections.singletonList(account));

        CBPaginatedResponse<CBAccount> legacyResponse = CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        assertEquals(account.getId(), legacyResponse.getData().get(0).getId());
        assertEquals(account.getName(), legacyResponse.getData().get(0).getName());
        assertEquals(account.getType(), legacyResponse.getData().get(0).getType());
        assertNull(legacyResponse.getData().get(0).getCurrency());
        assertNull(legacyResponse.getData().get(0).getBalance());
        assertEquals(account.getCreatedAt(), legacyResponse.getData().get(0).getCreated_at());
        assertEquals(account.getUpdatedAt(), legacyResponse.getData().get(0).getUpdated_at());
        assertEquals(account.getResource(), legacyResponse.getData().get(0).getResource());
        assertEquals(account.getResourcePath(), legacyResponse.getData().get(0).getResource_path());
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_emptyAccounts() {
        CoinbaseAccountsResponse response = new CoinbaseAccountsResponse();
        response.setAccounts(Collections.emptyList());

        CBPaginatedResponse<CBAccount> legacyResponse = CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(0, legacyResponse.getData().size());
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_nullAccounts() {
        CBPaginatedResponse<CBAccount> legacyResponse = CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(new CoinbaseAccountsResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(null));
    }

    @Test
    public void testMapAddressesResponseToLegacyResponse() {
        CoinbaseAddress address = new CoinbaseAddress();
        address.setId("id");
        address.setAddress("address");
        address.setName("name");
        address.setNetwork("network");
        address.setCreatedAt("created_at");
        address.setUpdatedAt("updated_at");
        address.setResourcePath("resourcePath");

        CoinbasePagination pagination = new CoinbasePagination();
        pagination.setNextUri("next_uri");

        CoinbaseAddressesResponse response = new CoinbaseAddressesResponse();
        response.setAddresses(Collections.singletonList(address));
        response.setPagination(pagination);

        CBAddressesResponse legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getPagination());
        assertEquals(pagination.getNextUri(), legacyResponse.getPagination().getNext_uri());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        assertEquals(address.getId(), legacyResponse.getData().get(0).getId());
        assertEquals(address.getName(), legacyResponse.getData().get(0).getName());
        assertEquals(address.getNetwork(), legacyResponse.getData().get(0).getNetwork());
        assertEquals(address.getCreatedAt(), legacyResponse.getData().get(0).getCreated_at());
        assertEquals(address.getUpdatedAt(), legacyResponse.getData().get(0).getUpdated_at());
        assertEquals(address.getResource(), legacyResponse.getData().get(0).getResource());
        assertEquals(address.getResourcePath(), legacyResponse.getData().get(0).getResource_path());
    }

    @Test
    public void testMapAddressesResponseToLegacyResponse_nullPagination() {
        CoinbaseAddress address = new CoinbaseAddress();
        address.setId("id");
        address.setAddress("address");
        address.setName("name");
        address.setNetwork("network");
        address.setCreatedAt("created_at");
        address.setUpdatedAt("updated_at");
        address.setResourcePath("resourcePath");

        CoinbaseAddressesResponse response = new CoinbaseAddressesResponse();
        response.setAddresses(Collections.singletonList(address));
        response.setPagination(null);

        CBAddressesResponse legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        assertEquals(address.getId(), legacyResponse.getData().get(0).getId());
        assertEquals(address.getName(), legacyResponse.getData().get(0).getName());
        assertEquals(address.getNetwork(), legacyResponse.getData().get(0).getNetwork());
        assertEquals(address.getCreatedAt(), legacyResponse.getData().get(0).getCreated_at());
        assertEquals(address.getUpdatedAt(), legacyResponse.getData().get(0).getUpdated_at());
        assertEquals(address.getResource(), legacyResponse.getData().get(0).getResource());
        assertEquals(address.getResourcePath(), legacyResponse.getData().get(0).getResource_path());
    }

    @Test
    public void testMapAddressesResponseToLegacyResponse_emptyAddresses() {
        CoinbaseAddressesResponse response = new CoinbaseAddressesResponse();
        response.setAddresses(Collections.emptyList());
        response.setPagination(null);

        CBAddressesResponse legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(0, legacyResponse.getData().size());
    }

    @Test
    public void testMapAddressesResponseToLegacyResponse_nullAddresses() {
        CBAddressesResponse legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(new CoinbaseAddressesResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapAddressesResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(null));
    }

    @Test
    public void testMapAccountResponseToLegacyResponse() {
        CoinbaseCurrency currency = new CoinbaseCurrency();
        currency.setCode("CZK");

        CoinbaseAmount balance = new CoinbaseAmount();
        balance.setAmount(BigDecimal.TEN);
        balance.setCurrency("BTC");

        CoinbaseAccount account = new CoinbaseAccount();
        account.setId("id");
        account.setName("name");
        account.setCurrency(currency);
        account.setBalance(balance);
        account.setPrimary(true);

        CoinbaseAccountResponse response = new CoinbaseAccountResponse();
        response.setAccount(account);

        CBAccountResponse legacyResponse = CoinbaseV2ApiMapper.mapAccountResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getData());
        assertEquals(account.getId(), legacyResponse.getData().getId());
        assertEquals(account.getName(), legacyResponse.getData().getName());
        assertNotNull(legacyResponse.getData().getCurrency());
        assertEquals(currency.getCode(), legacyResponse.getData().getCurrency().code);
        assertNotNull(legacyResponse.getData().getBalance());
        assertEquals(balance.getAmount(), legacyResponse.getData().getBalance().getAmount());
        assertEquals(balance.getCurrency(), legacyResponse.getData().getBalance().getCurrency());
        assertEquals(account.isPrimary(), legacyResponse.getData().isPrimary());
    }

    @Test
    public void testMapAccountResponseToLegacyResponse_nulls() {
        CoinbaseAccount account = new CoinbaseAccount();
        account.setId("id");
        account.setName("name");
        account.setCurrency(null);
        account.setBalance(null);
        account.setPrimary(true);

        CoinbaseAccountResponse response = new CoinbaseAccountResponse();
        response.setAccount(account);

        CBAccountResponse legacyResponse = CoinbaseV2ApiMapper.mapAccountResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getData());
        assertEquals(account.getId(), legacyResponse.getData().getId());
        assertEquals(account.getName(), legacyResponse.getData().getName());
        assertNull(legacyResponse.getData().getCurrency());
        assertNull(legacyResponse.getData().getBalance());
        assertEquals(account.isPrimary(), legacyResponse.getData().isPrimary());
    }

    @Test
    public void testMapAccountResponseToLegacyResponse_nullAccount() {
        CBAccountResponse legacyResponse = CoinbaseV2ApiMapper.mapAccountResponseToLegacyResponse(new CoinbaseAccountResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapAccountResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapAccountResponseToLegacyResponse(null));
    }

    @Test
    public void testMapLegacySendRequestToRequest() {
        CBSendRequest legacySendRequest = new CBSendRequest();
        legacySendRequest.setTo("to");
        legacySendRequest.setAmount("amount");
        legacySendRequest.setCurrency("currency");
        legacySendRequest.setIdem("idem");
        legacySendRequest.setDescription("description");

        CoinbaseSendCoinsRequest request = CoinbaseV2ApiMapper.mapLegacySendRequestToRequest(legacySendRequest);

        assertNotNull(request);
        assertEquals(legacySendRequest.getTo(), request.getTo());
        assertEquals(legacySendRequest.getAmount(), request.getAmount());
        assertEquals(legacySendRequest.getCurrency(), request.getCurrency());
        assertEquals(legacySendRequest.getType(), request.getType());
        assertEquals("7adab921-d7f8-3971-993c-3c3e936c67e5", request.getIdem());
        assertEquals(legacySendRequest.getDescription(), request.getDescription());
    }

    @Test
    public void testMapLegacySendRequestToRequest_nullRequest() {
        assertNull(CoinbaseV2ApiMapper.mapLegacySendRequestToRequest(null));
    }

    @Test
    public void testMapTransactionResponseToLegacySendResponse() {
        CoinbaseTransaction transaction = new CoinbaseTransaction();
        transaction.setId("id");

        CoinbaseTransactionResponse response = new CoinbaseTransactionResponse();
        response.setTransaction(transaction);

        CBSendResponse legacyResponse = CoinbaseV2ApiMapper.mapTransactionResponseToLegacySendResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getData());
        assertEquals(transaction.getId(), legacyResponse.getData().getId());
    }

    @Test
    public void testMapTransactionResponseToLegacySendResponse_nullTransaction() {
        CBSendResponse legacyResponse = CoinbaseV2ApiMapper.mapTransactionResponseToLegacySendResponse(new CoinbaseTransactionResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapTransactionResponseToLegacySendResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapTransactionResponseToLegacySendResponse(null));
    }

    @Test
    public void testMapLegacyCreateAddressRequestToRequest() {
        CBCreateAddressRequest legacyRequest = new CBCreateAddressRequest("name");

        CoinbaseCreateAddressRequest request = CoinbaseV2ApiMapper.mapLegacyCreateAddressRequestToRequest(legacyRequest);

        assertNotNull(request);
        assertEquals(legacyRequest.name, request.getName());
    }

    @Test
    public void testMapLegacyCreateAddressRequestToRequest_nullRequest() {
        assertNull(CoinbaseV2ApiMapper.mapLegacyCreateAddressRequestToRequest(null));
    }

    @Test
    public void testMapCreateAddressResponseToLegacyResponse() {
        CoinbaseAddress address = new CoinbaseAddress();
        address.setId("id");
        address.setName("name");
        address.setAddress("address");
        address.setNetwork("network");
        address.setCreatedAt("created_at");
        address.setUpdatedAt("updated_at");
        address.setResourcePath("resource_path");

        CoinbaseCreateAddressResponse response = new CoinbaseCreateAddressResponse();
        response.setAddress(address);

        CBCreateAddressResponse legacyResponse = CoinbaseV2ApiMapper.mapCreateAddressResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getData());
        assertEquals(address.getId(), legacyResponse.getData().getId());
        assertEquals(address.getName(), legacyResponse.getData().getName());
        assertEquals(address.getAddress(), legacyResponse.getData().getAddress());
        assertEquals(address.getNetwork(), legacyResponse.getData().getNetwork());
        assertEquals(address.getCreatedAt(), legacyResponse.getData().getCreated_at());
        assertEquals(address.getUpdatedAt(), legacyResponse.getData().getUpdated_at());
        assertEquals(address.getResource(), legacyResponse.getData().getResource());
        assertEquals(address.getResourcePath(), legacyResponse.getData().getResource_path());
    }

    @Test
    public void testMapCreateAddressResponseToLegacyResponse_nullAddress() {
        CBCreateAddressResponse legacyResponse = CoinbaseV2ApiMapper.mapCreateAddressResponseToLegacyResponse(new CoinbaseCreateAddressResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapCreateAddressResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapCreateAddressResponseToLegacyResponse(null));
    }

    @Test
    public void testMapTransactionsResponseToLegacyPaginatedResponse() {
        CoinbaseTransactionAmount amount = new CoinbaseTransactionAmount();
        amount.setCurrency("currency");
        amount.setValue(BigDecimal.TEN);

        CoinbaseTransactionAmount nativeAmount = new CoinbaseTransactionAmount();
        nativeAmount.setCurrency("native_currency");
        nativeAmount.setValue(BigDecimal.valueOf(100));

        CoinbaseNetwork coinbaseNetwork = new CoinbaseNetwork();
        coinbaseNetwork.setHash("hash");

        CoinbaseTransaction transaction = new CoinbaseTransaction();
        transaction.setId("id");
        transaction.setType("type");
        transaction.setStatus("status");
        transaction.setDescription("description");
        transaction.setAmount(amount);
        transaction.setNativeAmount(nativeAmount);
        transaction.setCreatedAt("created_at");
        transaction.setUpdatedAt("updated_at");
        transaction.setResourcePath("resource_path");
        transaction.setNetwork(coinbaseNetwork);

        CoinbasePagination pagination = new CoinbasePagination();
        pagination.setNextUri("next_uri");

        CoinbaseTransactionsResponse response = new CoinbaseTransactionsResponse();
        response.setPagination(pagination);
        response.setTransactions(Collections.singletonList(transaction));

        CBPaginatedResponse<CBTransaction> legacyResponse = CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getPagination());
        assertEquals(pagination.getNextUri(), legacyResponse.getPagination().getNext_uri());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        CBTransaction legacyTransaction = legacyResponse.getData().get(0);
        assertEquals(transaction.getId(), legacyTransaction.getId());
        assertEquals(transaction.getType(), legacyTransaction.getType());
        assertEquals(transaction.getStatus(), legacyTransaction.getStatus());
        assertEquals(transaction.getDescription(), legacyTransaction.getDescription());
        assertNotNull(legacyTransaction.getAmount());
        assertEquals(amount.getValue(), legacyTransaction.getAmount().getAmount());
        assertEquals(amount.getCurrency(), legacyTransaction.getAmount().getCurrency());
        assertNotNull(legacyTransaction.getNative_amount());
        assertEquals(nativeAmount.getValue(), legacyTransaction.getNative_amount().getAmount());
        assertEquals(nativeAmount.getCurrency(), legacyTransaction.getNative_amount().getCurrency());
        assertEquals(transaction.getCreatedAt(), legacyTransaction.getCreated_at());
        assertEquals(transaction.getUpdatedAt(), legacyTransaction.getUpdated_at());
        assertEquals(transaction.getResource(), legacyTransaction.getResource());
        assertEquals(transaction.getResourcePath(), legacyTransaction.getResource_path());
        assertNotNull(legacyTransaction.getNetwork());
        assertEquals(coinbaseNetwork.getHash(), legacyTransaction.getNetwork().getHash());
    }

    @Test
    public void testMapTransactionsResponseToLegacyPaginatedResponse_nulls() {
        CoinbaseTransaction transaction = new CoinbaseTransaction();
        transaction.setId("id");
        transaction.setType("type");
        transaction.setStatus("status");
        transaction.setDescription("description");
        transaction.setAmount(null);
        transaction.setNativeAmount(null);
        transaction.setCreatedAt("created_at");
        transaction.setUpdatedAt("updated_at");
        transaction.setResourcePath("resource_path");
        transaction.setNetwork(null);

        CoinbaseTransactionsResponse response = new CoinbaseTransactionsResponse();
        response.setPagination(null);
        response.setTransactions(Collections.singletonList(transaction));

        CBPaginatedResponse<CBTransaction> legacyResponse = CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(response);
        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        CBTransaction legacyTransaction = legacyResponse.getData().get(0);
        assertEquals(transaction.getId(), legacyTransaction.getId());
        assertEquals(transaction.getType(), legacyTransaction.getType());
        assertEquals(transaction.getStatus(), legacyTransaction.getStatus());
        assertEquals(transaction.getDescription(), legacyTransaction.getDescription());
        assertNull(legacyTransaction.getAmount());
        assertNull(legacyTransaction.getNative_amount());
        assertEquals(transaction.getCreatedAt(), legacyTransaction.getCreated_at());
        assertEquals(transaction.getUpdatedAt(), legacyTransaction.getUpdated_at());
        assertEquals(transaction.getResource(), legacyTransaction.getResource());
        assertEquals(transaction.getResourcePath(), legacyTransaction.getResource_path());
        assertNull(legacyTransaction.getNetwork());
    }

    @Test
    public void testMapTransactionsResponseToLegacyPaginatedResponse_emptyTransactions() {
        CoinbaseTransactionsResponse response = new CoinbaseTransactionsResponse();
        response.setPagination(null);
        response.setTransactions(Collections.emptyList());

        CBPaginatedResponse<CBTransaction> legacyResponse = CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(0, legacyResponse.getData().size());
    }

    @Test
    public void testMapTransactionsResponseToLegacyPaginatedResponse_nullTransactions() {
        CBPaginatedResponse<CBTransaction> legacyResponse = CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(new CoinbaseTransactionsResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapTransactionsResponseToLegacyPaginatedResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(null));
    }

    @Test
    public void testMapAddressesResponseToLegacyPaginatedResponse() {
        CoinbaseAddress address = new CoinbaseAddress();
        address.setId("id");
        address.setName("name");
        address.setAddress("address");
        address.setNetwork("network");
        address.setCreatedAt("created_at");
        address.setUpdatedAt("updated_at");
        address.setResourcePath("resource_path");

        CoinbasePagination pagination = new CoinbasePagination();
        pagination.setNextUri("next_uri");

        CoinbaseAddressesResponse response = new CoinbaseAddressesResponse();
        response.setPagination(pagination);
        response.setAddresses(Collections.singletonList(address));

        CBPaginatedResponse<CBAddress> legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.getPagination());
        assertEquals(pagination.getNextUri(), legacyResponse.getPagination().getNext_uri());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        assertEquals(address.getId(), legacyResponse.getData().get(0).getId());
        assertEquals(address.getName(), legacyResponse.getData().get(0).getName());
        assertEquals(address.getAddress(), legacyResponse.getData().get(0).getAddress());
        assertEquals(address.getNetwork(), legacyResponse.getData().get(0).getNetwork());
        assertEquals(address.getCreatedAt(), legacyResponse.getData().get(0).getCreated_at());
        assertEquals(address.getUpdatedAt(), legacyResponse.getData().get(0).getUpdated_at());
        assertEquals(address.getResource(), legacyResponse.getData().get(0).getResource());
        assertEquals(address.getResourcePath(), legacyResponse.getData().get(0).getResource_path());
    }

    @Test
    public void testMapAddressesResponseToLegacyPaginatedResponse_nullPagination() {
        CoinbaseAddress address = new CoinbaseAddress();
        address.setId("id");
        address.setName("name");
        address.setAddress("address");
        address.setNetwork("network");
        address.setCreatedAt("created_at");
        address.setUpdatedAt("updated_at");
        address.setResourcePath("resource_path");

        CoinbaseAddressesResponse response = new CoinbaseAddressesResponse();
        response.setPagination(null);
        response.setAddresses(Collections.singletonList(address));

        CBPaginatedResponse<CBAddress> legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(response);
        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(1, legacyResponse.getData().size());
        assertEquals(address.getId(), legacyResponse.getData().get(0).getId());
        assertEquals(address.getName(), legacyResponse.getData().get(0).getName());
        assertEquals(address.getAddress(), legacyResponse.getData().get(0).getAddress());
        assertEquals(address.getNetwork(), legacyResponse.getData().get(0).getNetwork());
        assertEquals(address.getCreatedAt(), legacyResponse.getData().get(0).getCreated_at());
        assertEquals(address.getUpdatedAt(), legacyResponse.getData().get(0).getUpdated_at());
        assertEquals(address.getResource(), legacyResponse.getData().get(0).getResource());
        assertEquals(address.getResourcePath(), legacyResponse.getData().get(0).getResource_path());
    }

    @Test
    public void testMapAddressesResponseToLegacyPaginatedResponse_emptyAddresses() {
        CoinbaseAddressesResponse response = new CoinbaseAddressesResponse();
        response.setPagination(null);
        response.setAddresses(Collections.emptyList());

        CBPaginatedResponse<CBAddress> legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNotNull(legacyResponse.getData());
        assertEquals(0, legacyResponse.getData().size());
    }

    @Test
    public void testMapAddressesResponseToLegacyPaginatedResponse_nullAddresses() {
        CBPaginatedResponse<CBAddress> legacyResponse = CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(new CoinbaseAddressesResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.getPagination());
        assertNull(legacyResponse.getData());
    }

    @Test
    public void testMapAddressesResponseToLegacyPaginatedResponse_nullResponse() {
        assertNull(CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(null));
    }

    private void assertCbErrors(CoinbaseApiError[] expectedErrors, List<CBError> errors) {
        assertNotNull(errors);
        assertEquals(expectedErrors.length, errors.size());
        for (int i = 0; i < expectedErrors.length; i++) {
            CBError error = errors.get(i);
            assertNotNull(error);
            assertEquals(expectedErrors[i].getId(), error.getId());
            assertEquals(expectedErrors[i].getMessage(), error.getMessage());
        }
    }

    private void assertCbWarnings(CoinbaseApiError[] expectedErrors, List<CBWarning> warnings) {
        assertNotNull(warnings);
        assertEquals(expectedErrors.length, warnings.size());
        for (int i = 0; i < expectedErrors.length; i++) {
            CBWarning warning = warnings.get(i);
            assertNotNull(warning);
            assertEquals(expectedErrors[i].getId(), warning.getId());
            assertEquals(expectedErrors[i].getMessage(), warning.getMessage());
            assertEquals(expectedErrors[i].getUrl(), warning.getUrl());
        }
    }

    private CoinbaseApiError createError(String id, String message, String url) {
        CoinbaseApiError error = new CoinbaseApiError();
        error.setId(id);
        error.setMessage(message);
        error.setUrl(url);
        return error;
    }

}