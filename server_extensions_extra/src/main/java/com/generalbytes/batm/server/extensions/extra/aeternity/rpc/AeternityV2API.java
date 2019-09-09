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
package com.generalbytes.batm.server.extensions.extra.aeternity.rpc;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// https://www.coingecko.com/api/documentations/v3
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public interface AeternityV2API {
    @GET
    @Path("/v2/accounts/{address}") //ak_2MR38Zf355m6JtP13T3WEcUcSLVLCxjGvjk6zG95S2mfKohcSS
    Object getAccountBalance(@PathParam("address") String address) throws IOException;
    
    @GET
    @Path("/v2/status")
    Object getBlockCount() throws IOException;
    
    @GET
    @Path("/v2/micro-blocks/hash/{blockHash}/header")
    Object getBlock(@PathParam("blockHash") String blockHash) throws IOException;
    
    @GET
    @Path("/v2/status")
    Object getStatus() throws IOException;
    
    @GET
    @Path("/middleware/transactions/account/{address}")
    Object getAccountTXs(@PathParam("address") String address) throws IOException;
}
