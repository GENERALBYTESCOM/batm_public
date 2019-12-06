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
package com.generalbytes.batm.server.extensions.extra.examples.rest;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class demonstrates how to write a simple REST service that will respond to HTTPS calls.
 * For exact URLs @see {@link RESTServiceExample}
 * For security reasons this extension is not enabled by default.
 * You need to enable it in batm-extensions.xml
 */
public class RESTExampleExtension extends AbstractExtension{
    private static IExtensionContext ctx;

    @Override
    public String getName() {
        return "BATM Example extension that demonstrates how to write RESFul services";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.ctx = ctx;
    }

    @Override
    public Set<IRestService> getRestServices() {
        HashSet<IRestService> services = new HashSet<>();
        //This is simplest implementation of rest service
        services.add(new IRestService() {
            @Override
            public String getPrefixPath() {
                return "example";
            }

            @Override
            public Class getImplementation() {
                return RESTServiceExample.class;
            }

            @Override
            public List<Class> getFilters() {
                return Arrays.asList(ServletFilterExample.class);
            }
        });

        services.add(new IRestService() {
            @Override
            public String getPrefixPath() {
                return "secured";
            }

            @Override
            public Class getImplementation() {
                return SecuredRESTServiceExample.class;
            }
        });
        return services;
    }

    public static IExtensionContext getExtensionContext() {
        return ctx;
    }
}
