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
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.util.ExtensionParameters;

public class OnfidoExtension extends AbstractExtension {
    @Override
    public String getName() {
        return "BATM Onfido extra extension";
    }

    @Override
    public IIdentityVerificationProvider createIdentityVerificationProvider(String colonDelimitedParameters) {
        ExtensionParameters params = ExtensionParameters.fromDelimited(colonDelimitedParameters);
        if ("onfido".equals(params.get(0))) {
            String apiKey = params.get(1);
            String verificationSiteUrl = params.get(2);
            String region = params.get(3);
            return null;// TODO new Onfido();
        }
        return null;

    }
}
