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

/**
 * Represents a general Coinbase V2 API wrapper, that can be implemented
 * for any authorization or API access method.
 */
public interface CoinbaseV2ApiWrapper {

    CBExchangeRatesResponse getExchangeRates(String fiatCurrency);

    CBPaginatedResponse<CBAccount> getAccounts(String apiVersion, long coinbaseTime, int limit, String startingAfterAccountId);

    CBAddressesResponse getAccountAddresses(String apiVersion, long coinbaseTime, String accountId);

    CBAccountResponse getAccount(String apiVersion, long coinbaseTime, String accountId);

    CBSendResponse send(String apiVersion, long coinbaseTime, String accountId, CBSendRequest request);

    CBCreateAddressResponse createAddress(String apiVersion, long coinbaseTime, String accountId, CBCreateAddressRequest request);

    CBPaginatedResponse<CBTransaction> getAddressTransactions(String apiVersion, long coinbaseTime, String accountId, String addressId,
                                                              int limit, String startingAfterTransactionId);

    CBPaginatedResponse<CBAddress> getAddresses(String apiVersion, long coinbaseTime, String accountId, int limit,
                                                String startingAfterAddressId);

}
