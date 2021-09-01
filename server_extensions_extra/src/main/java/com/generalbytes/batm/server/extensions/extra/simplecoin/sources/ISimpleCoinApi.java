/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/


package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

// https://server.simplecoin.eu/v1/ticker/market?from=EUR&to=BTC
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface ISimpleCoinApi {

    @GET
    @Path("/v1/ticker/market")
    public FiatCryptoResponse returnRate(@QueryParam("from") String fromCurrency, @QueryParam("to") String toCurrency);
}

/*
    {
      "status": "ok",
      "response": {
        "rate": 6647.187084914325,
        "rate_inverse": 0.000150439575
      },
      "error": null,
      "error_code": null
    }
 */
