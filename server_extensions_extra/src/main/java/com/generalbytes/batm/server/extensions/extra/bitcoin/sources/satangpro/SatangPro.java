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
 * Author   :  pawel.nowacki@teleit.pl / +48.600100825 - wanda.exchange
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.satangpro;

import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.satangpro.dto.SatangProRateInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/api")
public interface SatangPro {
    @GET
    @Path("orderbook-tickers")
    Map<String, SatangProRateInfo> getTicker();
}
