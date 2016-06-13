/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt;

import com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto.AccountResponse;
import com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto.MynxtAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto.MynxtSendResponse;
import com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto.SendResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("/api/0.1")
@Produces(MediaType.APPLICATION_JSON)
public interface IMynxtWalletAPI {
    @GET
    @Path("/user/account")
    public MynxtAccountsResponse getAllAccounts(@QueryParam("email") String email, @QueryParam("password") String password);

    @GET
    @Path("/nxt")
    public AccountResponse getAccount(@QueryParam("email") String email, @QueryParam("password") String password, @QueryParam("account") String accountId, @DefaultValue("getAccount") @QueryParam("requestType") String requestType);

    @POST
    @Path("/send")
    public MynxtSendResponse send(@FormParam("email") String email, @FormParam("password") String password, @FormParam("masterPassword") String masterPassword, @FormParam("accountId") String accountId, @FormParam("recipient") String recipient, @FormParam("amountNQT") BigDecimal amountNQT, @FormParam("message") String message);

    @POST
    @Path("/nxt")
    public SendResponse send2(@QueryParam("email") String email, @QueryParam("password") String password, @QueryParam("masterPassword") String masterPassword, @QueryParam("accountId") String accountId, @QueryParam("recipient") String recipient, @QueryParam("amountNQT") BigDecimal amountNQT,@QueryParam("feeNQT") BigDecimal feeNQT, @QueryParam("deadline") long deadline, @QueryParam("requestType") String requestType);
}
