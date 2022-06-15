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
package com.generalbytes.batm.server.extensions.extra.cardano;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.coinutil.Bech32;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardanoAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.CardanoAddressValidator");

    @Override
    public boolean isAddressValid(String address) {
        try {
            if (address.startsWith("addr1")) {
                Bech32.Bech32Data bech32Data = Bech32.decodeUnlimitedLength(address);
                if (!bech32Data.hrp.equals("addr")) {
                    log.info("Address HRP is not 'addr'");
                    return false;
                }
                return true;
            } else if (address.startsWith("Ae2") || address.startsWith("DdzFF")) {
                Base58.decode(address);
                return true;
            }
            return false;
        } catch (AddressFormatException e) {
            log.error("Cannot decode address", e);
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
