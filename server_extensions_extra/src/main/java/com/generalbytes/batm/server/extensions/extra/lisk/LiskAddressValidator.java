/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.lisk;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class LiskAddressValidator implements ICryptoAddressValidator {
    @Override
    public boolean isAddressValid(String address) {
        if (address.endsWith("L")) {
            String addressWithoutL = address.substring(0, address.length() - 1);
            for (char addressCharacter : addressWithoutL.toCharArray()) {
                if (!Character.isDigit(addressCharacter)) { 
                    return false;
                }
             } 
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return true;
    }
}
