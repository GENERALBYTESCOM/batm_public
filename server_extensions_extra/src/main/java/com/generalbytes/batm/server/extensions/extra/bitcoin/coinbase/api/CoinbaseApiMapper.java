/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAmount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseApiError;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseMarketOrderConfiguration;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrderConfiguration;
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
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAmount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPagination;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTimeResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTransaction;

import java.util.Arrays;
import java.util.UUID;

/**
 * Mapper between new and legacy Coinbase API DTOs.
 */
public class CoinbaseApiMapper {

    /**
     * Map {@link CoinbaseApiException} to a legacy {@link CBResponse}.
     *
     * <p>The legacy response DTOs contained any errors returned by the API.</p>
     *
     * @param exception      The {@link CoinbaseApiException} to map.
     * @param legacyResponse The legacy response to map the errors to.
     * @param <T>            Type of the legacy response.
     * @return The legacy response with errors from the {@link CoinbaseApiException}.
     */
    public static <T extends CBResponse> T mapExceptionToLegacyResponse(CoinbaseApiException exception, T legacyResponse) {
        if (exception == null || legacyResponse == null) {
            return legacyResponse;
        }
        legacyResponse.errors = mapCoinbaseApiErrorsToLegacyErrors(exception.getErrors());
        legacyResponse.warnings = mapCoinbaseApiErrorsToLegacyErrors(exception.getWarnings());
        return legacyResponse;
    }

    private static CBResponse.CBError[] mapCoinbaseApiErrorsToLegacyErrors(CoinbaseApiError[] errors) {
        if (errors == null) {
            return null;
        }

        return Arrays.stream(errors)
                .map(CoinbaseApiMapper::mapCoinbaseApiErrorToLegacyError)
                .toArray(CBResponse.CBError[]::new);
    }

    private static CBResponse.CBError mapCoinbaseApiErrorToLegacyError(CoinbaseApiError coinbaseApiError) {
        CBResponse.CBError error = new CBResponse.CBError();
        error.id = coinbaseApiError.getId();
        error.message = coinbaseApiError.getMessage();
        error.url = coinbaseApiError.getUrl();
        return error;
    }

    /**
     * Map {@link CoinbasePriceResponse} to legacy {@link CBPriceResponse}.
     *
     * @param response The {@link CoinbasePriceResponse} to map.
     * @return The mapped {@link CBPriceResponse}.
     */
    public static CBPriceResponse mapPriceResponseToLegacyResponse(CoinbasePriceResponse response) {
        if (response == null) {
            return null;
        }

        CBPriceResponse legacyResponse = new CBPriceResponse();
        legacyResponse.data = mapAmountToLegacyPrice(response.getPrice());
        return legacyResponse;
    }

    private static CBPriceResponse.CBPrice mapAmountToLegacyPrice(CoinbaseAmount price) {
        if (price == null) {
            return null;
        }

        CBPriceResponse.CBPrice legacyPrice = new CBPriceResponse.CBPrice();
        legacyPrice.amount = price.getAmount() == null ? null : price.getAmount().toPlainString();
        legacyPrice.currency = price.getCurrency();
        return legacyPrice;
    }

    /**
     * Map {@link CoinbaseCreateAddressResponse} to legacy {@link CBNewAddressResponse}.
     *
     * @param response The {@link CoinbaseCreateAddressResponse} to map.
     * @return The mapped {@link CBNewAddressResponse}.
     */
    public static CBNewAddressResponse mapCreateAddressResponseToLegacyResponse(CoinbaseCreateAddressResponse response) {
        if (response == null) {
            return null;
        }

        CBNewAddressResponse legacyResponse = new CBNewAddressResponse();
        legacyResponse.data = mapAddressToLegacyAddress(response.getAddress());
        return legacyResponse;
    }

    private static CBNewAddressResponse.CBAddress mapAddressToLegacyAddress(CoinbaseAddress address) {
        if (address == null) {
            return null;
        }

        CBNewAddressResponse.CBAddress legacyAddress = new CBNewAddressResponse.CBAddress();
        legacyAddress.id = address.getId();
        legacyAddress.address = address.getAddress();
        legacyAddress.name = address.getName();
        legacyAddress.created_at = address.getCreatedAt();
        legacyAddress.updated_at = address.getUpdatedAt();
        legacyAddress.network = address.getNetwork();
        legacyAddress.resource = address.getResource();
        legacyAddress.resource_path = address.getResourcePath();
        return legacyAddress;
    }

    /**
     * Map {@link CoinbasePaymentMethodsResponse} to legacy {@link CBPaymentMethodsResponse}.
     *
     * @param response The {@link CoinbasePaymentMethodsResponse} to map.
     * @return The mapped {@link CBPaymentMethodsResponse}.
     */
    public static CBPaymentMethodsResponse mapPaymentMethodsResponseToLegacyResponse(CoinbasePaymentMethodsResponse response) {
        if (response == null) {
            return null;
        }

        CBPaymentMethodsResponse legacyResponse = new CBPaymentMethodsResponse();
        if (response.getPaymentMethods() != null) {
            legacyResponse.data = response.getPaymentMethods().stream()
                    .map(CoinbaseApiMapper::mapPaymentMethod)
                    .toArray(CBPaymentMethodsResponse.CBPaymentMethod[]::new);
        }
        return legacyResponse;
    }

    private static CBPaymentMethodsResponse.CBPaymentMethod mapPaymentMethod(CoinbasePaymentMethod paymentMethod) {
        CBPaymentMethodsResponse.CBPaymentMethod legacyPaymentMethod = new CBPaymentMethodsResponse.CBPaymentMethod();
        legacyPaymentMethod.id = paymentMethod.getId();
        legacyPaymentMethod.type = paymentMethod.getType();
        legacyPaymentMethod.name = paymentMethod.getName();
        legacyPaymentMethod.currency = paymentMethod.getCurrency();
        return legacyPaymentMethod;
    }

    /**
     * Map legacy {@link CBSendCoinsRequest} to {@link CoinbaseSendCoinsRequest}.
     *
     * @param legacyRequest The {@link CBSendCoinsRequest} to map.
     * @return The mapped {@link CoinbaseSendCoinsRequest}.
     */
    public static CoinbaseSendCoinsRequest mapLegacySendCoinsRequestToRequest(CBSendCoinsRequest legacyRequest) {
        if (legacyRequest == null) {
            return null;
        }

        CoinbaseSendCoinsRequest request = new CoinbaseSendCoinsRequest();
        request.setTo(legacyRequest.to);
        request.setAmount(legacyRequest.amount);
        request.setCurrency(legacyRequest.currency);
        request.setDescription(legacyRequest.description);
        return request;
    }

    /**
     * Map {@link CoinbaseTransactionResponse} to legacy {@link CBSendCoinsResponse}.
     *
     * @param response The {@link CoinbaseTransactionResponse} to map.
     * @return The mapped {@link CBSendCoinsResponse}.
     */
    public static CBSendCoinsResponse mapSendCoinsResponseToLegacyResponse(CoinbaseTransactionResponse response) {
        if (response == null) {
            return null;
        }

        CBSendCoinsResponse legacyResponse = new CBSendCoinsResponse();
        legacyResponse.data = mapTransactionToLegacyTransaction(response.getTransaction());
        return legacyResponse;
    }

    private static CBTransaction mapTransactionToLegacyTransaction(CoinbaseTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        CBTransaction legacyTransaction = new CBTransaction();
        legacyTransaction.id = transaction.getId();
        return legacyTransaction;
    }

    /**
     * Map {@link CoinbaseAccountsResponse} to legacy {@link CBAccountsResponse}.
     *
     * @param response The {@link CoinbaseAccountsResponse} to map.
     * @return The mapped {@link CBAccountsResponse}.
     */
    public static CBAccountsResponse mapAccountsResponseToLegacyResponse(CoinbaseAccountsResponse response) {
        if (response == null) {
            return null;
        }

        CBAccountsResponse legacyResponse = new CBAccountsResponse();
        legacyResponse.pagination = mapPaginationToLegacyPagination(response.getPagination());
        if (response.getAccounts() != null) {
            legacyResponse.data = response.getAccounts().stream()
                    .map(CoinbaseApiMapper::mapAccountToLegacyAccount)
                    .toArray(CBAccount[]::new);
        }
        return legacyResponse;
    }

    private static CBAccount mapAccountToLegacyAccount(CoinbaseAccount account) {
        CBAccount legacyAccount = new CBAccount();
        legacyAccount.id = account.getId();
        legacyAccount.name = account.getName();
        legacyAccount.type = account.getType();
        legacyAccount.currency = mapCurrencyToLegacyCurrency(account.getCurrency());
        legacyAccount.balance = mapAmountToLegacyBalance(account.getBalance());
        legacyAccount.created_at = account.getCreatedAt();
        legacyAccount.updated_at = account.getUpdatedAt();
        legacyAccount.primary = account.isPrimary();
        legacyAccount.resource = account.getResource();
        legacyAccount.resource_path = account.getResourcePath();
        return legacyAccount;
    }

    private static CBCurrency mapCurrencyToLegacyCurrency(CoinbaseCurrency currency) {
        if (currency == null) {
            return null;
        }

        CBCurrency legacyCurrency = new CBCurrency();
        legacyCurrency.code = currency.getCode();
        return legacyCurrency;
    }

    private static CBAmount mapAmountToLegacyBalance(CoinbaseAmount amount) {
        if (amount == null) {
            return null;
        }

        CBAmount legacyBalance = new CBAmount();
        legacyBalance.amount = amount.getAmount() == null ? null : amount.getAmount().toPlainString();
        legacyBalance.currency = amount.getCurrency();
        return legacyBalance;
    }

    private static CBPagination mapPaginationToLegacyPagination(CoinbasePagination pagination) {
        if (pagination == null) {
            return null;
        }

        CBPagination legacyPagination = new CBPagination();
        legacyPagination.next_uri = pagination.getNextUri();
        return legacyPagination;
    }

    /**
     * Map legacy {@link CBOrderRequest} to {@link CoinbaseCreateOrderRequest}.
     *
     * @param legacyRequest The {@link CBOrderRequest} to map.
     * @return The mapped {@link CoinbaseCreateOrderRequest}.
     */
    public static CoinbaseCreateOrderRequest mapLegacyCreateOrderRequestToRequest(CBOrderRequest legacyRequest, CoinbaseOrderSide side) {
        if (legacyRequest == null) {
            return null;
        }

        CoinbaseMarketOrderConfiguration marketOrderConfiguration = new CoinbaseMarketOrderConfiguration();
        marketOrderConfiguration.setBaseSize(legacyRequest.total);

        CoinbaseOrderConfiguration orderConfiguration = new CoinbaseOrderConfiguration();
        orderConfiguration.setMarketOrderConfiguration(marketOrderConfiguration);

        CoinbaseCreateOrderRequest request = new CoinbaseCreateOrderRequest();
        request.setClientOrderId(UUID.randomUUID().toString());
        request.setOrderConfiguration(orderConfiguration);
        request.setSide(side);
        request.setProductId(legacyRequest.currency + "-" + legacyRequest.fiatCurrency);
        return request;
    }

    /**
     * Map {@link CoinbaseOrderResponse} to legacy {@link CBOrderResponse}.
     *
     * @param response The {@link CoinbaseOrderResponse} to map.
     * @return The mapped {@link CBOrderResponse}.
     */
    public static CBOrderResponse mapOrderResponseToLegacyResponse(CoinbaseOrderResponse response) {
        if (response == null) {
            return null;
        }

        CBOrderResponse legacyResponse = new CBOrderResponse();
        if (response.getOrder() != null) {
            CBOrderResponse.CBOrder order = new CBOrderResponse.CBOrder();
            order.id = response.getOrder().getId();
            order.status = mapOrderStatusToLegacyStatus(response.getOrder().getStatus());
            legacyResponse.data = order;
        }
        return legacyResponse;
    }

    private static String mapOrderStatusToLegacyStatus(CoinbaseOrderStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case QUEUED:
            case PENDING:
            case OPEN:
                return "created";
            case FILLED:
                return "completed";
            case EXPIRED:
            case FAILED:
            case UNKNOWN_ORDER_STATUS:
            case CANCEL_QUEUED:
            case CANCELLED:
                return "cancelled";
        }
        return null;
    }

    /**
     * Map {@link CoinbaseCreateOrderResponse} to legacy {@link CBOrderResponse}.
     *
     * @param response The {@link CoinbaseCreateOrderResponse} to map.
     * @return The mapped {@link CBOrderResponse}.
     */
    public static CBOrderResponse mapCreateOrderResponseToLegacyResponse(CoinbaseCreateOrderResponse response) {
        if (response == null || !response.isSuccess()) {
            return null;
        }

        CBOrderResponse legacyResponse = new CBOrderResponse();
        if (response.getSuccessResponse() != null) {
            CBOrderResponse.CBOrder order = new CBOrderResponse.CBOrder();
            order.id = response.getSuccessResponse().getOrderId();
            legacyResponse.data = order;
        }
        return legacyResponse;
    }

    /**
     * Map {@link CoinbaseServerTimeResponse} to legacy {@link CBTimeResponse}.
     *
     * @param response The {@link CoinbaseServerTimeResponse} to map.
     * @return The mapped {@link CBTimeResponse}.
     */
    public static CBTimeResponse mapServerTimeResponseToLegacyResponse(CoinbaseServerTimeResponse response) {
        if (response == null) {
            return null;
        }

        CBTimeResponse legacyResponse = new CBTimeResponse();
        legacyResponse.data = mapServerTimeToLegacyTime(response.getTime());
        return legacyResponse;
    }

    private static CBTimeResponse.CBTime mapServerTimeToLegacyTime(CoinbaseServerTime serverTime) {
        if (serverTime == null) {
            return null;
        }

        CBTimeResponse.CBTime legacyTime = new CBTimeResponse.CBTime();
        legacyTime.iso = serverTime.getIso();
        legacyTime.epoch = serverTime.getEpoch();
        return legacyTime;
    }

}
