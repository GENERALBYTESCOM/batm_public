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
package com.generalbytes.batm.server.extensions.extra.ethereumclassic;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;

import java.util.Collections;
import java.util.Set;

public class EthereumClassicExtension extends AbstractExtension{
    private static final CryptoCurrencyDefinition ETC_CRYPTOCURRENCY_DEFINITION = new EtcDefinition();

    @Override
    public String getName() {
        return "BATM Ethereum Classic extension";
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return Collections.singleton(CryptoCurrency.ETC.getCode());
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return Collections.singleton(ETC_CRYPTOCURRENCY_DEFINITION);
    }

}
