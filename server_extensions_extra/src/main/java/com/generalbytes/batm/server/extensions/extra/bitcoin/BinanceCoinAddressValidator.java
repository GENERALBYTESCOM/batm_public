/*************************************************************************************
 * Copyright (C) 2014-2024 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Bech32;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinanceCoinAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.BinanceCoinAddressValidator");

    @Override
    public boolean isAddressValid(String address) {
        try {
            address = getAddressWithoutTag(address);
            // BNB Smart Chain is EVM-compatible and uses Ethereum-style addresses.
            // Address format: 20 bytes represented as 40 hexadecimal characters prefixed with "0x".
            // https://docs.bnbchain.org/docs/learn/intro/
            // https://ethereum.org/en/developers/docs/accounts/
            if (address.startsWith("0x") && address.length() == 42) {
                return address.matches("^0x[0-9a-fA-F]{40}$");
            }

            // BNB Beacon Chain Bech32
            Bech32.Bech32Data bech32Data = Bech32.decodeUnlimitedLength(address);
            if (!bech32Data.hrp.equals("bnb")) {
                log.info("Address HRP is not 'bnb'");
                return false;
            }
            return true;
        } catch (AddressFormatException e) {
            log.info("Invalid BNB address format.");
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

    private String getAddressWithoutTag(String address) {
        return address.split(":")[0];
    }

}
