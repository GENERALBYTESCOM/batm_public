/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.digibyte.sources.coinmarketcap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1/ticker")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinmarketcapAPI {
  @GET
  @Path("/")
  CMCTicker[]  getTickers(@QueryParam("convert") String fiatCurrency);

  @GET
  @Path("/{cryptoToGet}/?convert={convert}")
  CMCTicker[]  getTickers(@PathParam("cryptoToGet") String cryptoToGet, @QueryParam("convert") String fiatCurrency);
}