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

package com.generalbytes.batm.server.extensions.travelrule;

/**
 * Represents the possible types of wallets.
 */
public enum CryptoWalletType {
    /**
     * Owned and managed by a third party, such as a centralized exchange like Binance.
     * The third party controls the private keys, and the user interacts with their funds through the service provider.
     */
    CUSTODIAL,
    /**
     * Also known as a noncustodial wallet, where the user has full control over their private keys.
     * Examples include hardware wallets, software wallets, and paper wallets. The user is solely responsible
     * for securing their funds.
     */
    UNHOSTED,

    UNKNOWN
}
