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
package com.generalbytes.batm.server.extensions.examples.externalpayment;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.exceptions.ExternalPaymentProcessingException;
import com.generalbytes.batm.server.extensions.payment.external.ExternalPaymentUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST controller for updating external payment status.
 * Accessible at /external-payment/update when the extension is enabled.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExternalPaymentController {

    private static final Logger log = LoggerFactory.getLogger(ExternalPaymentController.class);

    @POST
    @Path("/update")
    public ExternalPaymentExampleResponse updateExternalPayment(ExternalPaymentUpdateRequest request) {
        IExtensionContext ctx = ExternalPaymentExampleExtension.getCtx();

        if (ctx == null) {
            log.error("Extension context is not initialized");
            return new ExternalPaymentExampleResponse("Error: Extension context is not initialized");
        }

        if (request == null || request.getExternalPaymentId() == null || request.getStatus() == null) {
            log.error("Invalid request: externalPaymentId and status are required");
            return new ExternalPaymentExampleResponse("Error: externalPaymentId and status are required");
        }

        try {
            ExternalPaymentUpdate update = new ExternalPaymentUpdate();
            update.setExternalPaymentId(request.getExternalPaymentId());
            update.setPaymentStatus(request.getStatus());
            update.setUpdateDetails(request.getUpdateDetails());

            ctx.updateExternalPayment(update);
            log.info("Successfully updated external payment {} with status {}",
                    request.getExternalPaymentId(), request.getStatus());
            return new ExternalPaymentExampleResponse("External payment " + request.getExternalPaymentId()
                    + " updated with status " + request.getStatus());
        } catch (ExternalPaymentProcessingException e) {
            log.error("Failed to update external payment {}: {}", request.getExternalPaymentId(), e.getMessage());
            return new ExternalPaymentExampleResponse("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating external payment {}", request.getExternalPaymentId(), e);
            return new ExternalPaymentExampleResponse("Error: Unexpected error occurred: " + e.getMessage());
        }
    }
}
