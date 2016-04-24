/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 * <p/>
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 * <p/>
 * Contact information
 * -------------------
 * <p/>
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.shadowcash.sources.bittrex;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/api/v1.1/public")
@Produces(MediaType.APPLICATION_JSON)
public interface IBittrexAPI {

    @GET
    @Path("/getorderbook")
    BittrexOrderBookResponse returnOrderBook(@QueryParam("market") String market, @QueryParam("type") String type, @QueryParam("depth") long depth);

}
