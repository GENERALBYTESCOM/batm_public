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

import java.util.List;

/**
 * A Travel Rule Provider definition that makes it possible to connect to an external provider using its API.
 * Provider is responsible for implementing compliance checks and procedures necessary to ensure adherence to the Travel Rule regulations.
 */
public interface ITravelRuleProvider {

    /**
     * This is used as the provider identifier that is displayed in CAS.
     * @return Name of Travel Rule Provider.
     */
    String getName();

    /**
     * Get information about a crypto wallet.
     *
     * @param walletEvaluationRequest The wallet's context data.
     * @return Information about the wallet.
     */
    ITravelRuleWalletInfo getWalletInfo(IIdentityWalletEvaluationRequest walletEvaluationRequest);

    /**
     * Forces wallet verification by the provider if the customer has manually declared the wallet as CUSTODIAL for a specific VASP.
     *
     * <p>If {@code true}, {@link #getWalletInfo} will be called before {@link #createTransfer}
     * if the wallet is CUSTODIAL and the customer has declared VASP manually on the terminal.
     * Even though it is {@code true} and the wallet will be successfully evaluated locally (the wallet was added by an operator in CAS
     * or the wallet was already verified in the past) or using {@link IWalletTypeEvaluationProvider} as CUSTODIAL,
     * {@link #getWalletInfo} will NOT be called and {@link #createTransfer} will be called directly.</p>
     *
     * <p>If {@code false} and the customer has manually declared a VASP on the terminal,
     * {@link #getWalletInfo} is skipped and {@link #createTransfer} is called directly.</p>
     */
    default boolean verifyCustomerDeclaredCustodialWallet() {
        return false;
    }

    /**
     * Get all available VASPs.
     *
     * @return List of all available VASPs.
     * @throws TravelRuleProviderException In case of failure.
     */
    List<ITravelRuleVasp> getAllVasps();

    /**
     * Create a new transfer, sending a message to the Beneficiary VASP.
     *
     * @param outgoingTransferData The data to create a transfer from.
     * @return {@link ITravelRuleTransferInfo} of the new transfer or null in case of failure.
     */
    ITravelRuleTransferInfo createTransfer(ITravelRuleTransferData outgoingTransferData);

    /**
     * Register a new listener for transfer status updates.
     *
     * <p>Whenever the status of a transfer, related to the given VASP, changes, the method
     * {@link ITravelRuleTransferUpdateListener#onTransferStatusUpdate(ITravelRuleTransferStatusUpdateEvent)}
     * on this listener will be called.</p>
     *
     * @param listener The listener.
     * @return True if the listener was successfully registered, false otherwise.
     */
    boolean registerStatusUpdateListener(ITravelRuleTransferUpdateListener listener);

    /**
     * Unregister an existing listener for transfer status updates.
     *
     * @return True if the listener was successfully unregistered, false otherwise.
     */
    boolean unregisterStatusUpdateListener();

    /**
     * Update an existing transfer.
     *
     * @param request The request.
     * @return The updated transfer info or null if the update fails.
     */
    ITravelRuleTransferInfo updateTransfer(ITravelRuleTransferUpdateRequest request);

    /**
     * Called when the provider configuration changes.
     *
     * This may be used to update the provider's state or credentials used to call the provider's API.
     */
    void notifyProviderConfigurationChanged();

    /**
     * Test whether the Travel Rule Provider is configured correctly.
     * This test is called by the user from CAS.
     *
     * @return {@code True} if configuration is valid, otherwise {@code false}.
     */
    boolean testProviderConfiguration();
}
