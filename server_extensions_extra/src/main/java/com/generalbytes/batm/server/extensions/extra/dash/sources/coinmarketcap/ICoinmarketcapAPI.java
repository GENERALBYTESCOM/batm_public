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
package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;
@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinmarketcapAPI {
    @GET
    @Path("/ticker/{id}/")
    CmcTickerResponse  getTicker(@PathParam("id") Integer id, @QueryParam("convert") String fiatCurrency);

    /**
     * Method getListings() returns map which contain all suported crypto currencies.
     * For all cryptocurrencies there is associated id.
     *
     * @return Map of cryptocurrencies
     */
    @GET
    @Path("/listings")
    Map<String, Object> getListings();
}
