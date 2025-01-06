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
 * Represents the result of an attempt on identifying a wallet type.
 *
 * @see IWalletTypeEvaluationProvider
 */
public class WalletTypeEvaluationResult {

    private final CryptoWalletType walletType;
    private final boolean belongsToIdentity;
    private final String vaspDid;
    private final String travelRuleProviderId;

    private WalletTypeEvaluationResult(CryptoWalletType walletType,
                                       boolean belongsToIdentity,
                                       String vaspDid,
                                       String travelRuleProviderId
    ) {
        this.walletType = walletType;
        this.belongsToIdentity = belongsToIdentity;
        this.vaspDid = vaspDid;
        this.travelRuleProviderId = travelRuleProviderId;
    }

    /**
     * Get the {@link CryptoWalletType} of the identified wallet.
     *
     * @return The {@link CryptoWalletType}.
     */
    public CryptoWalletType getWalletType() {
        return walletType;
    }

    /**
     * @return True if the wallet belongs to the provided identity, false otherwise.
     */
    public boolean isBelongsToIdentity() {
        return belongsToIdentity;
    }

    /**
     * @return Get the VASP DID (decentralized identifier) of wallet owner.
     */
    public String getVaspDid() {
        return vaspDid;
    }

    /**
     * @return Get the ID (name) of the used Travel Rule Provider.
     */
    public String getTravelRuleProviderId() {
        return travelRuleProviderId;
    }

    /**
     * Create a {@link WalletTypeEvaluationResult} for cases where a wallet type
     * is successfully evaluated.
     *
     * @param walletType        The {@link CryptoWalletType} of the evaluated wallet.
     * @param belongsToIdentity True if the wallet belongs to the provided identity, false otherwise.
     * @return The new {@link WalletTypeEvaluationResult}.
     * @throws IllegalArgumentException If the walletType is null.
     */
    public static WalletTypeEvaluationResult evaluated(CryptoWalletType walletType, boolean belongsToIdentity) {
        return evaluated(walletType, belongsToIdentity, null, null);
    }

    /**
     * Create a {@link WalletTypeEvaluationResult} for cases where a wallet type
     * is successfully evaluated.
     *
     * @param walletType           The {@link CryptoWalletType} of the evaluated wallet.
     * @param belongsToIdentity    True if the wallet belongs to the provided identity, false otherwise.
     * @param vaspDid              VASP DID (decentralized identifier) of wallet owner.
     * @param travelRuleProviderId ID (name) of the used Travel Rule Provider.
     * @return The new {@link WalletTypeEvaluationResult}.
     * @throws IllegalArgumentException If the walletType is null.
     */
    public static WalletTypeEvaluationResult evaluated(CryptoWalletType walletType,
                                                       boolean belongsToIdentity,
                                                       String vaspDid,
                                                       String travelRuleProviderId
    ) {
        if (walletType == null) {
            throw new IllegalArgumentException("walletType cannot be null");
        }
        return new WalletTypeEvaluationResult(walletType, belongsToIdentity, vaspDid, travelRuleProviderId);
    }

    /**
     * Create a {@link WalletTypeEvaluationResult} for cases where a wallet type
     * could not be identified and remains unknown.
     *
     * @return The new {@link WalletTypeEvaluationResult}.
     */
    public static WalletTypeEvaluationResult unknown() {
        return new WalletTypeEvaluationResult(CryptoWalletType.UNKNOWN, false, null, null);
    }

}
