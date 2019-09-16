/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd;

import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Info;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Payment;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.PaymentRequest;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.SendPaymentResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/v1/")
@Produces(MediaType.APPLICATION_JSON)
public interface LndAPI {

    /**
     * @return general information concerning the lightning node including it’s identity pubkey, alias,
     * the chains it is connected to, and information concerning the number of open+pending channels.
     * @throws IOException
     */
    @GET
    @Path("/getinfo")
    Info getInfo() throws IOException, ErrorResponseException;

    /**
     * @return the total funds available across all open channels in satoshis
     * @throws IOException
     * @throws ErrorResponseException
     */
    @GET
    @Path("/balance/channels")
    Balance getBalance() throws IOException, ErrorResponseException;

    /**
     * attempts to add a new invoice to the invoice database
     *
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/invoices")
    @Consumes(MediaType.APPLICATION_JSON)
    Invoice addInvoice(Invoice invoice) throws IOException, ErrorResponseException;

    @GET
    @Path("/invoice/{r_hash_str}")
    Invoice getInvoice(@PathParam("r_hash_str") String paymentHash) throws IOException, ErrorResponseException;

    @POST
    @Path("/channels/transactions")
    @Consumes(MediaType.APPLICATION_JSON)
    SendPaymentResponse sendPayment(Payment payment) throws IOException, ErrorResponseException;

    /**
     * DecodePayReq takes an encoded payment request string and attempts to decode it, returning a full description of the conditions encoded within the payment request.
     */
    @GET
    @Path("/payreq/{pay_req}")
    PaymentRequest decodePaymentRequest(@PathParam("pay_req") String paymentRequest) throws IOException, ErrorResponseException;


}
