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
package com.generalbytes.batm.server.extensions.extra.examples.rest;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ITransactionCashbackInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * REST service implementation class that uses JSR-000311 JAX-RS
 */
@Path("/")
public class RESTServiceExample {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.RESTServiceExample");
    @GET
    @Path("/helloworld")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns JSON response on following URL https://localhost:7743/extensions/example/helloworld
     */
    public Object helloWorld(@Context HttpServletRequest request, @Context HttpServletResponse response, @QueryParam("serial_number") String serialNumber) {
        String serverVersion = RESTExampleExtension.getExtensionContext().getServerVersion();
        return new MyExtensionExampleResponse(0, "Server version is: " + serverVersion);
    }

    @GET
    @Path("/terminals")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns list of terminals and their locations plus other information. https://localhost:7743/extensions/example/terminals
     */
    public Object terminals() {
        try {
            return RESTExampleExtension.getExtensionContext().findAllTerminals();
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return "ERROR";
    }

    @GET
    @Path("/cashboxes")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns terminal cashboxes contains https://localhost:7743/extensions/example/cashboxes?serial_number=BT300045
     */
    public Object cashboxes(@QueryParam("serial_number") String serialNumber) {
        if (serialNumber == null) {
            return "You need to specify serial_number parameter";
        }
        try {
            return RESTExampleExtension.getExtensionContext().getCashBoxes(serialNumber);
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return "ERROR";
    }

    @GET
    @Path("/terminals_with_available_cash")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns list of terminals that have specified cash available for sell transactions. https://localhost:7743/extensions/example/terminals_with_available_cash?amount=100&fiat_currency=USD
     */
    public Object terminalsWithAvailableCash( @QueryParam("amount") String amount, @QueryParam("fiat_currency") String fiatCurrency) {
        if (amount == null || fiatCurrency == null) {
            return "amount and fiat_currency has to be set.";
        }
        try {
            return RESTExampleExtension.getExtensionContext().findTerminalsWithAvailableCashForSell(new BigDecimal(amount), fiatCurrency,null);
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return "ERROR";
    }

    @GET
    @Path("/calculate_crypto_amount")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns crypto amount for specified fiat amount using crypto settings of specified terminal
     * https://localhost:7743/extensions/builtin/calculate_crypto_amount?serial_number=BT300511&fiat_currency=USD&crypto_currency=LTC&fiat_amount=100
     */
    public Object calculateCryptoAmount(@QueryParam("serial_number") String serialNumber, @QueryParam("crypto_currency") String cryptoCurrency, @QueryParam("fiat_amount") String fiatAmount , @QueryParam("fiat_currency") String fiatCurrency) {
        if (serialNumber == null || fiatCurrency == null || cryptoCurrency == null || fiatAmount == null) {
            return "missing some parameters";
        }
        try {
            return RESTExampleExtension.getExtensionContext().calculateCryptoAmounts(serialNumber, Arrays.<String>asList(new String[]{cryptoCurrency}), new BigDecimal(fiatAmount), fiatCurrency, IExtensionContext.DIRECTION_BUY_CRYPTO,null,null);
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return "ERROR";
    }

    @GET
    @Path("/get_exchange_rate_info")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns exchange rates for specified terminal.
     * https://localhost:7743/extensions/builtin/get_exchange_rate_info?serial_number=BT300511
     */
    public Object getExchangeRateInfo(@QueryParam("serial_number") String serialNumber) {
        if (serialNumber == null ) {
            return "missing serial_number parameter";
        }
        try {
            return RESTExampleExtension.getExtensionContext().getExchangeRateInfo(serialNumber, IExtensionContext.DIRECTION_BUY_CRYPTO | IExtensionContext.DIRECTION_SELL_CRYPTO);
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return "ERROR";
    }

    @GET
    @Path("/sell_crypto")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Creates sell transaction.
     */
    public Object sellCrypto(@QueryParam("serial_number") String serialNumber, @QueryParam("fiat_amount") BigDecimal fiatAmount, @QueryParam("fiat_currency") String fiatCurrency, @QueryParam("crypto_amount") BigDecimal cryptoAmount, @QueryParam("crypto_currency") String cryptoCurrency, @QueryParam("identity_public_id") String identityPublicId, @QueryParam("discount_code") String discountCode ) {
        if (serialNumber == null || fiatAmount == null || fiatCurrency == null || cryptoAmount == null || cryptoCurrency == null) {
            return "missing parameters";
        }
        try {
            return RESTExampleExtension.getExtensionContext().sellCrypto(serialNumber, fiatAmount, fiatCurrency, cryptoAmount, cryptoCurrency, identityPublicId, discountCode);
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return "ERROR";
    }

    @GET
    @Path("/cashback")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Creates a cashback transaction on a given terminal. After this call you can visit terminal and withdraw cash
     */
    public Object cashback(@QueryParam("serial_number") String serialNumber, @QueryParam("fiat_amount") BigDecimal fiatAmount, @QueryParam("fiat_currency") String fiatCurrency, @QueryParam("identity_public_id") String identityPublicId) {
        if (serialNumber == null || fiatAmount == null || fiatCurrency == null) {
            return "missing parameters";
        }
        if (!new File("cashback.example").exists()) {
            return "For security reasons you need to create file cashback.example in master service working directory and change the code in this example. In order to have this example working.";
        }
        if (true) { // For security reasons you need to change this line to make it this work - You have to know what you are doing. By removing this line you understand that the risk.
            return "For security reasons you need to modify the code to get further.";
        }
        try {
            ITransactionCashbackInfo cashback = RESTExampleExtension.getExtensionContext().cashback(serialNumber, fiatAmount, fiatCurrency, identityPublicId);
            cashback.getCustomData().put("qrcode","cashback:jackpot?amount=" + cashback.getCashAmount().toPlainString() +"&" + "label=" + cashback.getRemoteTransactionId() + "&uuid=" + cashback.getTransactionUUID());
            return cashback;
        } catch (Throwable e) {
            log.error("Error", e);
        }
        return "ERROR";
    }

}
