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
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.coinofsale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;


@Path("/payment/api")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinOfSaleAPI {

    @GET
    @Path("/uri")
    public CoSPaymentResponseDTO createPayment(@QueryParam("token") String token, @QueryParam("pin") String pin, @QueryParam("price") BigDecimal fiatPrice, @QueryParam("fiat_currency") String fiatCurrency);

    @GET
    @Path("/status")
    public CoSStatusResponseDTO getPaymentStatus(@QueryParam("address") String address, @QueryParam("token") String token, @QueryParam("determined") boolean determined);
}
