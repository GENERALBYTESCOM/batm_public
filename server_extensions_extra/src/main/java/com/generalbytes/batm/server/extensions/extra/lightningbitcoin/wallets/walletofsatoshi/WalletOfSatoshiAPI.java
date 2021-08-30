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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi;

import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.Account;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.Balances;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.InvoiceRequest;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.Payment;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.PaymentRequest;
import org.knowm.xchange.utils.nonce.CurrentTimeIncrementalNonceFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/api/v1/wallet")
@Produces(MediaType.APPLICATION_JSON)
public interface WalletOfSatoshiAPI {
    static CurrentTimeIncrementalNonceFactory nonceFactory = new CurrentTimeIncrementalNonceFactory(TimeUnit.MILLISECONDS);

    static WalletOfSatoshiAPI create(String apiToken, String apiSecret) throws InvalidKeyException, NoSuchAlgorithmException {
        final ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, "api-token", apiToken);
        config.addDefaultParam(HeaderParam.class, "nonce", nonceFactory);
        config.addDefaultParam(HeaderParam.class, "signature", new WalletOfSatoshiDigest(apiToken, apiSecret));
        return RestProxyFactory.createProxy(WalletOfSatoshiAPI.class, "https://www.livingroomofsatoshi.com", config);
    }


    @GET
    @Path("/balance")
    Balances getBalances() throws IOException;


    @GET
    @Path("/account")
    Account getAccount() throws IOException;

    @POST
    @Path("/createInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    Invoice createInvoice(InvoiceRequest request) throws IOException;


    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    Payment createPayment(PaymentRequest request) throws IOException;

    @GET
    @Path("/payments")
    List<Payment> getPayments(@QueryParam("limit") Integer limit, @QueryParam("skip") Integer skip) throws IOException;

    @GET
    @Path("/payment/{id}")
    Payment getPaymentById(@PathParam("id") String id) throws IOException;

    @GET
    @Path("/payment/description/{description}")
    List<Payment> getPaymentsByDescription(@PathParam("description") String description) throws IOException;


}
