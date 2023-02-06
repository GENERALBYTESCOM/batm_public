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
package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.util.ExtensionParameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OnfidoExtension extends AbstractExtension {
    private static IExtensionContext ctx = null;
    private final Set<IRestService> restServices = new HashSet<>(Arrays.asList(
        new OnfidoSubmitRestService(),
        new OnfidoWebhookRestService()));

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        OnfidoExtension.ctx = ctx;
    }

    public static IExtensionContext getExtensionContext() {
        return Objects.requireNonNull(ctx, "ctx is null, extension not initialized yet");
    }

    @Override
    public String getName() {
        return "BATM Onfido extra extension";
    }

    @Override
    public IIdentityVerificationProvider createIdentityVerificationProvider(String colonDelimitedParameters, String gbApiKey) {
        ExtensionParameters params = ExtensionParameters.fromDelimited(colonDelimitedParameters);
        if ("onfido".equals(params.getPrefix())) {
            String apiKey = params.get(1);
            String verificationSiteUrl = params.get(2);
            OnfidoRegion region = params.get(3, OnfidoRegion.EU);
            return new OnfidoIdentityVerificationProvider(apiKey, verificationSiteUrl, region, ctx);
        }
        return null;
    }

    @Override
    public Set<IRestService> getRestServices() {
        return restServices;
    }

}
