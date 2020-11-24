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

package com.generalbytes.batm.server.extensions;

/**
 * Classes implementing this interface are used by the server to generate paper wallets
 */
public interface IPaperWalletGenerator {
    /**
     * Please generate me a wallet that will have private key encrypted by provided on-time-password
     * User language is provided produce paper wallet instructions in customer's language selected on the ATM
     * @param cryptoCurrency
     * @param onetimePassword
     * @param shouldBeVanity  if true (default) the returned wallet address should be a vanity address, e.g. start with 1GB for bitcoin etc.
     *                        If this is false, any random address should be generated for improved privacy.
     * @param userLanguage*
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IPaperWallet
     */
    IPaperWallet generateWallet(String cryptoCurrency, String onetimePassword, String userLanguage, boolean shouldBeVanity);
}
