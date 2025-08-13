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

package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/api/v3")
@Produces(MediaType.APPLICATION_JSON)
public interface IBitcoinPay {
    @POST
    @Path("/invoices")
    @Consumes(MediaType.APPLICATION_JSON)
    BitcoinPayPaymentResponseDTO createNewPaymentRequest(@HeaderParam("Authorization") String token, BitcoinPayPaymentRequestRequestDTO request) throws IOException;


    @GET
    @Path("/invoices/{payment_id}")
    BitcoinPayPaymentResponseDTO getPaymentStatus(@HeaderParam("Authorization") String token, @PathParam("payment_id") String paymentId) throws IOException;
}
