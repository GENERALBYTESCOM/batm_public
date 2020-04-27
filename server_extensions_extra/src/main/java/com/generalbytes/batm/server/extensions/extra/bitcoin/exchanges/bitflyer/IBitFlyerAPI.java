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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitflyer;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitflyer.dto.*;
import si.mazi.rescu.ParamsDigest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SuppressWarnings("all")
public interface IBitFlyerAPI {

    @GET
    @Path("/executions?product_code={product_code}&count={count}")
    BFYExecutionHistoryResponse[] getExecutionHistory(@PathParam("product_code") String productCode,
                                                      @PathParam("count") int count);

    @GET
    @Path("/board?product_code={product_code}")
    BFYOrderBookResponse getOrderBook(@PathParam("product_code") String productCode);

    @GET
    @Path("/me/getpermissions")
    String[] getPermissions(@HeaderParam("ACCESS-KEY") String key,
                            @HeaderParam("ACCESS-TIMESTAMP") String timestamp,
                            @HeaderParam("ACCESS-SIGN") ParamsDigest digest);

    @GET
    @Path("/me/getbalance")
    BFYAccountAssetBalanceResponse[] getBalance(@HeaderParam("ACCESS-KEY") String key,
                                                @HeaderParam("ACCESS-TIMESTAMP") String timestamp,
                                                @HeaderParam("ACCESS-SIGN") ParamsDigest digest);

    @GET
    @Path("/me/getaddresses")
    BFYDepositAddressResponse[] getAddresses(@HeaderParam("ACCESS-KEY") String key,
                                             @HeaderParam("ACCESS-TIMESTAMP") String timestamp,
                                             @HeaderParam("ACCESS-SIGN") ParamsDigest digest);

    @POST
    @Path("/me/sendchildorder")
    BFYNewOrderResponse sendOrder(@HeaderParam("ACCESS-KEY") String key,
                                  @HeaderParam("ACCESS-TIMESTAMP") String timestamp,
                                  @HeaderParam("ACCESS-SIGN") ParamsDigest digest,
                                  BFYNewOrderRequest request);

    @GET
    @Path("/me/getchildorders?product_code={product_code}&count={count}&child_order_state=ACTIVE")
    BFYListOrdersResponse[] getActiveOrders(@HeaderParam("ACCESS-KEY") String key,
                                            @HeaderParam("ACCESS-TIMESTAMP") String timestamp,
                                            @HeaderParam("ACCESS-SIGN") ParamsDigest digest,
                                            @PathParam("product_code") String productCode,
                                            @PathParam("count") int count);

    @POST
    @Path("/me/sendcoin")
    BFYSendCoinsResponse sendCoins(@HeaderParam("ACCESS-KEY") String key,
                                   @HeaderParam("ACCESS-TIMESTAMP") String timestamp,
                                   @HeaderParam("ACCESS-SIGN") ParamsDigest digest,
                                   BFYSendCoinsRequest request);
}
