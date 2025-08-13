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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBTransaction;
import si.mazi.rescu.ParamsDigest;

/**
 * Created by b00lean on 23.7.17.
 */
@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinbaseV2API {

    @GET
    @Path("/exchange-rates")
    CBExchangeRatesResponse getExchangeRates(@QueryParam("currency") String fiatCurrency);

    @GET
    @Path("/accounts")
    CBPaginatedResponse<CBAccount> getAccounts(@HeaderParam("CB-ACCESS-KEY") String apiKey,
                                               @HeaderParam("CB-VERSION") String apiVersion,
                                               @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                               @HeaderParam("CB-ACCESS-TIMESTAMP") long timestamp,
                                               // order of QueryParams here is important because of the CBDigest signature
                                               @QueryParam("limit") int limit,
                                               @QueryParam("starting_after") String startingAfter);

    @GET
    @Path("/accounts/{account_id}")
    CBAccountResponse getAccount(@HeaderParam("CB-ACCESS-KEY") String apiKey,
                                 @HeaderParam("CB-VERSION") String apiVersion,
                                 @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                 @HeaderParam("CB-ACCESS-TIMESTAMP") long timestamp,
                                 @PathParam("account_id") String accountId);


    @GET
    @Path("/accounts/{account_id}/addresses")
    CBAddressesResponse getAccountAddresses(@HeaderParam("CB-ACCESS-KEY") String apiKey,
                                            @HeaderParam("CB-VERSION") String apiVersion,
                                            @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                            @HeaderParam("CB-ACCESS-TIMESTAMP") long timestamp,
                                            @PathParam("account_id") String accountId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accounts/{account_id}/addresses")
    CBCreateAddressResponse createAddress(@HeaderParam("CB-ACCESS-KEY") String apiKey,
                                          @HeaderParam("CB-VERSION") String apiVersion,
                                          @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                          @HeaderParam("CB-ACCESS-TIMESTAMP") long timestamp,
                                          @PathParam("account_id") String accountId,
                                          CBCreateAddressRequest request);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accounts/{account_id}/transactions")
    CBSendResponse send(@HeaderParam("CB-ACCESS-KEY") String apiKey,
                        @HeaderParam("CB-VERSION") String apiVersion,
                        @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                        @HeaderParam("CB-ACCESS-TIMESTAMP") long timestamp,
                        @PathParam("account_id") String accountId,
                        CBSendRequest sendRequest);

    @GET
    @Path("/accounts/{account_id}/addresses/{address_id}/transactions")
    CBPaginatedResponse<CBTransaction> getAddressTransactions(@HeaderParam("CB-ACCESS-KEY") String apiKey,
                                                              @HeaderParam("CB-VERSION") String apiVersion,
                                                              @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                                              @HeaderParam("CB-ACCESS-TIMESTAMP") long timestamp,
                                                              @PathParam("account_id") String accountId,
                                                              @PathParam("address_id") String addressId,
                                                              // order of QueryParams here is important because of the CBDigest signature
                                                              @QueryParam("limit") int limit,
                                                              @QueryParam("starting_after") String startingAfter);

    @GET
    @Path("/accounts/{account_id}/addresses")
    CBPaginatedResponse<CBAddress> getAddresses(@HeaderParam("CB-ACCESS-KEY") String apiKey,
                                                @HeaderParam("CB-VERSION") String apiVersion,
                                                @HeaderParam("CB-ACCESS-SIGN") ParamsDigest digest,
                                                @HeaderParam("CB-ACCESS-TIMESTAMP") long timestamp,
                                                @PathParam("account_id") String accountId,
                                                // order of QueryParams here is important because of the CBDigest signature
                                                @QueryParam("limit") int limit,
                                                @QueryParam("starting_after") String startingAfter);

}
