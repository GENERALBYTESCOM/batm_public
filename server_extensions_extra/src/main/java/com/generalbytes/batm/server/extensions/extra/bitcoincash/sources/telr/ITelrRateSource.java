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
package com.generalbytes.batm.server.extensions.extra.bitcoincash.sources.telr;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

// https://api.telr.io/v1
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1")
public interface ITelrRateSource {
    @GET
    @Path("/ticker/price/{quote_currency}")
    String getPrice(@HeaderParam("x-telr-address") String authAddress, @HeaderParam("x-telr-secret") String authSecret, @HeaderParam("x-telr-signature") String authSignature, @PathParam("quote_currency") String quoteCurrency);
}
