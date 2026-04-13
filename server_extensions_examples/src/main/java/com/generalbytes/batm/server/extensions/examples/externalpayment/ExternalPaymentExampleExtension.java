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

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.payment.external.IExternalPaymentProvider;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Example extension demonstrating how to implement an external payment provider.
 * <p>
 * This extension provides:
 * <ul>
 *     <li>A mock {@link IExternalPaymentProvider} that logs payment requests and returns a test payment link</li>
 *     <li>A REST endpoint for updating external payment status</li>
 * </ul>
 * <p>
 * For security reasons this extension is not enabled by default.
 * You need to enable it in batm-extensions.xml.
 */
public class ExternalPaymentExampleExtension extends AbstractExtension {

    @Getter
    private static IExtensionContext ctx;

    @Override
    public String getName() {
        return "BATM Example extension that demonstrates how to implement an external payment provider";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        ExternalPaymentExampleExtension.ctx = ctx;
    }

    @Override
    public IExternalPaymentProvider createExternalPaymentProvider() {
        return new MockExternalPaymentProvider();
    }

    @Override
    public Set<IRestService> getRestServices() {
        HashSet<IRestService> services = new HashSet<>();
        services.add(new IRestService() {
            @Override
            public String getPrefixPath() {
                return "external-payment";
            }

            @Override
            public Class getImplementation() {
                return ExternalPaymentController.class;
            }
        });
        return services;
    }
}
