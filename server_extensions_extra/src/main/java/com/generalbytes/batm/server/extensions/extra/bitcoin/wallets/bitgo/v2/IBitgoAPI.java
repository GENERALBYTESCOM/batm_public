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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCoinRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.ErrorResponseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

@Path("/api/v2")
@Produces(MediaType.APPLICATION_JSON)
public interface IBitgoAPI {

    @POST
    @Path("/{coin}/wallet/{id}/sendcoins")
    @Consumes(MediaType.APPLICATION_JSON)
    Map<String, Object> sendCoins(@PathParam("coin") String coin, @PathParam("id") String id, BitGoCoinRequest request) throws IOException;

    @GET
    @Path("/{coin}/wallet/balances")
    Map<String, Object> getTotalBalances(@PathParam("coin") String coin) throws IOException;

    @GET
    @Path("/{coin}/wallet")
    Map<String, Object> getWallets(@PathParam("coin") String coin) throws IOException;

    @GET
    @Path("/{coin}/wallet/{id}")
    Map<String, Object> getWalletById(@PathParam("coin") String coin, @PathParam("id") String id) throws IOException;

    @GET
    @Path("/{coin}/wallet/{walletid}/address/{id}")
    Map<String, Object> getWalletAddressById(@PathParam("coin") String coin, @PathParam("walletid") String walletId, @PathParam("id") String id) throws IOException;

    @POST
    @Path("/{coin}/wallet/{id}/address")
    @Consumes(MediaType.APPLICATION_JSON)
    BitGoCreateAddressResponse createAddress(@PathParam("coin") String coin, @PathParam("id") String id, BitGoCreateAddressRequest request) throws IOException, ErrorResponseException;
}
