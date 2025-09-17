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
package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.coinutil.Bech32;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiquidBitcoinAddressValidator implements ICryptoAddressValidator {

    private static final Logger log = LoggerFactory.getLogger(LiquidBitcoinAddressValidator.class);

    @Override
    public boolean isAddressValid(String address) {
        /*

Prefix Example	Confidential?	Encoding	Address Type	Network
V…	                ✅ Yes	    Base58	Confidential (P2PKH/P2SH with blinding key)	Mainnet
CT…	                ✅ Yes	    Bech32	Confidential SegWit	Mainnet
lq1…                ❌ No        Bech32  Unconfidential SegWit (like Bitcoin bc1…) Mainnet
H…	                ❌ No	    Base58	Unconfidential P2PKH (like Bitcoin 1…)	Mainnet
X…	                ❌ No	    Base58	Unconfidential P2SH (like Bitcoin 3…)	Mainnet
Q…	                ❌ No	    Bech32	Unconfidential SegWit (like Bitcoin bc1…)	Mainnet

tlq…	            ✅ Yes	    Bech32	Confidential SegWit	Testnet
ERT… / tex…         ❌ No	    Bech32	Unconfidential SegWit	Testnet / Regtest
CTE… / ERT…	        ✅ Yes	    Base58	Confidential	Testnet / Regtest

         */
        if (address == null || address.trim().isEmpty()) {
            return false;
        }

        if (address.toLowerCase().startsWith("lq1") || address.toLowerCase().startsWith("ct") || address.toLowerCase().startsWith("q") ) {
            try {
                Bech32.decode(address);
                return true;
            } catch (Exception e) {
                log.debug("Liquid address [" + address + "] is not recognized.", e);
                return false;
            }
        }

        if (address.startsWith("H") || address.startsWith("X") || address.startsWith("V")) {
            try {
                Base58.decodeToBigInteger(address);
                Base58.decodeChecked(address);
                return true;
            } catch (AddressFormatException e) {
                log.debug("Liquid legacy address [" + address + "] is not recognized.", e);
                return false;
            }
        }

        return false;
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
