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
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.util.ExtensionParameters;

public class VeriffExtension extends AbstractExtension {
    @Override
    public String getName() {
        return "BATM Veriff extra extension";
    }

    @Override
    public IIdentityVerificationProvider createIdentityVerificationProvider(String colonDelimitedParameters) {
        ExtensionParameters params = ExtensionParameters.fromDelimited(colonDelimitedParameters);
        if ("veriff".equals(params.get(0))) {
            String publicKey = params.get(1);
            String privateKey = params.get(2);
            return null;// TODO new Veriff(publicKey, privateKey);
        }
        return null;

    }
}
