/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface IBitcoinPay {

    @POST
    @Path("/payment/btc")
    @Consumes(MediaType.APPLICATION_JSON)
    BitcoinPayPaymentResponseDTO createNewPaymentRequest(@HeaderParam("Authorization") String token, BitcoinPayPaymentRequestRequestDTO request);


    @GET
    @Path("/payment/btc/{payment_id}")
    BitcoinPayPaymentResponseDTO getPaymentStatus(@HeaderParam("Authorization") String token, @PathParam("payment_id") String paymentId);



}
