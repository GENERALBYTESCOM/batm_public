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

package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;

import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseAddresses;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseBalance;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseWithdrawal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.*;

import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_HIGH;
import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_LOW;
import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_MEDIUM;

/**
 * @deprecated Use {@link BlockIOWalletWithClientSideSigning}
 */
@Deprecated
public class BlockIOWallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.BlockIOWallet");
    private String pin;
    private String priority;

    private IBlockIO api;

    public BlockIOWallet(String apiKey, String pin, String priority) {
        this.pin = pin;
        if (priority == null) {
            this.priority = PRIORITY_LOW;
        } else if (PRIORITY_LOW.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_LOW;
        }
        else if (PRIORITY_MEDIUM.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_MEDIUM;
        }
        else if (PRIORITY_HIGH.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_HIGH;
        } else {
            this.priority = PRIORITY_LOW;
        }
        ClientConfig config = new ClientConfig();
        config.addDefaultParam(QueryParam.class, "api_key", apiKey);
        api = RestProxyFactory.createProxy(IBlockIO.class, "https://block.io", config);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.DOGE.getCode());
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode();
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseAddresses response = api.getAddresses();
            if (response != null && response.getData() != null && response.getData().getAddresses() != null && response.getData().getAddresses().length> 0) {
                return response.getData().getAddresses()[0].getAddress();
            }
        }catch (Throwable t) {
            log.error("Error", t);
        }

        return null;
    }


    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseBalance response = api.getBalance();
            if (response != null && response.getData() != null && response.getData().getAvailable_balance() != null ) {
                return new BigDecimal(response.getData().getAvailable_balance());
            }
        }catch (Throwable t) {
            log.error("Error", t);
        }

        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseWithdrawal response = api.withdraw(pin, amount.toPlainString(), destinationAddress, priority);
            if (response != null && response.getStatus() != null && "success".equalsIgnoreCase(response.getStatus()) && response.getData() != null && response.getData().getTxid() !=null) {
                return response.getData().getTxid();
            }
        }catch (Throwable t) {
            log.error("Error", t);
        }

        return null;
    }

//    public static void main(String[] args) {
//        final BlockIOWallet blockIOWallet = new BlockIOWallet("xxxx", "xxxx", "medium");
//        final BigDecimal ltc = blockIOWallet.getCryptoBalance("LTC");
//        System.out.println("ltc = " + ltc);
//        String address = blockIOWallet.getCryptoAddress("LTC");
//        System.out.println("address = " + address);
//        String s = blockIOWallet.sendCoins("LSYi8VQjbR3LAAdqkd4jSzj3Ci5B9Pryvk", new BigDecimal("0.01"), "LTC", "blabla");
//        System.out.println("s = " + s);
//    }

}
