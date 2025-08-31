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
package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd.ElementsdRPCWallet;
import com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd.ElementsdRPCWalletWithUniqueAddresses;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.*;

public class LiquidBitcoinExtension extends AbstractExtension{

    @Override
    public String getName() {
        return "BATM Liquid Network Bitcoin extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("elementsd".equalsIgnoreCase(walletType)
                || "elementsdnoforward".equalsIgnoreCase(walletType)) {
                //"elementsd:protocol:user:password:ip:port:walletName"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                String walletName = "";
                if (st.hasMoreTokens()) {
                    walletName = st.nextToken();
                }

                InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(hostname, port));
                hostname = tunnelAddress.getHostString();
                port = tunnelAddress.getPort();

                if (protocol != null && username != null && password != null && hostname !=null && walletName != null) {
                    String rpcURL = protocol +"://" + username +":" + password + "@" + hostname +":" + port;
                    if ("elementsdnoforward".equalsIgnoreCase(walletType)) {
                        return new ElementsdRPCWalletWithUniqueAddresses(rpcURL);
                    }
                    return new ElementsdRPCWallet(rpcURL, walletName);
                }
            }
        }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.L_BTC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new LiquidBitcoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                String exchangeType = st.nextToken();

                if ("l_btcfix".equalsIgnoreCase(exchangeType)) {
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
                ExtensionsUtil.logExtensionParamsException("createRateSource", getClass().getSimpleName(), sourceLogin, e);
            }

        }
        return null;
    }
    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.L_BTC.getCode());
        return result;
    }
}
