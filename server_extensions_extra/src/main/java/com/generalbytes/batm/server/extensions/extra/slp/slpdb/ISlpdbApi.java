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
package com.generalbytes.batm.server.extensions.extra.slp.slpdb;

import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.IncomingTransactionsSlpdbRequest;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.AddressesBalanceSlpdbRequest;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.BalanceSlpdbResponse;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.IncomingTransactionsSlpdbResponse;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.StatusSlpdbRequest;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.StatusSlpdbResponse;
import si.mazi.rescu.RestProxyFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("RestParamTypeInspection") // PathParam required to have single String constructor
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface ISlpdbApi {

    List<String> urls = Arrays.asList(
        "https://slpdb.fountainhead.cash/",
        "https://slpdb.electroncash.de/"
        // see https://status.slpdb.io/
    );

    static List<ISlpdbApi> create() {
        return urls.stream().map(url -> RestProxyFactory.createProxy(ISlpdbApi.class, url)).collect(Collectors.toList());
    }

    /**
     * @return total (sum) balance for one token ID and multiple addresses
     */
    @GET
    @Path("/q/{encodedquery}")
    BalanceSlpdbResponse getBalance(@PathParam("encodedquery") AddressesBalanceSlpdbRequest request) throws IOException;

    /**
     *
     * @param request
     * @return list of incoming transactions to the address sending the given token
     * @throws IOException
     */
    @GET
    @Path("/q/{encodedquery}")
    IncomingTransactionsSlpdbResponse getIncoimngTransactions(@PathParam("encodedquery") IncomingTransactionsSlpdbRequest request) throws IOException;

    /**
     *
     * @param request
     * @return status info
     * @throws IOException
     */
    @GET
    @Path("/q/{encodedquery}")
    StatusSlpdbResponse getStatus(@PathParam("encodedquery") StatusSlpdbRequest request) throws IOException;

}
