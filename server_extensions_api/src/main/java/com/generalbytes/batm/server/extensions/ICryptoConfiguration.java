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

import java.math.BigDecimal;

public interface ICryptoConfiguration {
    /**
     * Name of the crypto configuration as it is stored in administration
     * @return
     */
    String getName();

    /**
     * Cryptocurrency for which is following configuration valid.
     * @return
     */
    String getCryptoCurrency();

    /**
     * Unit in which you prefer to display cryptocurrency amount to human
     * @return
     */
    String getCryptoCurrencyUnit();

    /**
     * Exchange strategy for buy direction
     * @return
     */
    int getBuyExchangeStrategy();

    /**
     * Exchange strategy for sell direction
     * @return
     */
    int getSellExchangeStrategy();

    /**
     * Wallet for buy direction
     * @return
     */
    IWallet getBuyWallet();

    /**
     * Wallet for sell direction
     * @return
     */
    IWallet getSellWallet();

    /**
     * Rate source for buy direction
     * @return
     */
    IRateSource getBuyRateSource();

    /**
     * Rate source for sell direction
     * @return
     */
    IRateSource getSellRateSource();

    /**
     * Exchange for buy direction
     * @return
     */
    IExchange getBuyExchange();

    /**
     * Exchange for sell direction
     * @return
     */
    IExchange getSellExchange();

    /**
     * Profit percentage for buy direction
     * @return
     */
    BigDecimal getProfitBuy();

    /**
     * Profit percentage for sell direction
     * @return
     */
    BigDecimal getProfitSell();
}
