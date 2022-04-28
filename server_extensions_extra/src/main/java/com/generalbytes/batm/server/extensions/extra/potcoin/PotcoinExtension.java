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
package com.generalbytes.batm.server.extensions.extra.potcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.exceptions.helper.ExceptionHelper;
import com.generalbytes.batm.server.extensions.extra.potcoin.wallets.potwallet.Potwallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class PotcoinExtension extends AbstractExtension{

    private static final Logger log = LoggerFactory.getLogger(PotcoinExtension.class);

    @Override
    public String getName() {
        return "BATM Potcoin extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            String walletType = null;
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                walletType = st.nextToken();

                if ("potwallet".equalsIgnoreCase(walletType)) {
                    String publicKey = st.nextToken();
                    String privateKey = st.nextToken();
                    String walletId = st.nextToken();

                    if (publicKey != null && privateKey != null && walletId != null) {
                        return new Potwallet(publicKey, privateKey, walletId);
                    }
                }
                if ("potdemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.POT.getCode(), walletAddress);
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
        if (CryptoCurrency.POT.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new PotcoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        String rsType = null;
        try {
            if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(sourceLogin,":");
                rsType = st.nextToken();

                if ("potfix".equalsIgnoreCase(rsType)) {
                    BigDecimal rate = BigDecimal.ZERO;
                    if (st.hasMoreTokens()) {
                        rate = new BigDecimal(st.nextToken());
                    }
                    String preferedFiatCurrency = FiatCurrency.CAD.getCode();
                    if (st.hasMoreTokens()) {
                        preferedFiatCurrency = st.nextToken().toUpperCase();
                    }
                    return new FixPriceRateSource(rate,preferedFiatCurrency);
                }

            }
        } catch (Exception e) {
            String serialNumber = ExceptionHelper.findSerialNumberInStackTrace();
            log.warn("createRateSource failed for prefix: {}, on terminal with serial number: {}", rsType, serialNumber);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.POT.getCode());
        return result;
    }
}
