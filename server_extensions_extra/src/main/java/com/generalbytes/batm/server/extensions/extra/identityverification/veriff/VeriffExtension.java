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
package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.util.ExtensionParameters;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class VeriffExtension extends AbstractExtension {
    private static IExtensionContext ctx = null;
    private Set<IRestService> restServices = null;

    @Override
    public String getName() {
        return "BATM Veriff extra extension";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        VeriffExtension.ctx = ctx;
        this.restServices = getServices(ctx);
    }

    private Set<IRestService> getServices(IExtensionContext ctx) {
        Set<IRestService> services = new HashSet<>();
        services.add(new VeriffRestService());
        if (ctx.isGlobalServer()) {
            services.add(new GlobalVeriffRestService());
        }
        return services;
    }

    public static IExtensionContext getExtensionContext() {
        return Objects.requireNonNull(ctx, "ctx is null, extension not initialized yet");
    }

    @Override
    public Set<IRestService> getRestServices() {
        if (restServices == null) {
            throw new IllegalStateException("Extension not initialized yet");
        }
        return restServices;
    }

    @Override
    public IIdentityVerificationProvider createIdentityVerificationProvider(String colonDelimitedParameters, String gbApiKey) {
        ExtensionParameters params = ExtensionParameters.fromDelimited(colonDelimitedParameters);
        if ("veriff".equals(params.getPrefix())) {
            String publicKey = params.get(1);
            String privateKey = params.get(2);
            return new VeriffIdentityVerificationProvider(publicKey, privateKey);

        } else if ("gbcloud_local".equals(params.getPrefix())) {
            // internal provider, not available from the extensions XML, could not be configured to be used directly.
            // used by the "GB Cloud" provider when running locally on the global server (cloud)
            return VeriffIdentityVerificationProvider.getForGlobalServer();
        }

        return null;
    }
}
