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

    private WalletTypeEvaluationResult(CryptoWalletType walletType) {
        this.walletType = walletType;
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
     * Create a {@link WalletTypeEvaluationResult} for cases where a wallet type
     * is successfully evaluated.
     *
     * @param walletType The {@link CryptoWalletType} of the evaluated wallet.
     * @return The new {@link WalletTypeEvaluationResult}.
     * @throws IllegalArgumentException If the walletType is null or unknown.
     */
    public static WalletTypeEvaluationResult evaluated(CryptoWalletType walletType) {
        if (walletType == null) {
            throw new IllegalArgumentException("walletType cannot be null");
        }
        if (walletType == CryptoWalletType.UNKNOWN) {
            throw new IllegalArgumentException("walletType cannot be unknown");
        }
        return new WalletTypeEvaluationResult(walletType);
    }

    /**
     * Create a {@link WalletTypeEvaluationResult} for cases where a wallet type
     * could not be identified and remains unknown.
     *
     * @return The new {@link WalletTypeEvaluationResult}.
     */
    public static WalletTypeEvaluationResult unknown() {
        return new WalletTypeEvaluationResult(CryptoWalletType.UNKNOWN);
    }

}
