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
package com.generalbytes.batm.server.extensions.extra.ucacoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.ucacoin.wallets.UcacoinRPCWallet;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.DigiFinexExchange;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class UcacoinExtension extends AbstractExtension{
    @Override
    public String getName() {
        return "BATM Ucacoin extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("ucacoind".equalsIgnoreCase(walletType)) {
                //"ucacoind:protocol:user:password:ip:port:accountname"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();
                String accountName ="";
                if (st.hasMoreTokens()) {
                    accountName = st.nextToken();
                }


                if (protocol != null && username != null && password != null && hostname !=null && port != null && accountName != null) {
                    String rpcURL = protocol +"://" + username +":" + password + "@" + hostname +":" + port;
                    return new UcacoinRPCWallet(rpcURL,accountName);
                }
            }
            if ("ucademo".equalsIgnoreCase(walletType)) {

                String fiatCurrency = st.nextToken();
                String walletAddress = "";
                if (st.hasMoreTokens()) {
                    walletAddress = st.nextToken();
                }

                if (fiatCurrency != null && walletAddress != null) {
                    return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.UCA.getCode(), walletAddress);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.UCA.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new UcacoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ("coinmarketcap".equalsIgnoreCase(exchangeType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String apiKey = null;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                if (st.hasMoreTokens()) {
                    apiKey = st.nextToken();
                }
                return DigiFinexExchange.asRateSource(preferredFiatCurrency);
            }

        }
        return null;
    }
    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.UCA.getCode());
        return result;
    }

    @Override
    public IExchange createExchange(String paramString) {
        if ((paramString != null) && (!paramString.trim().isEmpty())) {
            StringTokenizer paramTokenizer = new StringTokenizer(paramString, ":");
            String prefix = paramTokenizer.nextToken();
            if ("digifinex".equalsIgnoreCase(prefix)) {
                String apiKey = paramTokenizer.nextToken();
                String apiSecret = paramTokenizer.nextToken();
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (paramTokenizer.hasMoreTokens()) {
                    preferredFiatCurrency = paramTokenizer.nextToken().toUpperCase();
                }
                return DigiFinexExchange.asExchange(apiKey, apiSecret, preferredFiatCurrency);
            }
        }

        return null;
    }
}
