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

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.sources.telr.TelrRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.wallets.telr.TelrCashWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BitcoinCashExtension extends AbstractExtension {
    private static final ICryptoCurrencyDefinition DEFINITION = new BitcoinCashDefinition();
    public static final String CURRENCY = CryptoCurrency.BCH.getCode();
    private static final Logger log = LoggerFactory.getLogger(BitcoinCashExtension.class);

    @Override
    public String getName() {
        return "BATM Bitcoin Cash extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();
            if ("bitcoincashd".equalsIgnoreCase(walletType)
                || "bitcoincashdnoforward".equalsIgnoreCase(walletType)) {
                //"bitcoind:protocol:user:password:ip:port:accountname"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                String label = "";
                if (st.hasMoreTokens()) {
                    label = st.nextToken();
                }

                InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(tunnelPassword, InetSocketAddress.createUnresolved(hostname, port));
                hostname = tunnelAddress.getHostString();
                port = tunnelAddress.getPort();


                if (protocol != null && username != null && password != null && hostname != null && label != null) {
                    String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                    if ("bitcoincashdnoforward".equalsIgnoreCase(walletType)) {
                        return new BitcoinCashUniqueAddressRPCWallet(rpcURL);
                    }
                    return new BitcoinCashRPCWallet(rpcURL, label);
                }
            } else if ("telr_cash".equalsIgnoreCase(walletType)) {
                String address = st.nextToken();
                String secret = st.nextToken();
                String signature = st.nextToken();
                return new TelrCashWallet(address, secret, signature);
            }
        }
        } catch (Exception e) {
            log.warn("createWallet failed", e);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CURRENCY);
        return result;
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
        result.add(DEFINITION);
        return result;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return new BitcoinCashAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.BCH.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new BitcoinCashWalletGenerator("qqqq", ctx);
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String rsType = st.nextToken();

            if ("telr".equalsIgnoreCase(rsType)) {
                /* Set authorization parameters. */
                String address = st.nextToken();
                String secret = st.nextToken();
                String signature = st.nextToken();

                /* Set preferred fiat currency. */
                String preferredFiatCurrency = "USD";
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }

                /* Initialize Telr Rate Source. */
                return new TelrRateSource(
                    address,
                    secret,
                    signature,
                    preferredFiatCurrency
                );
            }
        }
        return null;
    }

}
