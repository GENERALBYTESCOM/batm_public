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
