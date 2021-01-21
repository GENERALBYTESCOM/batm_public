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
package com.generalbytes.batm.server.extensions.extra.sumcoin;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.sumcoin.sumcored.SumcoinRPCWallet;
import com.generalbytes.batm.server.extensions.extra.sumcoin.sources.sumcoinindex.SumcoinindexRateSource;
import com.generalbytes.batm.server.extensions.extra.sumcoin.sumcored.SumcoinUniqueAddressRPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class SumcoinExtension extends AbstractExtension {
    private static final Logger log = LoggerFactory.getLogger(SumcoinExtension.class);

    private static final ICryptoCurrencyDefinition DEFINITION = new SumcoinDefinition();

    @Override
    public String getName() {
        return "BATM Sumcoin extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("sumcoind".equalsIgnoreCase(walletType)
                || "sumcoindnoforward".equalsIgnoreCase(walletType)) {
                //"sumcoind:protocol:user:password:ip:port:accountname"

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

                if (protocol != null && username != null && password != null && hostname !=null && label != null) {
                    String rpcURL = protocol +"://" + username +":" + password + "@" + hostname +":" + port;
                    if ("sumcoindnoforward".equalsIgnoreCase(walletType)) {
                        return new SumcoinUniqueAddressRPCWallet(rpcURL);
                    }
                    return new SumcoinRPCWallet(rpcURL, label);
                }
            }
            if ("sumdemo".equalsIgnoreCase(walletType)) {

                String fiatCurrency = st.nextToken();
                String walletAddress = "";
                if (st.hasMoreTokens()) {
                    walletAddress = st.nextToken();
                }

                if (fiatCurrency != null && walletAddress != null) {
                    return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.SUM.getCode(), walletAddress);
                }
            }
        }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.SUM.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new SumcoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ("sumcoinindex".equalsIgnoreCase(exchangeType)) {
                if (st.hasMoreTokens()) {
                    return new SumcoinindexRateSource(st.nextToken().toUpperCase());
                }
                return new SumcoinindexRateSource(FiatCurrency.USD.getCode());
            }
            else if ("sumfix".equalsIgnoreCase(exchangeType)) {
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
                return new FixPriceRateSource(rate,preferedFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.SUM.getCode());
        return result;
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
        result.add(DEFINITION);
        return result;
    }

    @Override
    public boolean cancelWalletTunnel(String walletLogin, String tunnelPassword) {
        StringTokenizer st = new StringTokenizer(walletLogin,":");
        String walletType = st.nextToken();

        if ("sumcoind".equalsIgnoreCase(walletType) || "sumcoindnoforward".equalsIgnoreCase(walletType)) {
            // skip protocol, username, password
            st.nextToken();
            st.nextToken();
            st.nextToken();

            String hostname = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            return ctx.getTunnelManager().removeTunnelKnownHost(tunnelPassword, InetSocketAddress.createUnresolved(hostname, port));
        }
        return false;
    }
}
