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

/**
 * Represents the result of an attempt on identifying a wallet type.
 *
 * @see IWalletTypeEvaluationProvider
 */
public class WalletTypeEvaluationResult {

    private final CryptoWalletType walletType;
    private final boolean belongsToIdentity;
    private final Long travelRuleProviderId;
    private final String vaspDid;

    private WalletTypeEvaluationResult(CryptoWalletType walletType,
                                       boolean belongsToIdentity,
                                       Long travelRuleProviderId,
                                       String vaspDid
    ) {
        this.walletType = walletType;
        this.belongsToIdentity = belongsToIdentity;
        this.travelRuleProviderId = travelRuleProviderId;
        this.vaspDid = vaspDid;
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
     * @return Get the database ID of the used Travel Rule Provider.
     */
    public Long getTravelRuleProviderId() {
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
     * @param travelRuleProviderId Database ID of the used Travel Rule Provider.
     * @param vaspDid              VASP DID (decentralized identifier) of wallet owner.
     * @return The new {@link WalletTypeEvaluationResult}.
     * @throws IllegalArgumentException If the walletType is null.
     */
    public static WalletTypeEvaluationResult evaluated(CryptoWalletType walletType,
                                                       boolean belongsToIdentity,
                                                       Long travelRuleProviderId,
                                                       String vaspDid
    ) {
        validateEvaluatedResult(walletType, travelRuleProviderId, vaspDid);
        return new WalletTypeEvaluationResult(walletType, belongsToIdentity, travelRuleProviderId, vaspDid);
    }

    private static void validateEvaluatedResult(CryptoWalletType walletType, Long travelRuleProviderId, String vaspDid) {
        if (walletType == null) {
            throw new IllegalArgumentException("walletType cannot be null");
        }

        if (walletType == CryptoWalletType.CUSTODIAL) {
            if (travelRuleProviderId == null) {
                throw new IllegalArgumentException("travelRuleProviderId cannot be null for custodial wallet");
            }

            if (vaspDid == null || vaspDid.isEmpty()) {
                throw new IllegalArgumentException("vaspDid cannot be blank for custodial wallet");
            }
        }
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
