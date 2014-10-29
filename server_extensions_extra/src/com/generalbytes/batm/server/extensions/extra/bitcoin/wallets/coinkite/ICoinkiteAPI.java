/*************************************************************************************
 * Copyright (C) 2014 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinkite;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinkiteAPI {
    @GET
    @Path("/account/{accountId}")
    AccountResponse getAccount(@HeaderParam("X-CK-Key") String apiKey, @HeaderParam("X-CK-Sign") String signature, @HeaderParam("X-CK-Timestamp") String timestamp, @PathParam("accountId") String accountId);

    @GET
    @Path("/my/self")
    void getInfo(@HeaderParam("X-CK-Key") String apiKey, @HeaderParam("X-CK-Sign") String signature, @HeaderParam("X-CK-Timestamp") String timestamp);

    @PUT
    @Path("/new/send")
    @Consumes(MediaType.APPLICATION_JSON)
    CoinkiteSendResponse send(@HeaderParam("X-CK-Key") String apiKey, @HeaderParam("X-CK-Sign") String signature, @HeaderParam("X-CK-Timestamp") String timestamp, CoinkiteSendRequest request);

    @PUT
    @Path("/update/{refnum}/auth_send")
    CoinkiteSendResponse authSend(@HeaderParam("X-CK-Key") String apiKey, @HeaderParam("X-CK-Sign") String signature, @HeaderParam("X-CK-Timestamp") String timestamp, @PathParam("refnum") String refnum, @QueryParam("authcode") String authcode);

}
