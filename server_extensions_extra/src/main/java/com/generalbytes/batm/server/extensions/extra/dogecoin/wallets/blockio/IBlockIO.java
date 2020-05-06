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
package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Path("/api/v2/")
@Produces(MediaType.APPLICATION_JSON)
public interface IBlockIO {

    String PRIORITY_LOW = "low";
    String PRIORITY_MEDIUM = "medium";
    String PRIORITY_HIGH = "high";

    @GET
    @Path("get_my_addresses")
    BlockIOResponseAddresses getAddresses() throws IOException;

    @GET
    @Path("get_new_address")
    BlockIOResponseNewAddress getNewAddress(@QueryParam("label") String label) throws IOException;

    @GET
    @Path("get_balance")
    BlockIOResponseBalance getBalance() throws IOException;

    @GET
    @Path("withdraw")
    BlockIOResponseWithdrawal withdraw(@QueryParam("pin") String pin, @QueryParam("amount") String amount, @QueryParam("payment_address") String payment_address, @QueryParam("priority") String priority) throws IOException;

    @POST
    @Path("sign_and_finalize_withdrawal")
    BlockIOResponseWithdrawal signAndFinalizeWithdrawal(@FormParam("signature_data") String signedDataInJson) throws IOException;

    @GET
    @Path("withdraw")
    BlockIOResponseWithdrawalToBeSigned withdrawToAddressesToBeSigned(@QueryParam("amounts") List<BigDecimal> amounts, @QueryParam("to_addresses") List<String> toAddresses, @QueryParam("priority") String priority) throws IOException;

    @GET
    @Path("withdraw")
    BlockIOResponseWithdrawalToBeSigned withdrawToBeSigned(@QueryParam("amount") String amount, @QueryParam("payment_address") String payment_address, @QueryParam("priority") String priority) throws IOException;

}
