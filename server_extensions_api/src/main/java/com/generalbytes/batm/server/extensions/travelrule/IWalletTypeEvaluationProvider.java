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
 * This provider is used to evaluate the type of wallet based on its characteristics.
 *
 * @see CryptoWalletType
 */
public interface IWalletTypeEvaluationProvider {

    /**
     * Attempt to evaluate the type of wallet based on the provided {@link IIdentityWalletEvaluationRequest}.
     * <p>
     * This method should either return {@link WalletTypeEvaluationResult#evaluated(CryptoWalletType, boolean)},
     * with the respective {@link CryptoWalletType} if the wallet was successfully evaluated.
     * If the wallet type cannot be evaluated, it returns {@link WalletTypeEvaluationResult#unknown()}.
     * </p>
     *
     * @param walletContext The context containing information needed to identify the wallet type.
     * @return A {@link WalletTypeEvaluationResult} indicating success with the wallet type, or failure if not evaluated.
     */
    WalletTypeEvaluationResult evaluateWalletType(IIdentityWalletEvaluationRequest walletContext);

}
