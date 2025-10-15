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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd;

import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Balance;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Channel;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Channels;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Graph;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Info;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Payment;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.PaymentRequest;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.RouteResponse;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.SendPaymentResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

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

    /**
     * @return a description of all the open channels that this node is a participant in.
     * @throws IOException
     * @throws ErrorResponseException
     */
    @GET
    @Path("/channels")
    Channels getChannels() throws IOException, ErrorResponseException;

    /**
     * @return a description of the latest graph state from the point of view of the node.
     * The graph information is partitioned into two components: all the nodes/vertexes,
     * and all the edges that connect the vertexes themselves. As this is a directed graph,
     * the edges also contain the node directional specific routing policy which includes:
     * the time lock delta, fee information, etc.
     * @throws IOException
     * @throws ErrorResponseException
     */
    @GET
    @Path("/graph")
    Graph getGraph() throws IOException, ErrorResponseException;

    /**
     * attempts to query the daemon's Channel Router for a possible route to a target destination capable of carrying a specific amount of satoshis. The returned route contains the full details required to craft and send an HTLC, also including the necessary information that should be present within the Sphinx packet encapsulated within the HTLC.
     *
     * @param pubKey         The 33-byte hex-encoded public key for the payment destination
     * @param amountSatoshis The amount to send expressed in satoshis
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @GET
    @Path("/graph/routes/{pub_key}/{amt}")
    RouteResponse getRoute(@PathParam("pub_key") String pubKey, @PathParam("amt") long amountSatoshis) throws IOException, ErrorResponseException;

}
