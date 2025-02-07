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

public class CoinbaseV2ApiWrapperCdp implements CoinbaseV2ApiWrapper {

    private final ICoinbaseV3Api api;
    private final String privateKey;
    private final String keyName;

    public CoinbaseV2ApiWrapperCdp(ICoinbaseV3Api api, String privateKey, String keyName) {
        this.api = api;
        this.privateKey = privateKey;
        this.keyName = keyName;
    }

    @Override
    public CBExchangeRatesResponse getExchangeRates(String fiatCurrency) {
        try {
            CoinbaseExchangeRatesResponse response = api.getExchangeRates(fiatCurrency);
            return CoinbaseV2ApiMapper.mapExchangeRatesResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBExchangeRatesResponse());
        }
    }

    @Override
    public CBPaginatedResponse<CBAccount> getAccounts(String apiVersion, long coinbaseTime, int limit, String startingAfterAccountId) {
        validateCredentials();
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseAccountsResponse response = api.getAccounts(authorizationDigest, limit, startingAfterAccountId);
            return CoinbaseV2ApiMapper.mapAccountsResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBPaginatedResponse<>());
        }
    }

    @Override
    public CBAddressesResponse getAccountAddresses(String apiVersion, long coinbaseTime, String accountId) {
        validateCredentials();
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseAddressesResponse response = api.getAddresses(authorizationDigest, accountId);
            return CoinbaseV2ApiMapper.mapAddressesResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBAddressesResponse());
        }
    }

    @Override
    public CBAccountResponse getAccount(String apiVersion, long coinbaseTime, String accountId) {
        validateCredentials();
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseAccountResponse response = api.getAccount(authorizationDigest, accountId);
            return CoinbaseV2ApiMapper.mapAccountResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBAccountResponse());
        }
    }

    @Override
    public CBSendResponse send(String apiVersion, long coinbaseTime, String accountId, CBSendRequest legacyRequest) {
        validateCredentials();
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        CoinbaseSendCoinsRequest request = CoinbaseV2ApiMapper.mapLegacySendRequestToRequest(legacyRequest);
        try {
            CoinbaseTransactionResponse response = api.sendCoins(authorizationDigest, accountId, request);
            return CoinbaseV2ApiMapper.mapTransactionResponseToLegacySendResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBSendResponse());
        }
    }

    @Override
    public CBCreateAddressResponse createAddress(String apiVersion,
                                                 long coinbaseTime,
                                                 String accountId,
                                                 CBCreateAddressRequest legacyRequest) {
        validateCredentials();
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        CoinbaseCreateAddressRequest request = CoinbaseV2ApiMapper.mapLegacyCreateAddressRequestToRequest(legacyRequest);
        try {
            CoinbaseCreateAddressResponse response = api.createAddress(authorizationDigest, accountId, request);
            return CoinbaseV2ApiMapper.mapCreateAddressResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBCreateAddressResponse());
        }
    }

    @Override
    public CBPaginatedResponse<CBTransaction> getAddressTransactions(String apiVersion,
                                                                     long coinbaseTime,
                                                                     String accountId,
                                                                     String addressId,
                                                                     int limit,
                                                                     String startingAfterTransactionId) {
        validateCredentials();
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseTransactionsResponse response = api.getAddressTransactions(authorizationDigest, accountId, addressId, limit,
                    startingAfterTransactionId);
            return CoinbaseV2ApiMapper.mapTransactionsResponseToLegacyPaginatedResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBPaginatedResponse<>());
        }
    }

    @Override
    public CBPaginatedResponse<CBAddress> getAddresses(String apiVersion,
                                                       long coinbaseTime,
                                                       String accountId,
                                                       int limit,
                                                       String startingAfterAddressId) {
        validateCredentials();
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseAddressesResponse response = api.getAddresses(authorizationDigest, accountId, limit, startingAfterAddressId);
            return CoinbaseV2ApiMapper.mapAddressesResponseToLegacyPaginatedResponse(response);
        } catch (CoinbaseApiException e) {
            return CoinbaseV2ApiMapper.mapExceptionToLegacyResponse(e, new CBPaginatedResponse<>());
        }
    }

    private void validateCredentials() {
        validateNotNull(privateKey, "privateKey");
        validateNotNull(keyName, "keyName");
    }

    private void validateNotNull(String string, String fieldName) {
        if (string == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
}
