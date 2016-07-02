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

package com.generalbytes.batm.server.extensions;

/**
 * Classes that implement this interface are used by server to validate user crypto address entry
 */
public interface ICryptoAddressValidator {
    /**
     * This method checks if provided address is valid for specified cryptocurrency
     * @param address
     * @return
     */
    public boolean isAddressValid(String address);

    /**
     * returns true if cryproaddress of this cryptocurrency must be BASE58 checked when read by QR code scanner.
     * @return
     */
    public boolean mustBeBase58Address();

    /**
     * returns true if cryptoaddress can be email address later used for issuing paper wallets
     * Please note that Paper wallet for specified cryptocurrency must exist
     * @return
     */
    public boolean isPaperWalletSupported();

}
