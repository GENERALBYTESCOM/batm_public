/*
 *
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
 */
package com.generalbytes.batm.server.extensions.extra.nuls;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

/**
 * @author naveen
 */
public class NulsAddressValidator implements ICryptoAddressValidator {

    private static final int ADDRESS_LENGTH = 32;

    @Override
    public boolean isAddressValid(String address) {
        return validAddress(address);
    }

    @Override
    public boolean mustBeBase58Address() {
        return true;
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    private boolean validAddress(String address) {
        if (address.isEmpty()) {
            return false;
        }
        if(!address.toLowerCase().startsWith("ns")){
            return false;
        }
        if(address.length() != ADDRESS_LENGTH){
            return false;
        }
        return true;
    }
}
