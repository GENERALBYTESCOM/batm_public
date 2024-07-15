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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.BitbuyResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.CreateOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.DepositAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.OrderBook;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto.QuoteRequest;
import com.generalbytes.batm.server.extensions.util.OrderBookPriceCalculator;
import com.generalbytes.batm.server.extensions.util.net.RateLimitingInterceptor;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.ClientConfigUtil;
import si.mazi.rescu.Interceptor;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface IBitbuyAPI {
    String prodUrl = "https://api-crypto.bitbuy.ca";
    String testUrl = "https://bb-api-crypto-qa.blockchainmarkets.com";

    static IBitbuyAPI create(String clientId, String secretKey) {
        final ClientConfig config = new ClientConfig();
        ClientConfigUtil.addBasicAuthCredentials(config, clientId, secretKey);
        Interceptor interceptor = new RateLimitingInterceptor(IBitbuyAPI.class, 25, 30_000);
        return RestProxyFactory.createProxy(IBitbuyAPI.class, prodUrl, config, interceptor);
    }

    /**
     * @param currency fiat or crypto currency
     */
    @GET
    @Path("/account/balance")
    BitbuyResponse<Balance> getBalance(@QueryParam("currency") String currency) throws IOException;

    /**
     * @return deposit address for the given coin (new address is NOT generated on each call)
     */
    @GET
    @Path("/account/deposit-address")
    BitbuyResponse<DepositAddress> getDepositAddress(@QueryParam("coin") String coin) throws IOException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/rfq/create")
    BitbuyResponse<CreateOrderResponse> createOrder(QuoteRequest quoteRequest) throws IOException;

    @POST
    @Path("/rfq/execute")
    BitbuyResponse<Void> executeOrder(@QueryParam("id") String orderId) throws IOException;

    /**
     * @param depth number of bids and asks, e.g. for depth 10, 10 asks + 10 bids will be returned.
     *              Increase this if {@link OrderBookPriceCalculator} does not have enough orders to compute the price.
     */
    @GET
    @Path("/public/markets/{marketId}/{depth}")
    BitbuyResponse<OrderBook> getOrderBook(@PathParam("marketId") String market, @PathParam("depth") int depth) throws IOException;
}
