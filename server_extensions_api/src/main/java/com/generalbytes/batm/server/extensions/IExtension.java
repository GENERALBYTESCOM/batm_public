/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.util.Set;

/**
 * This class provides information to the server about the available crypto currencies and represents
 * the single point for creating objects later used for manipulating with the coins of supported cryptocurrencies
 */
public interface IExtension {
    /**
     * This method is called after Extension is instantiated and before any other extension is called
     * @param ctx
     */
    void init(IExtensionContext ctx);

    /**
     * Returns the name of the extension.
     * @return
     */
    String getName();

    /**
     * Returns list of crypto currencies that this extension supports
     * @return
     */
    Set<String> getSupportedCryptoCurrencies();

    /**
     * Extension can optionally provide crypto currency definitions.
     * Crypto currency definition must exist if you want to have a support for two way ATM and Point of sale terminal for your crypto currency
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
     * @param walletLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IWallet
     */
    IWallet createWallet(String walletLogin);

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

}
