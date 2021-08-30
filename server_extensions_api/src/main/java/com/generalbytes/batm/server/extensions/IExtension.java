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

import com.generalbytes.batm.server.extensions.aml.IAMLProvider;
import com.generalbytes.batm.server.extensions.aml.IExternalIdentityProvider;
import com.generalbytes.batm.server.extensions.aml.scoring.ITransactionScoringProvider;
import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.communication.IPhoneLookupProvider;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.util.Set;

/**
 * This class provides information to the server about the available cryptocurrencies and represents
 * the single point for creating objects later used for manipulating with the coins of supported cryptocurrencies
 */
public interface IExtension {
    /**
     * This method is called after Extension is instantiated and before any other extension is called
     * @param ctx
     */
    void init(IExtensionContext ctx);

    /**
     * This method is called before the Extension is unloaded.
     */
    void deinit();

    /**
     * Returns the name of the extension.
     * @return
     */
    String getName();

    /**
     * Returns list of cryptocurrencies that this extension supports
     * @return
     */
    Set<String> getSupportedCryptoCurrencies();

    /**
     * Extension can optionally provide cryptocurrency definitions.
     * Cryptocurrency definition must exist if you want to have a support for two way ATM and Point of sale terminal for your cryptocurrency
     * Only one @{@link ICryptoCurrencyDefinition} can exist in whole server.
     * @return
     */
    Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions();
    /**
     * This method is used for creating implementation of crypto exchange
     * @param exchangeLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IExchange
     */
    IExchange createExchange(String exchangeLogin);

    /**
     * This method is used for creating implementation of payment processor
     * @param paymentProcessorLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IPaymentProcessor
     */
    IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin);

    /**
     * This method is used for creating implementation of coin price source
     * @param sourceLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IRateSource
     */
    IRateSource createRateSource(String sourceLogin);

    /**
     * This method is used for creating implementation of cryptocurrency hot wallet used by the server
     *
     * @param walletLogin colon-separated list of parameters for the wallet connection.
     *                    The first parameter ("prefix") defines which wallet to use.
     * @param tunnelPassword ssh password to establish an encrypted tunnel to the wallet host.
     *                    It can be {@null null} or an empty string when no tunnel is required.
     *                    If the wallet defined by the prefix in {@code walletLogin} is not supporting tunnels
     *                    {@code tunnelPassword} parameter is ignored.
     * @return
     * @see com.generalbytes.batm.server.extensions.IWallet
     */
    IWallet createWallet(String walletLogin, String tunnelPassword);

    /**
     * This method is used for creating implementation cryptocurrency address validator used by the server
     * @param cryptoCurrency
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.ICryptoAddressValidator
     */
    ICryptoAddressValidator createAddressValidator(String cryptoCurrency);

    /**
     * This method is used for creating implementation cryptocurrency paper wallet generator
     * @param cryptoCurrency
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IPaperWalletGenerator
     * @see com.generalbytes.batm.server.extensions.IPaperWallet
     */
    IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency);

    /**
     * Returns the list of watchlists that extenstion contains
     * @return
     */
    Set<String> getSupportedWatchListsNames();


    /**
     * Returns watchlist by name
     * @param name
     * @return
     */
    IWatchList getWatchList(String name);

    /**
     * Returns set of the rest services classes that will be added on https://localhost:7743/extensions/xxx
     * @return
     */
    Set<IRestService> getRestServices();

    /**
     * Returns list of available commands that will be available to server's chatbot
     * @return
     */
    Set<Class> getChatCommands();

    /**
     * Optionally returns external identity providers that can be used by server to look up identities.
     * @return
     */
    Set<IExternalIdentityProvider> getIdentityProviders();

    /**
     * Optionally returns AML providers that can be used by server to for example validate identities
     * @return
     */
    Set<IAMLProvider> getAMLProviders();

    /**
     * Optionally returns phone lookup providers that can be used to find information about phone number.
     * @return
     */
    Set<IPhoneLookupProvider> getPhoneLookupProviders();

    /**
     * @param transactionScoringProviderParamValues colon-separated list of parameters for the provider connection.
     *                    The first parameter ("prefix") defines which provider to use.
     */
    ITransactionScoringProvider createTransactionScoringProvider(String transactionScoringProviderParamValues);

    /**
     * Optionally returns external communication providers that can be used by server to sending SMS or making voice calls.
     * @return
     */
    Set<ICommunicationProvider> getCommunicationProviders();

    /**
     * @return Validators that can be used to validate SSNs
     */
    Set<ISsnValidator> getSsnValidators();
}
