package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAmount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseApiError;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderSuccessResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseMarketOrderConfiguration;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrderSide;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrderStatus;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePagination;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePaymentMethod;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseServerTime;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseServerTimeResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransaction;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTimeResponse;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CoinbaseApiMapperTest {

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

        CBTimeResponse response = CoinbaseApiMapper.mapExceptionToLegacyResponse(exception, new CBTimeResponse());

        assertNotNull(response);
        assertNull(response.data);
        assertCbErrors(errors, response.errors);
        assertCbErrors(warnings, response.warnings);
    }

    @Test
    public void testMapExceptionToLegacyResponse_emptyErrors() {
        CoinbaseApiError[] errors = new CoinbaseApiError[0];
        CoinbaseApiError[] warnings = new CoinbaseApiError[0];
        CoinbaseApiException exception = new CoinbaseApiException("error", 0, "message", errors, warnings);

        CBTimeResponse response = CoinbaseApiMapper.mapExceptionToLegacyResponse(exception, new CBTimeResponse());

        assertNotNull(response);
        assertNull(response.data);
        assertCbErrors(errors, response.errors);
        assertCbErrors(warnings, response.warnings);
    }

    @Test
    public void testMapExceptionToLegacyResponse_nullErrors() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 0, "message", null, null);

        CBTimeResponse response = CoinbaseApiMapper.mapExceptionToLegacyResponse(exception, new CBTimeResponse());

        assertNotNull(response);
        assertNull(response.data);
        assertNull(response.errors);
        assertNull(response.warnings);
    }

    @Test
    public void testMapExceptionToLegacyResponse_nullException() {
        CBTimeResponse response = CoinbaseApiMapper.mapExceptionToLegacyResponse(null, new CBTimeResponse());

        assertNotNull(response);
        assertNull(response.data);
        assertNull(response.errors);
        assertNull(response.warnings);
    }

    @Test
    public void testMapExceptionToLegacyResponse_nullResponse() {
        CoinbaseApiException exception = new CoinbaseApiException("error", 0, "message", null, null);

        assertNull(CoinbaseApiMapper.mapExceptionToLegacyResponse(exception, null));
    }

    @Test
    public void testMapPriceResponseToLegacyResponse() {
        CoinbasePriceResponse response = createCoinbasePriceResponse(BigDecimal.TEN);

        CBPriceResponse legacyResponse = CoinbaseApiMapper.mapPriceResponseToLegacyResponse(response);
        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(response.getPrice().getAmount().toString(), legacyResponse.data.amount);
        assertEquals(response.getPrice().getCurrency(), legacyResponse.data.currency);
    }

    @Test
    public void testMapPriceResponseToLegacyResponse_nullAmount() {
        CoinbasePriceResponse response = createCoinbasePriceResponse(null);

        CBPriceResponse legacyResponse = CoinbaseApiMapper.mapPriceResponseToLegacyResponse(response);
        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertNull(legacyResponse.data.amount);
        assertEquals(response.getPrice().getCurrency(), legacyResponse.data.currency);
    }

    @Test
    public void testMapPriceResponseToLegacyResponse_nullPrice() {
        CBPriceResponse legacyResponse = CoinbaseApiMapper.mapPriceResponseToLegacyResponse(new CoinbasePriceResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.data);
    }

    @Test
    public void testMapPriceResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapPriceResponseToLegacyResponse(null));
    }

    @Test
    public void testMapCreateAddressResponseToLegacyResponse() {
        CoinbaseAddress address = new CoinbaseAddress();
        address.setId("id");
        address.setAddress("address");
        address.setName("name");
        address.setCreatedAt("created_at");
        address.setUpdatedAt("updated_at");
        address.setNetwork("network");
        address.setResourcePath("resource_path");

        CoinbaseCreateAddressResponse response = new CoinbaseCreateAddressResponse();
        response.setAddress(address);

        CBNewAddressResponse legacyResponse = CoinbaseApiMapper.mapCreateAddressResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(address.getId(), legacyResponse.data.id);
        assertEquals(address.getAddress(), legacyResponse.data.address);
        assertEquals(address.getName(), legacyResponse.data.name);
        assertEquals(address.getCreatedAt(), legacyResponse.data.created_at);
        assertEquals(address.getUpdatedAt(), legacyResponse.data.updated_at);
        assertEquals(address.getNetwork(), legacyResponse.data.network);
        assertEquals(address.getResource(), legacyResponse.data.resource);
        assertEquals(address.getResourcePath(), legacyResponse.data.resource_path);
    }

    @Test
    public void testMapCreateAddressResponseToLegacyResponse_nullAddress() {
        CBNewAddressResponse response = CoinbaseApiMapper.mapCreateAddressResponseToLegacyResponse(new CoinbaseCreateAddressResponse());

        assertNotNull(response);
        assertNull(response.data);
    }

    @Test
    public void testMapCreateAddressResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapCreateAddressResponseToLegacyResponse(null));
    }

    @Test
    public void testMapPaymentMethodsResponseToLegacyResponse() {
        CoinbasePaymentMethod paymentMethod = new CoinbasePaymentMethod();
        paymentMethod.setId("id");
        paymentMethod.setName("name");
        paymentMethod.setCurrency("currency");
        paymentMethod.setType("type");

        List<CoinbasePaymentMethod> paymentMethodList = new ArrayList<>();
        paymentMethodList.add(paymentMethod);

        CoinbasePaymentMethodsResponse response = new CoinbasePaymentMethodsResponse();
        response.setPaymentMethods(paymentMethodList);

        CBPaymentMethodsResponse legacyResponse = CoinbaseApiMapper.mapPaymentMethodsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(1, legacyResponse.data.length);
        assertEquals(paymentMethod.getId(), legacyResponse.data[0].id);
        assertEquals(paymentMethod.getName(), legacyResponse.data[0].name);
        assertEquals(paymentMethod.getCurrency(), legacyResponse.data[0].currency);
        assertEquals(paymentMethod.getType(), legacyResponse.data[0].type);
    }

    @Test
    public void testMapPaymentMethodsResponseToLegacyResponse_nullPaymentMethods() {
        CBPaymentMethodsResponse legacyResponse = CoinbaseApiMapper.mapPaymentMethodsResponseToLegacyResponse(new CoinbasePaymentMethodsResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.data);
    }

    @Test
    public void testMapPaymentMethodsResponseToLegacyResponse_emptyPaymentMethods() {
        CoinbasePaymentMethodsResponse response = new CoinbasePaymentMethodsResponse();
        response.setPaymentMethods(new ArrayList<>());

        CBPaymentMethodsResponse legacyResponse = CoinbaseApiMapper.mapPaymentMethodsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(0, legacyResponse.data.length);
    }

    @Test
    public void testMapPaymentMethodsResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapPaymentMethodsResponseToLegacyResponse(null));
    }

    @Test
    public void testMapLegacySendCoinsRequestToRequest() {
        CBSendCoinsRequest legacyRequest = new CBSendCoinsRequest();
        legacyRequest.to = "to";
        legacyRequest.amount = "amount";
        legacyRequest.currency = "currency";
        legacyRequest.description = "description";

        CoinbaseSendCoinsRequest request = CoinbaseApiMapper.mapLegacySendCoinsRequestToRequest(legacyRequest);

        assertNotNull(request);
        assertEquals("send", request.getType());
        assertEquals(legacyRequest.to, request.getTo());
        assertEquals(legacyRequest.amount, request.getAmount());
        assertEquals(legacyRequest.currency, request.getCurrency());
        assertEquals(legacyRequest.description, request.getDescription());
    }

    @Test
    public void testMapLegacySendCoinsRequestToRequest_nullRequest() {
        assertNull(CoinbaseApiMapper.mapLegacySendCoinsRequestToRequest(null));
    }

    @Test
    public void testMapSendCoinsResponseToLegacyResponse() {
        CoinbaseTransaction transaction = new CoinbaseTransaction();
        transaction.setId("id");

        CoinbaseTransactionResponse response = new CoinbaseTransactionResponse();
        response.setTransaction(transaction);

        CBSendCoinsResponse legacyResponse = CoinbaseApiMapper.mapSendCoinsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(transaction.getId(), legacyResponse.data.id);
    }

    @Test
    public void testMapSendCoinsResponseToLegacyResponse_nullTransaction() {
        CBSendCoinsResponse legacyResponse = CoinbaseApiMapper.mapSendCoinsResponseToLegacyResponse(new CoinbaseTransactionResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.data);
    }

    @Test
    public void testMapSendCoinsResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapSendCoinsResponseToLegacyResponse(null));
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse() {
        CoinbaseAmount balance = new CoinbaseAmount();
        balance.setAmount(BigDecimal.TEN);
        balance.setCurrency("EUR");

        CoinbaseCurrency currency = new CoinbaseCurrency();
        currency.setCode("USD");

        CoinbaseAccount account = new CoinbaseAccount();
        account.setId("id");
        account.setName("name");
        account.setType("type");
        account.setBalance(balance);
        account.setCurrency(currency);
        account.setPrimary(true);
        account.setCreatedAt("created_at");
        account.setUpdatedAt("updated_at");
        account.setResourcePath("resource_path");

        CoinbasePagination pagination = new CoinbasePagination();
        pagination.setNextUri("next_uri");

        CoinbaseAccountsResponse response = new CoinbaseAccountsResponse();
        response.setPagination(pagination);
        response.setAccounts(Collections.singletonList(account));

        CBAccountsResponse legacyResponse = CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.pagination);
        assertEquals(pagination.getNextUri(), legacyResponse.pagination.next_uri);
        assertNotNull(legacyResponse.data);
        assertEquals(1, legacyResponse.data.length);
        assertEquals(account.getId(), legacyResponse.data[0].id);
        assertEquals(account.getName(), legacyResponse.data[0].name);
        assertEquals(account.getType(), legacyResponse.data[0].type);
        assertNotNull(legacyResponse.data[0].balance);
        assertEquals(balance.getAmount().toPlainString(), legacyResponse.data[0].balance.amount);
        assertEquals(balance.getCurrency(), legacyResponse.data[0].balance.currency);
        assertNotNull(legacyResponse.data[0].currency);
        assertEquals(currency.getCode(), legacyResponse.data[0].currency.code);
        assertEquals(account.getCreatedAt(), legacyResponse.data[0].created_at);
        assertEquals(account.getUpdatedAt(), legacyResponse.data[0].updated_at);
        assertEquals(account.getResource(), legacyResponse.data[0].resource);
        assertEquals(account.getResourcePath(), legacyResponse.data[0].resource_path);
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_nullAmountInBalance() {
        CoinbaseAmount balance = new CoinbaseAmount();
        balance.setCurrency("EUR");
        balance.setAmount(null);

        CoinbaseAccount account = new CoinbaseAccount();
        account.setId("id");
        account.setName("name");
        account.setType("type");
        account.setBalance(balance);
        account.setCurrency(null);
        account.setPrimary(true);
        account.setCreatedAt("created_at");
        account.setUpdatedAt("updated_at");
        account.setResourcePath("resource_path");

        CoinbaseAccountsResponse response = new CoinbaseAccountsResponse();
        response.setPagination(null);
        response.setAccounts(Collections.singletonList(account));

        CBAccountsResponse legacyResponse = CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.pagination);
        assertNotNull(legacyResponse.data);
        assertEquals(1, legacyResponse.data.length);
        assertEquals(account.getId(), legacyResponse.data[0].id);
        assertEquals(account.getName(), legacyResponse.data[0].name);
        assertEquals(account.getType(), legacyResponse.data[0].type);
        assertNotNull(legacyResponse.data[0].balance);
        assertNull(legacyResponse.data[0].balance.amount);
        assertEquals(balance.getCurrency(), legacyResponse.data[0].balance.currency);
        assertNull(legacyResponse.data[0].currency);
        assertEquals(account.getCreatedAt(), legacyResponse.data[0].created_at);
        assertEquals(account.getUpdatedAt(), legacyResponse.data[0].updated_at);
        assertEquals(account.getResource(), legacyResponse.data[0].resource);
        assertEquals(account.getResourcePath(), legacyResponse.data[0].resource_path);
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_nulls() {
        CoinbaseAccount account = new CoinbaseAccount();
        account.setId("id");
        account.setName("name");
        account.setType("type");
        account.setBalance(null);
        account.setCurrency(null);
        account.setPrimary(true);
        account.setCreatedAt("created_at");
        account.setUpdatedAt("updated_at");
        account.setResourcePath("resource_path");

        CoinbaseAccountsResponse response = new CoinbaseAccountsResponse();
        response.setPagination(null);
        response.setAccounts(Collections.singletonList(account));

        CBAccountsResponse legacyResponse = CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.pagination);
        assertNotNull(legacyResponse.data);
        assertEquals(1, legacyResponse.data.length);
        assertEquals(account.getId(), legacyResponse.data[0].id);
        assertEquals(account.getName(), legacyResponse.data[0].name);
        assertEquals(account.getType(), legacyResponse.data[0].type);
        assertNull(legacyResponse.data[0].balance);
        assertNull(legacyResponse.data[0].currency);
        assertEquals(account.getCreatedAt(), legacyResponse.data[0].created_at);
        assertEquals(account.getUpdatedAt(), legacyResponse.data[0].updated_at);
        assertEquals(account.getResource(), legacyResponse.data[0].resource);
        assertEquals(account.getResourcePath(), legacyResponse.data[0].resource_path);
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_emptyAccounts() {
        CoinbaseAccountsResponse response = new CoinbaseAccountsResponse();
        response.setAccounts(Collections.emptyList());

        CBAccountsResponse legacyResponse = CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.pagination);
        assertNotNull(legacyResponse.data);
        assertEquals(0, legacyResponse.data.length);
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_nullAccounts() {
        CBAccountsResponse legacyResponse = CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(new CoinbaseAccountsResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.pagination);
        assertNull(legacyResponse.data);
    }

    @Test
    public void testMapAccountsResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(null));
    }

    @Test
    public void testMapLegacyCreateOrderRequestToRequest() {
        doTestMapLegacyCreateOrderRequestToRequest(CoinbaseOrderSide.BUY);
        doTestMapLegacyCreateOrderRequestToRequest(CoinbaseOrderSide.SELL);
        doTestMapLegacyCreateOrderRequestToRequest(null);
    }

    private void doTestMapLegacyCreateOrderRequestToRequest(CoinbaseOrderSide side) {
        CBOrderRequest legacyRequest = new CBOrderRequest();
        legacyRequest.amount = "amount";
        legacyRequest.total = "total";
        legacyRequest.currency = "BTC";
        legacyRequest.payment_method = "payment_method";
        legacyRequest.agree_btc_amount_varies = true;
        legacyRequest.quote = true;
        legacyRequest.commit = true;
        legacyRequest.fiatCurrency = "CZK";

        CoinbaseCreateOrderRequest request = CoinbaseApiMapper.mapLegacyCreateOrderRequestToRequest(legacyRequest, side);

        assertNotNull(request);
        assertNotNull(request.getClientOrderId());
        assertEquals(side, request.getSide());
        assertEquals("BTC-CZK", request.getProductId());
        assertNotNull(request.getOrderConfiguration());
        assertNotNull(request.getOrderConfiguration().getMarketOrderConfiguration());
        CoinbaseMarketOrderConfiguration marketOrderConfiguration = request.getOrderConfiguration().getMarketOrderConfiguration();
        assertEquals(legacyRequest.total, marketOrderConfiguration.getBaseSize());
        assertNull(marketOrderConfiguration.getQuoteSize());
    }

    @Test
    public void testMapLegacyCreateOrderRequestToRequest_nullRequest() {
        assertNull(CoinbaseApiMapper.mapLegacyCreateOrderRequestToRequest(null, CoinbaseOrderSide.BUY));
    }

    @Test
    public void testMapOrderResponseToLegacyResponse() {
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.QUEUED, "created");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.PENDING, "created");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.OPEN, "created");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.FILLED, "completed");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.EXPIRED, "cancelled");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.FAILED, "cancelled");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.UNKNOWN_ORDER_STATUS, "cancelled");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.CANCEL_QUEUED, "cancelled");
        doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus.CANCELLED, "cancelled");
        doTestMapOrderResponseToLegacyResponse(null, null);
    }

    private void doTestMapOrderResponseToLegacyResponse(CoinbaseOrderStatus status, String expectedLegacyStatus) {
        CoinbaseOrder order = new CoinbaseOrder();
        order.setId("id");
        order.setStatus(status);

        CoinbaseOrderResponse response = new CoinbaseOrderResponse();
        response.setOrder(order);

        CBOrderResponse legacyResponse = CoinbaseApiMapper.mapOrderResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(order.getId(), legacyResponse.data.id);
        assertEquals(expectedLegacyStatus, legacyResponse.data.status);
    }

    @Test
    public void testMapOrderResponseToLegacyResponse_nullOrder() {
        CBOrderResponse legacyResponse = CoinbaseApiMapper.mapOrderResponseToLegacyResponse(new CoinbaseOrderResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.data);
    }

    @Test
    public void testMapOrderResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapOrderResponseToLegacyResponse(null));
    }

    @Test
    public void testMapCreateOrderResponseToLegacyResponse() {
        CoinbaseCreateOrderSuccessResponse successResponse = new CoinbaseCreateOrderSuccessResponse();
        successResponse.setOrderId("orderId");

        CoinbaseCreateOrderResponse response = new CoinbaseCreateOrderResponse();
        response.setSuccess(true);
        response.setSuccessResponse(successResponse);

        CBOrderResponse legacyResponse = CoinbaseApiMapper.mapCreateOrderResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(successResponse.getOrderId(), legacyResponse.data.id);
    }

    @Test
    public void testMapCreateOrderResponseToLegacyResponse_nullSuccessResponse() {
        CoinbaseCreateOrderResponse response = new CoinbaseCreateOrderResponse();
        response.setSuccess(true);
        response.setSuccessResponse(null);

        CBOrderResponse legacyResponse = CoinbaseApiMapper.mapCreateOrderResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.data);
    }

    @Test
    public void testMapCreateOrderResponseToLegacyResponse_unsuccessful() {
        CoinbaseCreateOrderResponse response = new CoinbaseCreateOrderResponse();
        response.setSuccess(false);

        CBOrderResponse legacyResponse = CoinbaseApiMapper.mapCreateOrderResponseToLegacyResponse(response);

        assertNull(legacyResponse);
    }

    @Test
    public void testMapCreateOrderResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapCreateOrderResponseToLegacyResponse(null));
    }

    @Test
    public void testMapServerTimeResponseToLegacyResponse() {
        CoinbaseServerTime time = new CoinbaseServerTime();
        time.setEpoch(10_000L);
        time.setIso("2015-06-23T18:02:51Z");

        CoinbaseServerTimeResponse response = new CoinbaseServerTimeResponse();
        response.setTime(time);

        CBTimeResponse legacyResponse = CoinbaseApiMapper.mapServerTimeResponseToLegacyResponse(response);

        assertNotNull(legacyResponse);
        assertNotNull(legacyResponse.data);
        assertEquals(time.getEpoch(), legacyResponse.data.epoch);
        assertEquals(time.getIso(), legacyResponse.data.iso);
    }

    @Test
    public void testMapServerTimeResponseToLegacyResponse_nullTime() {
        CBTimeResponse legacyResponse = CoinbaseApiMapper.mapServerTimeResponseToLegacyResponse(new CoinbaseServerTimeResponse());

        assertNotNull(legacyResponse);
        assertNull(legacyResponse.data);
    }

    @Test
    public void testMapServerTimeResponseToLegacyResponse_nullResponse() {
        assertNull(CoinbaseApiMapper.mapServerTimeResponseToLegacyResponse(null));
    }

    private void assertCbErrors(CoinbaseApiError[] expectedErrors, CBResponse.CBError[] errors) {
        assertNotNull(errors);
        assertEquals(expectedErrors.length, errors.length);
        for (int i = 0; i < expectedErrors.length; i++) {
            assertNotNull(errors[i]);
            assertEquals(expectedErrors[i].getId(), errors[i].id);
            assertEquals(expectedErrors[i].getMessage(), errors[i].message);
            assertEquals(expectedErrors[i].getUrl(), errors[i].url);
        }
    }

    private CoinbasePriceResponse createCoinbasePriceResponse(BigDecimal amount) {
        CoinbaseAmount price = new CoinbaseAmount();
        price.setCurrency("CZK");
        price.setAmount(amount);

        CoinbasePriceResponse response = new CoinbasePriceResponse();
        response.setPrice(price);
        return response;
    }

    private CoinbaseApiError createError(String id, String message, String url) {
        CoinbaseApiError error = new CoinbaseApiError();
        error.setId(id);
        error.setMessage(message);
        error.setUrl(url);
        return error;
    }

}