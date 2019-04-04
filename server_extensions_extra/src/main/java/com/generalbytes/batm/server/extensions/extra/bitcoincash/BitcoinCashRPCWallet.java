/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.RPCWallet;

//You need to have node running: i.e.:  bitcoind -rpcuser=rpcuser -rpcpassword=rpcpassword -rpcport=8332

public class BitcoinCashRPCWallet extends RPCWallet {
    public BitcoinCashRPCWallet(String rpcURL, String accountName) {
        super(rpcURL, accountName, CryptoCurrency.BCH.getCode());
    }
}
