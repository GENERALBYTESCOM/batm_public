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
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseExchangeRatesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseServerTimeResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionsResponse;
import si.mazi.rescu.ParamsDigest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Provides access to Coinbase API endpoints.
 *
 * <p>Uses the CDP API Key authorization.</p>
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinbaseV3Api {

    String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Get current exchange rates.
     * The default base currency is USD, but it can be defined as any supported currency.
     * Returned rates will define the exchange rate for one unit of the base currency.
     *
     * <p>This endpoint doesn't require authentication.</p>
     *
     * @param fiatCurrency Base currency (default: USD)
     * @return The exchange rates.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-exchange-rates">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/exchange-rates")
    CoinbaseExchangeRatesResponse getExchangeRates(@QueryParam("currency") String fiatCurrency) throws CoinbaseApiException;

    /**
     * Get the total price to buy or sell one unit of a cryptocurrency.
     *
     * <p>This endpoint doesn't require authentication.</p>
     *
     * @param currencyPair The cryptocurrency-fiatCurrency pair to get price for. (Example: "BTC-USD")
     * @param priceType    Type of the price.
     * @return The price of the given cryptocurrency in the given fiat currency.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-prices">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/prices/{currencyPair}/{priceType}")
    CoinbasePriceResponse getPrice(@PathParam("currencyPair") String currencyPair,
                                   @PathParam("priceType") String priceType) throws CoinbaseApiException;

    /**
     * Show (or get) a current user's account.
     * To access the primary account for a given currency,
     * a currency string (e.g., BTC or ETH) can be used instead of the account ID in the URL.
     *
     * @param accountId ID of the account.
     * @return The account.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-accounts#show-account">Coinbase Documentation</a>
     */
    @GET
    @Path("/api/v2/accounts/{accountId}")
    CoinbaseAccountResponse getAccount(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                       @PathParam("accountId") String accountId) throws CoinbaseApiException;

    /**
     * List a current user's accounts to which the authentication method has access to.
     *
     * @param limit         The maximum number of records (between 1 and 300). Default is 25.
     * @param startingAfter ID of the account to start from (not included in response). Can be null.
     * @return The list of accounts.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-accounts#list-accounts">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/accounts")
    CoinbaseAccountsResponse getAccounts(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                         @QueryParam("limit") Integer limit,
                                         @QueryParam("starting_after") String startingAfter) throws CoinbaseApiException;

    /**
     * Creates a new address for an account. Addresses can be created for wallet account types.
     *
     * @param accountId ID of the account.
     * @param request   The request.
     * @return Information about the created address.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-addresses#create-address">Coinbase Documentation</a>
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/v2/accounts/{accountId}/addresses")
    CoinbaseCreateAddressResponse createAddress(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                                @PathParam("accountId") String accountId,
                                                CoinbaseCreateAddressRequest request) throws CoinbaseApiException;

    /**
     * Lists addresses for an account.
     *
     * @param accountId     ID of the account.
     * @param limit         The maximum number of records.
     * @param startingAfter ID of the address to start from (not included in response).
     * @return The addresses.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-addresses#list-addresses">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/accounts/{accountId}/addresses")
    CoinbaseAddressesResponse getAddresses(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                           @PathParam("accountId") String accountId,
                                           @QueryParam("limit") Integer limit,
                                           @QueryParam("starting_after") String startingAfter) throws CoinbaseApiException;

    /**
     * Lists addresses for an account.
     *
     * @param accountId ID of the account.
     * @return The addresses.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-addresses#list-addresses">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/accounts/{accountId}/addresses")
    CoinbaseAddressesResponse getAddresses(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                           @PathParam("accountId") String accountId) throws CoinbaseApiException;

    /**
     * List transactions that have been sent to a specific address.
     * A regular cryptocurrency address can be used in place of addressId,
     * but the address must be associated with the correct account.
     *
     * @param accountId     ID of the account.
     * @param addressId     ID of the address.
     * @param limit         Maximum number of records.
     * @param startingAfter ID of the transaction to start from (not included in response).
     * @return The transactions.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-addresses#list-transactions">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/accounts/{accountId}/addresses/{addressId}/transactions")
    CoinbaseTransactionsResponse getAddressTransactions(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                                        @PathParam("accountId") String accountId,
                                                        @PathParam("addressId") String addressId,
                                                        @QueryParam("limit") Integer limit,
                                                        @QueryParam("starting_after") String startingAfter) throws CoinbaseApiException;

    /**
     * Send funds to a network address for any Coinbase supported asset, or email address of the recipient.
     * No transaction fees are required for off-blockchain cryptocurrency transactions.
     *
     * @param accountId ID of the account.
     * @param request   The request.
     * @return Information about the created transaction.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-transactions#send-money">Coinbase Documentation</a>
     */
    // TODO BATM-6210: Test with real funds
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/v2/accounts/{accountId}/transactions")
    CoinbaseTransactionResponse sendCoins(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                          @PathParam("accountId") String accountId,
                                          CoinbaseSendCoinsRequest request) throws CoinbaseApiException;

    /**
     * Get a single transaction for an account.
     *
     * @param accountId     ID of the account.
     * @param transactionId ID of the transaction.
     * @return The transaction.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-transactions#show-transaction">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/accounts/{accountId}/transactions/{transactionId}")
    CoinbaseTransactionResponse getTransaction(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                               @PathParam("accountId") String accountId,
                                               @PathParam("transactionId") String transactionId) throws CoinbaseApiException;

    /**
     * Get a list of payment methods for the current user.
     *
     * @return The payment methods.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/advanced-trade/reference/retailbrokerageapi_getpaymentmethods">Coinbase Documentation</a>
     */
    @GET
    @Path("/api/v3/brokerage/payment_methods")
    CoinbasePaymentMethodsResponse getPaymentMethods(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization) throws CoinbaseApiException;

    /**
     * Create an order with a specified product_id (asset-pair), side (buy/sell), etc.
     *
     * @param request The request.
     * @return Information about the new order.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/advanced-trade/reference/retailbrokerageapi_postorder">Coinbase Documentation</a>
     */
    // TODO BATM-6210: Test with real funds
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/api/v3/brokerage/orders")
    CoinbaseCreateOrderResponse createOrder(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                            CoinbaseCreateOrderRequest request) throws CoinbaseApiException;

    /**
     * Get a single order by order ID.
     *
     * @param orderId ID of the order to get.
     * @return The order.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/advanced-trade/reference/retailbrokerageapi_gethistoricalorder">Coinbase Documentation</a>
     */
    @GET
    @Path("/api/v3/brokerage/orders/historical/{orderId}")
    CoinbaseOrderResponse getOrder(@HeaderParam(HEADER_AUTHORIZATION) ParamsDigest authorization,
                                   @PathParam("orderId") String orderId) throws CoinbaseApiException;

    /**
     * Get the API server time.
     *
     * <p>This endpoint doesn't require authentication.</p>
     *
     * @return The Coinbase API server time.
     * @throws CoinbaseApiException If the API call fails.
     * @see <a href="https://docs.cdp.coinbase.com/coinbase-app/docs/api-time#get-current-time">Coinbase Documentation</a>
     */
    @GET
    @Path("/v2/time")
    CoinbaseServerTimeResponse getServerTime() throws CoinbaseApiException;

}
