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

public class CoinbaseV2ApiWrapperLegacy implements CoinbaseV2ApiWrapper {

    private final ICoinbaseV2APILegacy api;
    private final String apiKey;
    private final String secretKey;

    public CoinbaseV2ApiWrapperLegacy(CoinbaseApiFactory apiFactory, String apiKey, String secretKey) {
        this.api = apiFactory.createCoinbaseV2ApiLegacy();
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    @Override
    public CBExchangeRatesResponse getExchangeRates(String fiatCurrency) {
        return api.getExchangeRates(fiatCurrency);
    }

    @Override
    public CBPaginatedResponse<CBAccount> getAccounts(String apiVersion, long coinbaseTime, int limit, String startingAfterAccountId) {
        CBDigest authorizationDigest = CBDigest.createInstance(secretKey, coinbaseTime);
        return api.getAccounts(apiKey, apiVersion, authorizationDigest, coinbaseTime, limit, startingAfterAccountId);
    }

    @Override
    public CBAddressesResponse getAccountAddresses(String apiVersion, long coinbaseTime, String accountId) {
        CBDigest authorizationDigest = CBDigest.createInstance(secretKey, coinbaseTime);
        return api.getAccountAddresses(apiKey, apiVersion, authorizationDigest, coinbaseTime, accountId);
    }

    @Override
    public CBAccountResponse getAccount(String apiVersion, long coinbaseTime, String accountId) {
        CBDigest authorizationDigest = CBDigest.createInstance(secretKey, coinbaseTime);
        return api.getAccount(apiKey, apiVersion, authorizationDigest, coinbaseTime, accountId);
    }

    @Override
    public CBSendResponse send(String apiVersion, long coinbaseTime, String accountId, CBSendRequest request) {
        CBDigest authorizationDigest = CBDigest.createInstance(secretKey, coinbaseTime);
        return api.send(apiKey, apiVersion, authorizationDigest, coinbaseTime, accountId, request);
    }

    @Override
    public CBCreateAddressResponse createAddress(String apiVersion, long coinbaseTime, String accountId, CBCreateAddressRequest request) {
        CBDigest authorizationDigest = CBDigest.createInstance(secretKey, coinbaseTime);
        return api.createAddress(apiKey, apiVersion, authorizationDigest, coinbaseTime, accountId, request);
    }

    @Override
    public CBPaginatedResponse<CBTransaction> getAddressTransactions(String apiVersion,
                                                                     long coinbaseTime,
                                                                     String accountId,
                                                                     String addressId,
                                                                     int limit,
                                                                     String startingAfterTransactionId) {
        CBDigest authorizationDigest = CBDigest.createInstance(secretKey, coinbaseTime);
        return api.getAddressTransactions(apiKey, apiVersion, authorizationDigest, coinbaseTime, accountId, addressId, limit, startingAfterTransactionId);
    }

    @Override
    public CBPaginatedResponse<CBAddress> getAddresses(String apiVersion,
                                                       long coinbaseTime,
                                                       String accountId,
                                                       int limit,
                                                       String startingAfterAddressId) {
        CBDigest authorizationDigest = CBDigest.createInstance(secretKey, coinbaseTime);
        return api.getAddresses(apiKey, apiVersion, authorizationDigest, coinbaseTime, accountId, limit, startingAfterAddressId);
    }

}
