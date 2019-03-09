/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
 

public class LskWallet extends BinanceWallet{
    private static final Logger log = LoggerFactory.getLogger("batm.master.LskWallet");

    public LskWallet(String address, String binanceApiKey, String binanceApiSecret) {
        super(address,binanceApiKey,binanceApiSecret);
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.LSK.getCode();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.LSK.getCode());
        return result;
    }
}
