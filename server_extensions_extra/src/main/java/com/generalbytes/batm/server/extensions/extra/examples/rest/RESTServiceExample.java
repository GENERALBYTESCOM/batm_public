/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
    class MyExtensionResponse {
        int resultCode;
        String message;

        public MyExtensionResponse(int resultCode, String message) {
            this.resultCode = resultCode;
            this.message = message;
        }
    }


    @GET
    @Path("/helloworld")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Returns JSON response on following URL https://localhost:7743/extensions/example/helloworld
     */
    public Object helloWorld(@Context HttpServletRequest request, @Context HttpServletResponse response, @QueryParam("serial_number") String serialNumber) {
        String serverVersion = RESTExampleExtension.getExtensionContext().getServerVersion();
        return new MyExtensionResponse(0, "Server version is: " + serverVersion);
    }
}
