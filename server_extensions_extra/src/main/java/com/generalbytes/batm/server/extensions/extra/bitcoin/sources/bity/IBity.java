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

package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity;


import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bity.dto.RateInfo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/api/v1")
public interface IBity {


    @GET
    @Path("/rate_we_sell/{from_currency}{to_currency}/")
    RateInfo getRateBuy(@PathParam("from_currency") String fromCurrency, @PathParam("to_currency") String toCurrency);

    @GET
    @Path("/rate_we_buy/{from_currency}{to_currency}/")
    RateInfo getRateSell(@PathParam("from_currency") String fromCurrency, @PathParam("to_currency") String toCurrency);

}
