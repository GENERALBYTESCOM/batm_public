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
package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.ERC20Wallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.bizz.BizzDefinition;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.dai.DaiDefinition;
import com.generalbytes.batm.server.extensions.extra.ethereum.sources.stasis.StasisTickerRateSource;
import com.generalbytes.batm.server.extensions.extra.ethereum.stream365.Stream365;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class EthereumExtension extends AbstractExtension{

    private static final Logger log = LoggerFactory.getLogger(EthereumExtension.class);
    private static final Set<ICryptoCurrencyDefinition> cryptoCurrencyDefinitions = ImmutableSet.of(
            new DaiDefinition(),
            new EthDefinition(),
            new BizzDefinition(),
            new UsdtDefinition(),
            new UsdcDefinition());

    @Override
    public String getName() {
        return "BATM Ethereum extension";
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        HashSet<String> result = new HashSet<>();
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.ANT.getCode());
        result.add(CryptoCurrency.BAT.getCode());
        result.add(CryptoCurrency.REP.getCode());
        result.add(CryptoCurrency.MKR.getCode());
        result.add(CryptoCurrency.BTOKEN.getCode());
        result.add(CryptoCurrency.DAI.getCode());
        result.add(CryptoCurrency.BIZZ.getCode());
        result.add(CryptoCurrency.BTBS.getCode());
        result.add(CryptoCurrency.GQ.getCode());
        result.add(CryptoCurrency.HBX.getCode());
        result.add(CryptoCurrency.VOLTZ.getCode());
        result.add(CryptoCurrency.THBX.getCode());
        result.add(CryptoCurrency.MUSD.getCode());
        result.add(CryptoCurrency.EURS.getCode());
        result.add(CryptoCurrency.USDC.getCode());
        result.add(CryptoCurrency.USDT.getCode());
        result.add(CryptoCurrency.ZPAE.getCode());
        result.add(CryptoCurrency.PAXG.getCode());
        result.add(CryptoCurrency.HT.getCode());
        result.add(CryptoCurrency.LMY.getCode());
        result.add(CryptoCurrency.USDS.getCode());
        result.add(CryptoCurrency.JOB.getCode());
        result.add(CryptoCurrency.WILC.getCode());
        result.add(CryptoCurrency.SHIB.getCode());
        return result;
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(walletLogin,":");
                String walletType = st.nextToken();

                if ("infura".equalsIgnoreCase(walletType)) {
                    String projectId = st.nextToken();
                    String passwordOrMnemonic = st.nextToken();
                    if (projectId != null && passwordOrMnemonic != null) {
                        return new InfuraWallet(projectId, passwordOrMnemonic);
                    }
                }else if (walletType.startsWith("infuraERC20_")) {
                    StringTokenizer wt = new StringTokenizer(walletType,"_");
                    wt.nextToken();//no use for this one
                    String tokenSymbol = wt.nextToken();
                    int tokenDecimalPlaces = Integer.parseInt(wt.nextToken());
                    String contractAddress = wt.nextToken();

                    String projectId = st.nextToken();
                    String passwordOrMnemonic = st.nextToken();
                    BigInteger gasLimit = null;
                    if (st.hasMoreTokens()) {
                        gasLimit = new BigInteger(st.nextToken());
                    }
                    BigDecimal gasPriceMultiplier = BigDecimal.ONE;
                    if (st.hasMoreTokens()) {
                        gasPriceMultiplier = new BigDecimal(st.nextToken());
                    }

                    if (projectId != null && passwordOrMnemonic != null) {
                        return new ERC20Wallet(projectId, passwordOrMnemonic, tokenSymbol, tokenDecimalPlaces, contractAddress, gasLimit, gasPriceMultiplier);
                    }
                }
            } catch (Exception e) {
                log.warn("createWallet failed for prefix: {}, {}: {} ",
                    ExtensionsUtil.getPrefixWithCountOfParameters(walletLogin), e.getClass().getSimpleName(), e.getMessage()
                );
            }
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            if (sourceLogin.startsWith("stream365")) {
                return new Stream365();
            } else if (sourceLogin.startsWith("stasis")) {
                return new StasisTickerRateSource();
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

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return cryptoCurrencyDefinitions;
    }

}
