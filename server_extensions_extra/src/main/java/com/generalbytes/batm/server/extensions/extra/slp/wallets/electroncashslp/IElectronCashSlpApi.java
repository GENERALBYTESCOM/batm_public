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
package com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp;

import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.BroadcastElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.BroadcastElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.CreateNewAddressElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.CreateNewAddressElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.GetAddressBalanceElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.GetAddressBalanceElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.GetAddressUnspentElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.GetAddressUnspentElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesAndBalancesElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesAndBalancesElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.PayToSlpElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.PayToSlpElectrumResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IElectronCashSlpApi {


    @POST
    @Path("/")
    CreateNewAddressElectrumResponse createNewAddress(CreateNewAddressElectrumRequest request) throws IOException;

    @POST
    @Path("/")
    ListAddressesElectrumResponse listAddresses(ListAddressesElectrumRequest request) throws IOException;

    @POST
    @Path("/")
    ListAddressesAndBalancesElectrumResponse listAddresses(ListAddressesAndBalancesElectrumRequest request) throws IOException;

    @POST
    @Path("/")
    PayToSlpElectrumResponse payToSlp(PayToSlpElectrumRequest request) throws IOException;

    @POST
    @Path("/")
    BroadcastElectrumResponse broadcast(BroadcastElectrumRequest request) throws IOException;

    @POST
    @Path("/")
    GetAddressBalanceElectrumResponse getAddressBalance(GetAddressBalanceElectrumRequest request) throws IOException;

    @POST
    @Path("/")
    GetAddressUnspentElectrumResponse getAddressUnspent(GetAddressUnspentElectrumRequest address) throws IOException;
}
