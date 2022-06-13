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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface IAquaNowAPI {

    @GET
    @Path("/bestprice")
    BestPriceResponse getbestprice(@QueryParam("symbol") String symbol) throws IOException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/trades/v1/market")
    TradeCoinResponse buyCoins(@HeaderParam("x-api-key") String apikey,
                               @HeaderParam("x-nonce") String nonce,
                               @HeaderParam("x-signature") AquaNowDigest digest,
                               BuyCoinRequest buycoin) throws IOException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/trades/v1/market")
    TradeCoinResponse sellCoins(@HeaderParam("x-api-key") String apikey,
                                @HeaderParam("x-nonce") String nonce,
                                @HeaderParam("x-signature") AquaNowDigest digest,
                                SellCoinRequest sellcoin) throws IOException;

    @GET
    @Path("/trades/v2/order")
    OrderStatusResponse getOrderStatus(@HeaderParam("x-api-key") String apikey,
                                       @HeaderParam("x-nonce") String nonce,
                                       @HeaderParam("x-signature") AquaNowDigest digest,
                                       @QueryParam("orderId") String orderId) throws IOException;

    @GET
    @Path("/users/v1/userbalance")
    UserBalanceResponse getUserBalance(@HeaderParam("x-api-key") String apikey,
                                       @HeaderParam("x-nonce") String nonce,
                                       @HeaderParam("x-signature") AquaNowDigest digest,
                                       @QueryParam("symbol") String symbol) throws IOException;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accounts/v1/createAddress")
    GetAddressResponse getUserAddress(@HeaderParam("x-api-key") String apikey,
                                       @HeaderParam("x-nonce") String nonce,
                                       @HeaderParam("x-signature") AquaNowDigest digest,
                                       GetAddressRequest getAddressRequest) throws IOException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accounts/v1/transaction")
    SendCoinResponse sendCoins(@HeaderParam("x-api-key") String apikey,
                          @HeaderParam("x-nonce") String nonce,
                          @HeaderParam("x-signature") AquaNowDigest digest,
                          SendCoinRequest sendCoinRequest) throws IOException;




}
