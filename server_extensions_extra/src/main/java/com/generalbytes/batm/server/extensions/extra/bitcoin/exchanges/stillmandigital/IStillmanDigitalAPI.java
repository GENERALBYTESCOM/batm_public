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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.*;
import com.generalbytes.batm.server.extensions.util.net.RateLimitingInterceptor;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.Interceptor;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface IStillmanDigitalAPI {

    String API_EXPIRES_HEADER = "api-timestamp";

    static IStillmanDigitalAPI create(String apiKey, String apiSecret,
                                      boolean useSandbox) throws GeneralSecurityException {
        return create(apiKey, apiSecret,
            useSandbox ? "https://sandbox-api.stillmandigital.com" : "https://api.stillmandigital.com");
    }

    static IStillmanDigitalAPI create(String apiKey, String apiSecret, String baseUrl) throws GeneralSecurityException {
        final ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, "api-key", apiKey);
        config.addDefaultParam(HeaderParam.class, API_EXPIRES_HEADER, new CurrentTimeFactory());
        config.addDefaultParam(HeaderParam.class, "api-signature", new StillmanDigitalDigest(apiSecret));
        Interceptor interceptor = new RateLimitingInterceptor(IStillmanDigitalAPI.class, 25, 30_000);
        return RestProxyFactory.createProxy(IStillmanDigitalAPI.class, baseUrl, config, interceptor);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/v1/trading/rate")
    Rate requestRate(RateRequest request) throws IOException;

    @GET
    @Path("/v1/balances")
    List<RowBalanceByAssetResponse> getBalance() throws IOException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/v1/trading/new")
    NewOrderResponse sendOrder(OrderRequest orderRequest) throws IOException;

    @GET
    @Path("/v1/orders/{id}")
    RowOrderResponse getOrder(@PathParam("id")  long orderId) throws IOException;

}
