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
package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.coinutil.Bech32;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class BitcoinCashAddressValidator implements ICryptoAddressValidator {
    @Override
    public boolean isAddressValid(String address) {
        if (address == null) {
            return false;
        } else {
            address = address.trim();
            if (address.startsWith("xpub")) {
                return false;
            } else if (!address.startsWith("p") && !address.startsWith("q")) {
                return false;
            } else {
                try {
                    byte[] checksumData = Bech32.concatenateByteArrays(Bech32.concatenateByteArrays(Bech32.getPrefixBytes("bitcoincash"), new byte[]{0}), Bech32.decode(address));
                    byte[] calculateChecksumBytesPolymod = Bech32.calculateChecksumBytesPolymod(checksumData);
                    return Bech32.bytes2Long(calculateChecksumBytesPolymod) == 0L;
                } catch (RuntimeException var3) {
                    return false;
                }
            }
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
