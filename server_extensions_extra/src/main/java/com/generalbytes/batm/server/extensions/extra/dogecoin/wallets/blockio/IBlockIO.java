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

import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIORequestSubmitTransaction;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseAddresses;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseBalance;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseNewAddress;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponsePrepareTransaction;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseSubmitTransaction;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
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
    @Path("get_address_balance")
    BlockIOResponseBalance getAddressBalance(@QueryParam("labels") List<String> labels) throws IOException;

    @GET
    @Path("prepare_transaction")
    BlockIOResponsePrepareTransaction prepareTransaction(
        @QueryParam("from_labels") List<String> fromLabels,
        @QueryParam("amounts") List<BigDecimal> amounts,
        @QueryParam("to_addresses") List<String> toAddresses,
        @QueryParam("priority") String priority
    );

    @POST
    @Path("submit_transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    BlockIOResponseSubmitTransaction submitTransaction(BlockIORequestSubmitTransaction request);

}
