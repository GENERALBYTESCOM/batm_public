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
     * Returns the name of the extension.
     * @return
     */
    public String getName();

    /**
     * Returns list of crypto currencies that this extension supports
     * @return
     */
    public Set<String> getSupportedCryptoCurrencies();

    /**
     * This method is used for creating implementation of crypto exchange
     * @param exchangeLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IExchange
     */
    public IExchange createExchange(String exchangeLogin);

    /**
     * This method is used for creating implementation of payment processor
     * @param paymentProcessorLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IPaymentProcessor
     */
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin);

    /**
     * This method is used for creating implementation of coin price source
     * @param sourceLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IRateSource
     */
    public IRateSource createRateSource(String sourceLogin);

    /**
     * This method is used for creating implementation of cryptocurrency hot wallet used by the server
     * @param walletLogin
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IWallet
     */
    public IWallet createWallet(String walletLogin);

    /**
     * This method is used for creating implementation cryptocurrency address validator used by the server
     * @param cryptoCurrency
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.ICryptoAddressValidator
     */
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency);

    /**
     * This method is used for creating implementation cryptocurrency paper wallet generator
     * @param cryptoCurrency
     * @return
     *
     * @see com.generalbytes.batm.server.extensions.IPaperWalletGenerator
     * @see com.generalbytes.batm.server.extensions.IPaperWallet
     */
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency);

    /**
     * Returns the list of watchlists that extenstion contains
     * @return
     */
    public Set<String> getSupportedWatchListsNames();


    /**
     * Returns watchlist by name
     * @param name
     * @return
     */
    public IWatchList getWatchList(String name);


}
