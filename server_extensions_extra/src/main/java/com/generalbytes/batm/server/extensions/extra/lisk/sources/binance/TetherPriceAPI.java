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
package com.generalbytes.batm.server.extensions.extra.lisk.sources.binance;

import javax.ws.rs.*;
import java.util.List;
import javax.ws.rs.core.MediaType;
import java.util.Map;
@Path("/api/v3/coins") 
@Produces(MediaType.APPLICATION_JSON)
public interface TetherPriceAPI {
	@GET
	@Path("/markets")
	List<Object> getTetherPrice(@QueryParam("vs_currency") String vs_currency, @QueryParam("ids") String Flatcurrency);
	 
    
}
