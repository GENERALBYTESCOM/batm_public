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
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.generalbytes.batm.server.extensions.extra.common.RPCWallet;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;

public class ElementsdRPCWalletWithUniqueAddresses extends RPCWallet implements IGeneratesNewDepositCryptoAddress, IQueryableWallet {
    private static final Logger log = LoggerFactory.getLogger(ElementsdRPCWalletWithUniqueAddresses.class);
    private static final String ASSET_NAME = "bitcoin";
    public ElementsdRPCWalletWithUniqueAddresses(String rpcURL, String walletName) {
        super(rpcURL, walletName, CryptoCurrency.L_BTC.getCode());
    }

    @Override
    public RPCClient createClient(String cryptoCurrency, String rpcURL) {
        try {
            return new ElementsdRPCClient(cryptoCurrency, rpcURL, ASSET_NAME);
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }
        return null;
    }

    /**
     * This method returns amount that has at least 5 confirmations.
     * If there is no such amount it will return amount that has at least 4 confirmations. Then 3,2,1 and 0.
     * Note that Liquid Network is designed to have only one reorg. So 2 confirmations is considered safe.
     *
     * @param address
     * @param cryptoCurrency
     * @return
     */
    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        if (!cryptoCurrency.equals(CryptoCurrency.L_BTC.getCode())) {
            return ReceivedAmount.ZERO;
        }

        for (int confirmationsToTest=5; confirmationsToTest>=0; confirmationsToTest--) {
            RPCClient client = this.getClient();
            BigDecimal confirmedBalance =  client.getReceivedByAddress(address, confirmationsToTest);
            if (confirmedBalance == null) {
                return ReceivedAmount.ZERO;
            }
            if (confirmedBalance.compareTo(BigDecimal.ZERO) > 0) {
                return new ReceivedAmount(confirmedBalance, confirmationsToTest);
            }
        }
        return ReceivedAmount.ZERO;
    }
}
