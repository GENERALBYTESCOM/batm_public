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
package com.generalbytes.batm.server.extensions.extra.groestlcoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.extensions.extra.groestlcoin.fr.cryptohash.Base58Groestl;
import com.generalbytes.batm.server.extensions.extra.litecoin.Bech32;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroestlcoinAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.GroestlcoinAddressValidator");

    @Override
    public boolean isAddressValid(String address) {
        if (address.startsWith("F") || address.startsWith("3")) {
            try {
                Base58Groestl.decodeToBigInteger(address);
                Base58Groestl.decodeChecked(address);
                return true;
            } catch (AddressFormatException e) {
                log.debug("Address [" + address + "] is not recognized.", e);
                return false;
            }
        } else if (address.toLowerCase().startsWith("grs1")) {
            try {
                Bech32.decode(address);
                return true;
            } catch (Exception e) {
                log.debug("Address [" + address + "] is not recognized.", e);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }

}
