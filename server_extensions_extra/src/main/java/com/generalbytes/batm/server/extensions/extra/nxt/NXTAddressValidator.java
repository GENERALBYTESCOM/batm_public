/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.nxt;

import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

import java.math.BigInteger;

public class NXTAddressValidator implements ICryptoAddressValidator {
    @Override
    public boolean isAddressValid(String address) {
        return isNXTAddressValid(address);
    }

    public static BigInteger getAccountIdFromRS(String address) {
        if (address.startsWith("NXT")) {
            try {
                String[] pieces = address.split("-");
                if (pieces.length != 5) {
                    return null;
                }
                if (pieces[0].length() != 3 ||
                        pieces[1].length() != 4 ||
                        pieces[2].length() != 4 ||
                        pieces[3].length() != 4 ||
                        pieces[4].length() != 5 ) {
                    return null;
                }

                BigInteger l = rsDecode(pieces[1] + "-" + pieces[2] + "-" + pieces[3] + "-" + pieces[4]);
                return l;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    private boolean isNXTAddressValid(String address) {
        return getAccountIdFromRS(address) != null;
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }

    private static BigInteger rsDecode(String rsString) {
        rsString = rsString.toUpperCase();
        try {
            BigInteger id = ReedSolomon.decode(rsString);
            if (! rsString.equals(ReedSolomon.encode(id))) {
                throw new RuntimeException("ERROR: Reed-Solomon decoding of " + rsString + " not reversible, decoded to " + id);
            }
            return id;
        } catch (ReedSolomon.DecodeException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }
}
