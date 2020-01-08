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
package com.generalbytes.batm.server.extensions.extra.bitcoincash.wallets.telr;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

// https://api.telr.io/v1
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1")
public interface ITelrCashProxyAPI {
    @GET
    @Path("/crypto/address")
    String getCryptoAddress(@HeaderParam("x-telr-address") String authAddress, @HeaderParam("x-telr-secret") String authSecret, @HeaderParam("x-telr-signature") String authSignature, @QueryParam("crypto") String cryptoCurrency) throws IOException;

    @GET
    @Path("/crypto/balance")
    String getCryptoBalance(@HeaderParam("x-telr-address") String authAddress, @HeaderParam("x-telr-secret") String authSecret, @HeaderParam("x-telr-signature") String authSignature, @QueryParam("crypto") String cryptoCurrency) throws IOException;

    @POST
    @Path("/crypto/send")
    String sendCoins(@HeaderParam("x-telr-address") String authAddress, @HeaderParam("x-telr-secret") String authSecret, @HeaderParam("x-telr-signature") String authSignature, @QueryParam("crypto") String cryptoCurrency, @QueryParam("address") String destinationAddress, @QueryParam("amount") long amount, @QueryParam("description") String description) throws IOException;
}
