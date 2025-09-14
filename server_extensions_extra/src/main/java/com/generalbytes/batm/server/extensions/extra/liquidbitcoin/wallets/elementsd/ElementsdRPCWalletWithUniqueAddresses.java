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
package com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.generalbytes.batm.server.extensions.extra.common.RPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class ElementsdRPCWalletWithUniqueAddresses extends RPCWallet implements IGeneratesNewDepositCryptoAddress {
    private static final Logger log = LoggerFactory.getLogger(ElementsdRPCWalletWithUniqueAddresses.class);

    public ElementsdRPCWalletWithUniqueAddresses(String rpcURL, String walletName) {
        super(rpcURL, walletName, CryptoCurrency.L_BTC.getCode());
    }

    @Override
    public RPCClient createClient(String cryptoCurrency, String rpcURL) {
        try {
            return new ElementsdRPCClient(cryptoCurrency, rpcURL,"bitcoin");
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }
        return null;
    }
}
