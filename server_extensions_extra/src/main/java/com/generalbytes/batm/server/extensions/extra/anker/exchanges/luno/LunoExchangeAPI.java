/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.anker.exchanges.luno;

import java.math.BigDecimal;

import javax.ws.rs.*; 
import javax.ws.rs.core.MediaType; 
@Path("api/1") 
@Produces(MediaType.APPLICATION_JSON)
public interface LunoExchangeAPI {
    @GET
    @Path("/balance")
    LunoBalanceData getBalance();

    @GET
    @Path("/balance")
    String getBalanceTest();

    @GET
    @Path("/funding_address")
    LunoAddressData getAddress(@QueryParam("asset") String symbol);

    @POST
    @Path("/send")
    LunoRequestData sendMoney(@QueryParam("address") String destinationAddress, @QueryParam("amount") BigDecimal amount, @QueryParam("currency") String cryptoCurrency, @QueryParam("description") String description);

    @POST
    @Path("/marketorder")
    LunoOrderData createBuyOrder(@QueryParam("pair") String pair, @QueryParam("type") String type, @QueryParam("counter_volume") BigDecimal volume);

    @POST
    @Path("/marketorder")
    LunoOrderData createSellOrder(@QueryParam("pair") String pair, @QueryParam("type") String type, @QueryParam("base_volume") BigDecimal volume);

    @POST
    @Path("/postorder")
    LunoOrderData createLimitBuyOrder(@QueryParam("pair") String pair, @QueryParam("type") String type, @QueryParam("volume") BigDecimal volume, @QueryParam("price") BigDecimal price);

    @POST
    @Path("/postorder")
    LunoOrderData createLimitSellOrder(@QueryParam("pair") String pair, @QueryParam("type") String type, @QueryParam("volume") BigDecimal volume, @QueryParam("price") BigDecimal price);

    @GET
    @Path("/ticker")
    LunoTickerData getTicker(@QueryParam("pair") String symbol);

}