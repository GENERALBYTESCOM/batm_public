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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;

import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Channel;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Info;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.NodeInfo;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ReceivedInfo;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.SentInfo;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface EclairAPI {

    /**
     * @param invoice The invoice you want to decode
     * @return unique identifier for this payment attempt
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/parseinvoice")
    Invoice parseInvoice(@FormParam("invoice") String invoice) throws IOException, ErrorResponseException;

    /**
     * Pays a BOLT11 invoice
     *
     * @param invoice    The invoice you want to pay
     * @param amountMsat Amount in to pay if the invoice does not have one (Millisatoshi)
     * @return unique identifier for this payment attempt
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/payinvoice")
    String payInvoice(@FormParam("invoice") String invoice, @FormParam("amountMsat") Long amountMsat) throws IOException, ErrorResponseException;

    /**
     * @param id The unique id of the payment attempt
     * @return a list containing at most one element.
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/getsentinfo")
    List<SentInfo> getSentInfoById(@FormParam("id") String id) throws IOException, ErrorResponseException;

    /**
     * @param paymentHash The payment hash common to all payment attepts to be retrieved
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/getsentinfo")
    List<SentInfo> getSentInfoByPaymentHash(@FormParam("paymentHash") String paymentHash) throws IOException, ErrorResponseException;

    @POST
    @Path("/getreceivedinfo")
    ReceivedInfo getReceivedInfoByPaymentHash(@FormParam("paymentHash") String paymentHash) throws IOException, ErrorResponseException;

    /**
     * Check whether a payment has been received
     *
     * @param invoice
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/getreceivedinfo")
    ReceivedInfo getReceivedInfoByInvoice(@FormParam("invoice") String invoice) throws IOException, ErrorResponseException;


    /**
     * @return the list of local channels
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/channels")
    List<Channel> getChannels() throws IOException, ErrorResponseException;

    /**
     * Create a BOLT11 payment invoice
     *
     * @param description
     * @param expireIn    Number of seconds that the invoice will be valid
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/createinvoice")
    Invoice createInvoice(@FormParam("amountMsat") Long amountMsat, @FormParam("description") String description, @FormParam("expireIn") Long expireIn) throws IOException, ErrorResponseException;

    /**
     * Returns information about this instance such as nodeId and current block height as seen by eclair.
     *
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/getinfo")
    Info getInfo() throws IOException, ErrorResponseException;

    /**
     * Returns information about all public nodes on the lightning network, this information is taken from the node_announcement network message.
     *
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/allnodes")
    List<NodeInfo> getAllNodes() throws IOException, ErrorResponseException;

    /**
     * Finds a route to the node specified by the invoice
     *
     * @param invoice The invoice containing the destination
     * @param amountMsat The amount that should go through the route
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/findroute")
    List<String> findRoute(@FormParam("invoice") String invoice, @FormParam("amountMsat") Long amountMsat) throws IOException, ErrorResponseException;
}
