/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.tokencoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.paperwallet.TokencoinPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind.TokenWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class TKNExtension extends AbstractExtension{

    private static final Logger log = LoggerFactory.getLogger(TKNExtension.class);

    @Override
    public String getName() { return "BATM TKN extension"; }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if ("tokencoind".equalsIgnoreCase(walletType)) {
                    //"nud:protocol:user:password:ip:port:accountname"

                    String host = st.nextToken();
                    String portn = st.nextToken();
                    String accountid = st.nextToken();
                    int port = Integer.parseInt(portn);


                    if (host != null && portn != null) {
                        return new TokenWallet(host, port, accountid);
                    }
                }
                if ("tkndemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.TKN.getCode(), walletAddress);
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
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.TKN.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new TKNAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.TKN.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new TokencoinPaperWalletGenerator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                String rsType = st.nextToken();

                if ("tknfix".equalsIgnoreCase(rsType)) { // fixed price
                    BigDecimal rate = BigDecimal.ZERO;
                    if (st.hasMoreTokens()) {
                        try {
                            rate = new BigDecimal(st.nextToken());
                        } catch (Throwable e) {
                        }
                    }
                    String preferedFiatCurrency = FiatCurrency.EUR.getCode();
                    if (st.hasMoreTokens()) {
                        preferedFiatCurrency = st.nextToken().toUpperCase();
                    }
                    return new FixPriceRateSource(rate, preferedFiatCurrency);
                }
            } catch (Exception e) {
                log.warn("createRateSource failed for prefix: {}, {}: {} ",
                    ExtensionsUtil.getPrefixWithCountOfParameters(sourceLogin), e.getClass().getSimpleName(), e.getMessage()
                );
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.TKN.getCode());
        return result;
    }
}
