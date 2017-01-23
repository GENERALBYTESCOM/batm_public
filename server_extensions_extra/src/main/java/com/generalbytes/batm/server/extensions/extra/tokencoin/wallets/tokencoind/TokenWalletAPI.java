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

package com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind;

import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind.dto.AccountResponse;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind.dto.TokenAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind.dto.TokenSendResponse;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind.dto.SendResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public interface TokenWalletAPI {
    @GET
    @Path("/Accounts")
    public TokenAccountsResponse getAllAccounts();

    @GET
    @Path("/Accounts/{id}")
    public AccountResponse getBalance( @PathParam("id") String accountId);

    @POST
    @Path("/send")
    public TokenSendResponse send(@FormParam("email") String email, @FormParam("password") String password, @FormParam("masterPassword") String masterPassword, @FormParam("accountId") String accountId, @FormParam("recipient") String recipient, @QueryParam("amount") BigDecimal amountNQT, @FormParam("message") String message);

    @POST
    @Path("/Accounts/{id}/{recipient}/{amount}/{message}/send")
    public SendResponse send2( @PathParam("id") String accountId, @PathParam("recipient") String recipient, @PathParam("amount") BigDecimal amountNQT, @PathParam("message") String requestType);
}
