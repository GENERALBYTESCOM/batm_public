/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

/**
 * A copy of {@link ICoinmarketcapAPI} that returns just map of strings instead of deserialized objects.
 * Used for tests only
 */
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface TestCoinmarketcapAPI {
    @GET
    @Path("/cryptocurrency/quotes/latest")
    Map<String, Map<String, Object>> getTicker(@HeaderParam("X-CMC_PRO_API_KEY") String apikey, @QueryParam("symbol") String symbol, @QueryParam("convert") String fiatCurrency) throws IOException;
}
