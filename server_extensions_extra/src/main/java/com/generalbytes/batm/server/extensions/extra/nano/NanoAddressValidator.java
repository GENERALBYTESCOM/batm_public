/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class NanoAddressValidator implements ICryptoAddressValidator {

    private final NanoCurrencySpecification spec;

    public NanoAddressValidator(NanoCurrencySpecification spec) {
        this.spec = spec;
    }


    @Override
    public boolean isAddressValid(String address) {
        if (address == null) return false;
        try {
            return spec.isPrefixValid(spec.parseAddressRaw(address));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return true;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }

}