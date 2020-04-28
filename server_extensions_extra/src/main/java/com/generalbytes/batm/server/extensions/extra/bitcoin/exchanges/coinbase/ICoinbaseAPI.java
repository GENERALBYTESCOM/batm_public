/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBCurrencyResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTimeResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTransactionResponse;
import si.mazi.rescu.ParamsDigest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
@SuppressWarnings("all")
public interface ICoinbaseAPI {

    @GET
    @Path("/currencies")
    CBCurrencyResponse getSupportedCurrencies(@HeaderParam("CB-VERSION") String version) throws IOException, RuntimeException;

    @GET
    @Path("/time")
    CBTimeResponse getTime(@HeaderParam("CB-VERSION") String version) throws IOException, RuntimeException;

    @GET
    @Path("/prices/{currency_pair}/{price_type}")
    CBPriceResponse getPrice(@HeaderParam("CB-VERSION") String version,
                             @PathParam("currency_pair") String currencyPair,
                             @PathParam("price_type") String priceType) throws IOException, RuntimeException;


    @GET
    @Path("/accounts")
    CBAccountsResponse getAccounts(@HeaderParam("CB-VERSION") String version,
                                   @HeaderParam("CB-ACCESS-KEY") String key,
                                   @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                   @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime) throws IOException, RuntimeException;

    @POST
    @Path("/accounts/{account_id}/addresses")
    CBNewAddressResponse getNewAddress(@HeaderParam("CB-VERSION") String version,
                                       @HeaderParam("CB-ACCESS-KEY") String key,
                                       @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                       @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime,
                                       @PathParam("account_id") String accountId) throws IOException, RuntimeException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accounts/{account_id}/transactions")
    CBSendCoinsResponse sendCoins(@HeaderParam("CB-VERSION") String version,
                                  @HeaderParam("CB-ACCESS-KEY") String key,
                                  @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                  @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime,
                                  @PathParam("account_id") String accountId,
                                  CBSendCoinsRequest sendCoinsRequest)  throws IOException, RuntimeException;

    @GET
    @Path("/accounts/{account_id}/transactions/{transaction_id}")
    CBTransactionResponse showTransaction(@HeaderParam("CB-VERSION") String version,
                                          @HeaderParam("CB-ACCESS-KEY") String key,
                                          @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                          @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime,
                                          @PathParam("account_id") String accountId,
                                          @PathParam("transaction_id") String transactionId) throws IOException, RuntimeException;

    @GET
    @Path("/payment-methods")
    CBPaymentMethodsResponse listPaymentMethods(@HeaderParam("CB-VERSION") String version,
                                                @HeaderParam("CB-ACCESS-KEY") String key,
                                                @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                                @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime) throws IOException, RuntimeException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accounts/{account_id}/buys")
    CBOrderResponse buyCoins(@HeaderParam("CB-VERSION") String version,
                             @HeaderParam("CB-ACCESS-KEY") String key,
                             @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                             @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime,
                             @PathParam("account_id") String accountId,
                             CBOrderRequest orderRequest)  throws IOException, RuntimeException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accounts/{account_id}/sells")
    CBOrderResponse sellCoins(@HeaderParam("CB-VERSION") String version,
                              @HeaderParam("CB-ACCESS-KEY") String key,
                              @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                              @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime,
                              @PathParam("account_id") String accountId,
                              CBOrderRequest orderRequest)  throws IOException, RuntimeException;

    @GET
    @Path("/accounts/{account_id}/buys/{buy_id}")
    CBOrderResponse getBuyOrder(@HeaderParam("CB-VERSION") String version,
                                @HeaderParam("CB-ACCESS-KEY") String key,
                                @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime,
                                @PathParam("account_id") String accountId,
                                @PathParam("buy_id") String buyId)  throws IOException, RuntimeException;

    @GET
    @Path("/accounts/{account_id}/sells/{sell_id}")
    CBOrderResponse getSellOrder(@HeaderParam("CB-VERSION") String version,
                                 @HeaderParam("CB-ACCESS-KEY") String key,
                                 @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                 @HeaderParam("CB-ACCESS-TIMESTAMP") String coinBaseTime,
                                 @PathParam("account_id") String accountId,
                                 @PathParam("sell_id") String sellId)  throws IOException, RuntimeException;
}
