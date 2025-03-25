/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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
public interface IIdentityWalletEvaluationRequest {

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
     * <p>If the cryptocurrency supports the destination tag / memo, for example Ripple (XRP),
     * then the address is in the format "{@code address:destination_tag}".</p>
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

    /**
     * Get the DID of the VASP that should be verified as the owner of the wallet (crypto address).
     * The VASP DID is available only if the user manually declares the CUSTODIAL wallet and selects a VASP on the terminal.
     * This is used for providers that do not support VASP searches based on crypto address.
     *
     * @return VASP DID.
     */
    String getDidOfVaspHostingCustodialWallet();

    /**
     * Get ID of provider with which the VASP hosting the custodial wallet is registered.
     *
     * @return ID of Travel Rule provider.
     *
     * @see #getDidOfVaspHostingCustodialWallet()
     */
    Long getTravelRuleProviderId();

}
