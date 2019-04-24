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
package com.generalbytes.batm.server.extensions.extra.litecoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.extra.groestlcoin.wallets.groestlcoind.GroestlcoindRPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LitecoinAddressValidator implements ICryptoAddressValidator {

    private static final Logger log = LoggerFactory.getLogger(LitecoinAddressValidator.class);

    @Override
    public boolean isAddressValid(String address) {
        if (address.startsWith("L") || address.startsWith("3") || address.startsWith("M")) {
            try {
                Base58.decodeToBigInteger(address);
                Base58.decodeChecked(address);
            } catch (AddressFormatException e) {
                log.debug("Address ["+address+"] is not recognized.", e);
                return false;
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return true;
    }

    @Override
    public boolean mustBeBase58Address() {
        return true;
    }
}
