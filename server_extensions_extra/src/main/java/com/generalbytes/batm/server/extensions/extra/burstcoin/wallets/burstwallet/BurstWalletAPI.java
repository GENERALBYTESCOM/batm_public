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

package com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet;

import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.AccountResponse;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.BurstTransactionBroadcastResponse;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.BurstTransactionBytesResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("/burst")
@Produces(MediaType.APPLICATION_JSON)
public interface BurstWalletAPI {
    @POST
    AccountResponse getAccount(@QueryParam("account") String accountId, @DefaultValue("getAccount") @QueryParam("requestType") String requestType);

    @POST
    BurstTransactionBytesResponse sendMoney(@QueryParam("recipient") String recipient, @QueryParam("publicKey") String publicKey, @QueryParam("amountNQT") BigDecimal amountNQT, @QueryParam("feeNQT") BigDecimal feeNQT, @QueryParam("deadline") long deadline, @QueryParam("requestType") String requestType, @QueryParam("broadcast") boolean broadcast);

    @POST
    BurstTransactionBroadcastResponse broadcastTransaction(@QueryParam("transactionBytes") String transactionBytes, @QueryParam("requestType") String requestType);
}
