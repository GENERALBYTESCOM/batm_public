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
package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v2/")
@Produces(MediaType.APPLICATION_JSON)
public interface IBlockIO {

    String PRIORITY_LOW = "low";
    String PRIORITY_MEDIUM = "medium";
    String PRIORITY_HIGH = "high";

    @GET
    @Path("get_my_addresses/?api_key={apikey}")
    BlockIOResponseAddresses getAddresses(@PathParam("apikey") String apikey);

    @GET
    @Path("get_balance/?api_key={apikey}")
    BlockIOResponseBalance getBalance(@PathParam("apikey") String apikey);

    @GET
    @Path("withdraw/?api_key={apikey}&amounts={amount}&to_addresses={payment_address}&pin={pin}&priority={priority}")
    BlockIOResponseWithdrawal withdraw(@PathParam("apikey") String apikey, @PathParam("pin") String pin, @PathParam("amount") String amount, @PathParam("payment_address") String payment_address, @PathParam("priority") String priority);

    @POST
    @Path("sign_and_finalize_withdrawal?api_key={apikey}")
    BlockIOResponseWithdrawal signAndFinalizeWithdrawal(@PathParam("apikey") String apikey, @FormParam("signature_data") String signedDataInJson);

    @GET
    @Path("withdraw/?api_key={apikey}&amounts={amount}&to_addresses={payment_address}&priority={priority}")
    BlockIOResponseWithdrawalToBeSigned withdrawToBeSigned(@PathParam("apikey") String apikey, @PathParam("amount") String amount, @PathParam("payment_address") String payment_address, @PathParam("priority") String priority);

    @GET
    @Path("verify_signature/?signed_data={signed_data}&signature={signature}&public_key={public_key}")
    BlockIOResponseVerify verify(@PathParam("signed_data") String signedDataInHex, @PathParam("signature") String signatureInHex, @PathParam("public_key") String public_keyInHex);


}
