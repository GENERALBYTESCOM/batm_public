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
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePagination;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransaction;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccountResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddressesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBBalance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBError;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBExchangeRates;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBExchangeRatesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPaginatedResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPagination;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSend;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBTransaction;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBWarning;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CbAccountV2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between new and legacy Coinbase V2 API DTOs.
 */
public class CoinbaseV2ApiLegacyMapper {

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
        legacyResponse.setErrors(mapCoinbaseApiErrorsToLegacyErrors(exception.getErrors()));
        legacyResponse.setWarnings(mapCoinbaseApiWarningsToLegacyWarnings(exception.getWarnings()));
        return legacyResponse;
    }

    private static List<CBError> mapCoinbaseApiErrorsToLegacyErrors(CoinbaseApiError[] errors) {
        if (errors == null) {
            return null;
        }

        return Arrays.stream(errors)
            .map(CoinbaseV2ApiLegacyMapper::mapCoinbaseApiErrorToLegacyError)
            .collect(Collectors.toList());
    }

    private static List<CBWarning> mapCoinbaseApiWarningsToLegacyWarnings(CoinbaseApiError[] warnings) {
        if (warnings == null) {
            return null;
        }

        return Arrays.stream(warnings)
            .map(CoinbaseV2ApiLegacyMapper::mapCoinbaseApiWarningToLegacyWarning)
            .collect(Collectors.toList());

    }

    private static CBError mapCoinbaseApiErrorToLegacyError(CoinbaseApiError coinbaseApiError) {
        CBError error = new CBError();
        error.setId(coinbaseApiError.getId());
        error.setMessage(coinbaseApiError.getMessage());
        return error;
    }

    private static CBWarning mapCoinbaseApiWarningToLegacyWarning(CoinbaseApiError coinbaseApiError) {
        CBWarning warning = new CBWarning();
        warning.setId(coinbaseApiError.getId());
        warning.setMessage(coinbaseApiError.getMessage());
        warning.setUrl(coinbaseApiError.getUrl());
        return warning;
    }

    /**
     * Map {@link CoinbaseExchangeRatesResponse} to legacy {@link CBExchangeRatesResponse}.
     *
     * @param response The {@link CoinbaseExchangeRatesResponse} to map.
     * @return The mapped {@link CBExchangeRatesResponse}.
     */
    public static CBExchangeRatesResponse mapExchangeRatesResponseToLegacyResponse(CoinbaseExchangeRatesResponse response) {
        if (response == null) {
            return null;
        }

        CBExchangeRatesResponse legacyResponse = new CBExchangeRatesResponse();
        legacyResponse.setData(mapExchangeRatesToLegacy(response.getExchangeRates()));
        return legacyResponse;
    }

    private static CBExchangeRates mapExchangeRatesToLegacy(CoinbaseExchangeRates exchangeRates) {
        if (exchangeRates == null) {
            return null;
        }

        CBExchangeRates legacyExchangeRates = new CBExchangeRates();
        legacyExchangeRates.setRates(exchangeRates.getRatesPerCryptocurrency());
        legacyExchangeRates.setCurrency(exchangeRates.getFiatCurrency());
        return legacyExchangeRates;
    }

    /**
     * Map {@link CoinbaseAccountsResponse} to legacy {@link CBPaginatedResponse<CBAccount>}.
     *
     * @param response The {@link CoinbaseAccountsResponse} to map.
     * @return The mapped {@link CBPaginatedResponse<CBAccount>}.
     */
    public static CBPaginatedResponse<CBAccount> mapAccountsResponseToLegacyResponse(CoinbaseAccountsResponse response) {
        if (response == null) {
            return null;
        }

        CBPaginatedResponse<CBAccount> legacyResponse = new CBPaginatedResponse<>();
        legacyResponse.setPagination(mapPaginationToLegacyPagination(response.getPagination()));
        if (response.getAccounts() != null) {
            legacyResponse.setData(response.getAccounts().stream()
                .map(CoinbaseV2ApiLegacyMapper::mapAccountToLegacyAccount)
                .collect(Collectors.toList()));
        }
        return legacyResponse;
    }

    private static CBAccount mapAccountToLegacyAccount(CoinbaseAccount account) {
        CBAccount legacyAccount = new CBAccount();
        legacyAccount.setId(account.getId());
        legacyAccount.setName(account.getName());
        legacyAccount.setType(account.getType());
        legacyAccount.setCurrency(mapCurrencyToLegacyCurrency(account.getCurrency()));
        legacyAccount.setBalance(mapAmountToLegacyBalance(account.getBalance()));
        legacyAccount.setCreated_at(account.getCreatedAt());
        legacyAccount.setUpdated_at(account.getUpdatedAt());
        legacyAccount.setPrimary(account.isPrimary());
        legacyAccount.setResource(account.getResource());
        legacyAccount.setResource_path(account.getResourcePath());
        return legacyAccount;
    }

    private static CBCurrency mapCurrencyToLegacyCurrency(CoinbaseCurrency currency) {
        if (currency == null) {
            return null;
        }

        CBCurrency legacyCurrency = new CBCurrency();
        legacyCurrency.setCode(currency.getCode());
        return legacyCurrency;
    }

    private static CBBalance mapAmountToLegacyBalance(CoinbaseAmount amount) {
        if (amount == null) {
            return null;
        }

        CBBalance legacyBalance = new CBBalance();
        legacyBalance.setAmount(amount.getAmount());
        legacyBalance.setCurrency(amount.getCurrency());
        return legacyBalance;
    }

    private static CBPagination mapPaginationToLegacyPagination(CoinbasePagination pagination) {
        if (pagination == null) {
            return null;
        }

        CBPagination legacyPagination = new CBPagination();
        legacyPagination.setNext_uri(pagination.getNextUri());
        return legacyPagination;
    }

    /**
     * Map {@link CoinbaseAddressesResponse} to legacy {@link CBAddressesResponse}.
     *
     * @param response The {@link CoinbaseAddressesResponse} to map.
     * @return The mapped {@link CBAddressesResponse}.
     */
    public static CBAddressesResponse mapAddressesResponseToLegacyResponse(CoinbaseAddressesResponse response) {
        if (response == null) {
            return null;
        }

        CBAddressesResponse legacyResponse = new CBAddressesResponse();
        legacyResponse.setPagination(mapPaginationToLegacyPagination(response.getPagination()));
        if (response.getAddresses() != null) {
            legacyResponse.setData(response.getAddresses().stream()
                .map(CoinbaseV2ApiLegacyMapper::mapAddressToLegacyAddress)
                .collect(Collectors.toList()));
        }
        return legacyResponse;
    }

    private static CBAddress mapAddressToLegacyAddress(CoinbaseAddress address) {
        if (address == null) {
            return null;
        }

        CBAddress legacyAddress = new CBAddress();
        legacyAddress.setId(address.getId());
        legacyAddress.setName(address.getName());
        legacyAddress.setAddress(address.getAddress());
        legacyAddress.setNetwork(address.getNetwork());
        legacyAddress.setCreated_at(address.getCreatedAt());
        legacyAddress.setUpdated_at(address.getUpdatedAt());
        legacyAddress.setResource(address.getResource());
        legacyAddress.setResource_path(address.getResourcePath());
        return legacyAddress;
    }

    /**
     * Map {@link CoinbaseAccountResponse} to legacy {@link CBAccountResponse}.
     *
     * @param response The {@link CoinbaseAccountResponse} to map.
     * @return The mapped {@link CBAccountResponse}.
     */
    public static CBAccountResponse mapAccountResponseToLegacyResponse(CoinbaseAccountResponse response) {
        if (response == null) {
            return null;
        }

        CBAccountResponse legacyResponse = new CBAccountResponse();
        legacyResponse.setData(mapAccountToLegacyAccountV2(response.getAccount()));
        return legacyResponse;
    }

    private static CbAccountV2 mapAccountToLegacyAccountV2(CoinbaseAccount account) {
        if (account == null) {
            return null;
        }

        CbAccountV2 legacyAccount = new CbAccountV2();
        legacyAccount.setId(account.getId());
        legacyAccount.setName(account.getName());
        legacyAccount.setCurrency(mapCurrencyToLegacyCurrencyV1(account.getCurrency()));
        legacyAccount.setBalance(mapAmountToLegacyBalance(account.getBalance()));
        legacyAccount.setPrimary(account.isPrimary());
        return legacyAccount;
    }

    private static com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBCurrency mapCurrencyToLegacyCurrencyV1(
        CoinbaseCurrency currency) {
        if (currency == null) {
            return null;
        }

        com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBCurrency legacyCurrency
            = new com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBCurrency();
        legacyCurrency.code = currency.getCode();
        return legacyCurrency;
    }

    /**
     * Map legacy {@link CBSendRequest} to {@link CoinbaseSendCoinsRequest}.
     *
     * @param legacyRequest The {@link CBSendRequest} to map.
     * @return The mapped {@link CoinbaseSendCoinsRequest}.
     */
    public static CoinbaseSendCoinsRequest mapLegacySendRequestToRequest(CBSendRequest legacyRequest) {
        if (legacyRequest == null) {
            return null;
        }

        CoinbaseSendCoinsRequest request = new CoinbaseSendCoinsRequest();
        request.setTo(legacyRequest.getTo());
        request.setAmount(legacyRequest.getAmount());
        request.setCurrency(legacyRequest.getCurrency());
        request.setIdem(legacyRequest.getIdem());
        request.setDescription(legacyRequest.getDescription());
        return request;
    }

    /**
     * Map {@link CoinbaseTransactionResponse} to legacy {@link CBSendResponse}.
     *
     * @param response The {@link CoinbaseTransactionResponse} to map.
     * @return The mapped {@link CBSendResponse}.
     */
    public static CBSendResponse mapTransactionResponseToLegacySendResponse(CoinbaseTransactionResponse response) {
        if (response == null) {
            return null;
        }

        CBSendResponse legacySendResponse = new CBSendResponse();
        legacySendResponse.setData(mapTransactionToLegacySend(response.getTransaction()));
        return legacySendResponse;
    }

    private static CBSend mapTransactionToLegacySend(CoinbaseTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        CBSend legacySend = new CBSend();
        legacySend.setId(transaction.getId());
        return legacySend;
    }

    /**
     * Map legacy {@link CBCreateAddressRequest} to {@link CoinbaseCreateAddressRequest}.
     *
     * @param legacyRequest The {@link CBCreateAddressRequest} to map.
     * @return The mapped {@link CoinbaseCreateAddressRequest}.
     */
    public static CoinbaseCreateAddressRequest mapLegacyCreateAddressRequestToRequest(CBCreateAddressRequest legacyRequest) {
        if (legacyRequest == null) {
            return null;
        }

        CoinbaseCreateAddressRequest request = new CoinbaseCreateAddressRequest();
        request.setName(legacyRequest.name);
        return request;
    }

    /**
     * Map {@link CoinbaseCreateAddressResponse} to legacy {@link CBCreateAddressResponse}.
     *
     * @param response The {@link CoinbaseCreateAddressResponse} to map.
     * @return The mapped {@link CBCreateAddressResponse}.
     */
    public static CBCreateAddressResponse mapCreateAddressResponseToLegacyResponse(CoinbaseCreateAddressResponse response) {
        if (response == null) {
            return null;
        }

        CBCreateAddressResponse legacyResponse = new CBCreateAddressResponse();
        legacyResponse.setData(mapAddressToLegacyAddress(response.getAddress()));
        return legacyResponse;
    }

    /**
     * Map {@link CoinbaseTransactionsResponse} to legacy {@link CBPaginatedResponse<CBTransaction>}.
     *
     * @param response The {@link CoinbaseTransactionsResponse} to map.
     * @return The mapped {@link CBPaginatedResponse<CBTransaction>}.
     */
    public static CBPaginatedResponse<CBTransaction> mapTransactionsResponseToLegacyPaginatedResponse(CoinbaseTransactionsResponse response) {
        if (response == null) {
            return null;
        }

        CBPaginatedResponse<CBTransaction> legacyResponse = new CBPaginatedResponse<>();
        legacyResponse.setPagination(mapPaginationToLegacyPagination(response.getPagination()));
        if (response.getTransactions() != null) {
            legacyResponse.setData(response.getTransactions().stream()
                .map(CoinbaseV2ApiLegacyMapper::mapTransactionToLegacyTransaction)
                .collect(Collectors.toList()));
        }
        return legacyResponse;
    }

    private static CBTransaction mapTransactionToLegacyTransaction(CoinbaseTransaction transaction) {
        CBTransaction legacyTransaction = new CBTransaction();
        legacyTransaction.setId(transaction.getId());
        legacyTransaction.setType(transaction.getType());
        legacyTransaction.setStatus(transaction.getStatus());
        legacyTransaction.setAmount(mapAmountToLegacyBalance(transaction.getAmount()));
        legacyTransaction.setNative_amount(mapAmountToLegacyBalance(transaction.getNativeAmount()));
        legacyTransaction.setDescription(transaction.getDescription());
        legacyTransaction.setCreated_at(transaction.getCreatedAt());
        legacyTransaction.setUpdated_at(transaction.getUpdatedAt());
        legacyTransaction.setResource(transaction.getResource());
        legacyTransaction.setResource_path(transaction.getResourcePath());
        return legacyTransaction;
    }

    /**
     * Map {@link CoinbaseAddressesResponse} to legacy {@link CBPaginatedResponse<CBAddress>}.
     *
     * @param response The {@link CoinbaseAddressesResponse} to map.
     * @return The mapped {@link CBPaginatedResponse<CBAddress>}.
     */
    public static CBPaginatedResponse<CBAddress> mapAddressesResponseToLegacyPaginatedResponse(CoinbaseAddressesResponse response) {
        if (response == null) {
            return null;
        }

        CBPaginatedResponse<CBAddress> legacyResponse = new CBPaginatedResponse<>();
        legacyResponse.setPagination(mapPaginationToLegacyPagination(response.getPagination()));
        if (response.getAddresses() != null) {
            legacyResponse.setData(response.getAddresses().stream()
                .map(CoinbaseV2ApiLegacyMapper::mapAddressToLegacyAddress)
                .collect(Collectors.toList()));
        }
        return legacyResponse;
    }

}
