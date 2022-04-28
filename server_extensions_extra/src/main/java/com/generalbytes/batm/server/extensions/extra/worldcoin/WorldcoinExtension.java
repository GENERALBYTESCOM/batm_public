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
package com.generalbytes.batm.server.extensions.extra.worldcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.exceptions.helper.ExceptionHelper;
import com.generalbytes.batm.server.extensions.extra.worldcoin.sources.cd.CryptodiggersRateSource;
import com.generalbytes.batm.server.extensions.extra.worldcoin.wallets.worldcoind.WorldcoindRPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class WorldcoinExtension extends AbstractExtension{

    private static final Logger log = LoggerFactory.getLogger(WorldcoinExtension.class);

    @Override
    public String getName() {
        return "BATM Worldcoin extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            String walletType = null;
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                walletType = st.nextToken();

                if ("worldcoind".equalsIgnoreCase(walletType)) {
                    //"worldcoind:protocol:user:password:ip:port:accountname"

                    String protocol = st.nextToken();
                    String username = st.nextToken();
                    String password = st.nextToken();
                    String hostname = st.nextToken();
                    String port = st.nextToken();
                    String accountName = "";
                    if (st.hasMoreTokens()) {
                        accountName = st.nextToken();
                    }


                    if (protocol != null && username != null && password != null && hostname != null && port != null && accountName != null) {
                        String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                        return new WorldcoindRPCWallet(rpcURL, accountName);
                    }
                }
                if ("wdcdemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.WDC.getCode(), walletAddress);
                    }
                }
            } catch (Exception e) {
                String serialNumber = ExceptionHelper.findSerialNumberInStackTrace();
                log.warn("createWallet failed for prefix: {}, on terminal with serial number: {}", walletType, serialNumber);
            }

        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.WDC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new WorldcoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            String exchangeType = null;
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                exchangeType = st.nextToken();
                if ("cd".equalsIgnoreCase(exchangeType)) {
                    if (st.hasMoreTokens()) {
                        return new CryptodiggersRateSource(st.nextToken());
                    }
                    return new CryptodiggersRateSource(FiatCurrency.USD.getCode());
                } else if ("wdcfix".equalsIgnoreCase(exchangeType)) {
                    BigDecimal rate = BigDecimal.ZERO;
                    if (st.hasMoreTokens()) {
                        try {
                            rate = new BigDecimal(st.nextToken());
                        } catch (Throwable e) {
                        }
                    }
                    String preferedFiatCurrency = FiatCurrency.USD.getCode();
                    if (st.hasMoreTokens()) {
                        preferedFiatCurrency = st.nextToken().toUpperCase();
                    }
                    return new FixPriceRateSource(rate, preferedFiatCurrency);
                }
            } catch (Exception e) {
                String serialNumber = ExceptionHelper.findSerialNumberInStackTrace();
                log.warn("createRateSource failed for prefix: {}, on terminal with serial number: {}", exchangeType, serialNumber);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.WDC.getCode());
        return result;
    }
}
