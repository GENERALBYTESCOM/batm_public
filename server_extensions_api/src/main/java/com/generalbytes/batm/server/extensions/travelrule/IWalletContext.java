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

import com.generalbytes.batm.server.extensions.IIdentity;

/**
 * This class holds basic information required to identify a wallet.
 */
public interface IWalletContext {

    /**
     * Get the public id of the identity that the wallet belongs to (if any.)
     *
     * @return The identity public id or null.
     * @see IIdentity
     */
    String getIdentityPublicId();

    /**
     * Get the external id of the identity that the wallet belongs to (if any.)
     *
     * @return The identity external id or null.
     * @see IIdentity
     */
    String getIdentityExternalId();

    /**
     * Get the crypto address of the wallet.
     *
     * @return The crypto address of the wallet.
     */
    String getCryptoAddress();

    /**
     * Get the cryptocurrency of the wallet.
     *
     * @return The cryptocurrency of the wallet.
     */
    String getCryptocurrency();

}
