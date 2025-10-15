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
package com.generalbytes.batm.server.extensions.extra.potcoin.wallets.potwallet;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface PotwalletAPI {
    @GET
    @Path("/balance")
    PotwalletResponse getCryptoBalance(@HeaderParam("Access-Public") String publicKey, @HeaderParam("Access-Hash") String accessHash, @HeaderParam("Access-Nonce") String nonce) throws IOException;
    @GET
    @Path("/address")
    PotwalletResponse getAddress(@HeaderParam("Access-Public") String publicKey, @HeaderParam("Access-Hash") String accessHash, @HeaderParam("Access-Nonce") String nonce) throws IOException;
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    PotwalletResponse sendPots(@HeaderParam("Access-Public") String publicKey, @HeaderParam("Access-Hash") String accessHash, @HeaderParam("Access-Nonce") String nonce, PotwalletSendRequest request) throws IOException;
}
