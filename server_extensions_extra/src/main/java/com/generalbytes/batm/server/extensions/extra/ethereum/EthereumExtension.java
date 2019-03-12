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
package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.ERC20Wallet;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class EthereumExtension extends AbstractExtension{
    @Override
    public String getName() {
        return "BATM Ethereum extension";
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        HashSet<String> result = new HashSet<>();
        result.add(CryptoCurrency.BAT.getCode());
        result.add(CryptoCurrency.DAI.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.MKR.getCode());
        result.add(CryptoCurrency.REP.getCode());
        result.add(CryptoCurrency.REP.getCode());
        return result;
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("infura".equalsIgnoreCase(walletType)) {
                String apiKey = st.nextToken();
                String passwordOrMnemonic = st.nextToken();
                if (apiKey != null && passwordOrMnemonic != null) {
                    return new InfuraWallet(apiKey, passwordOrMnemonic);
                }
            }else if (walletType.startsWith("infuraERC20_")) {
                StringTokenizer st2 = new StringTokenizer(walletType,"_");
                st2.nextToken();//no use for this one
                String tokenSymbol = st2.nextToken();
                int tokenDecimalPlaces = Integer.parseInt(st2.nextToken());
                String contractAddress = st2.nextToken();
                String apiKey = st.nextToken();
                String passwordOrMnemonic = st.nextToken();
                if (apiKey != null && passwordOrMnemonic != null) {
                    return new ERC20Wallet(apiKey, passwordOrMnemonic, tokenSymbol, tokenDecimalPlaces, contractAddress);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (!getSupportedCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }

        return new ICryptoAddressValidator() {

            @Override
            public boolean isAddressValid(String address) {
                return EtherUtils.isEtherAddressValid(address);
            }

            @Override
            public boolean isPaperWalletSupported() {
                return false;
            }

            @Override
            public boolean mustBeBase58Address() {
                return false;
            }

        };
    }
}
