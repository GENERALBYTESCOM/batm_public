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
package com.generalbytes.batm.server.extensions.extra.dogecoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.BlockIOWalletWithClientSideSigning;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.BlockIOWalletWithClientSideSigningWithUniqueAddresses;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.dogecoind.DogecoindRPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class DogecoinExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(DogecoinExtension.class);

    @Override
    public String getName() {
        return "BATM Dogecoin extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
            if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(walletLogin,":");
                String walletType = st.nextToken();

                if ("blockio".equalsIgnoreCase(walletType)) {
                    String apikey = st.nextToken();
                    String pin = st.nextToken();
                    String priority = null;
                    if (st.hasMoreTokens()) {
                        priority = st.nextToken();
                    }
                    String fromLabel = null;
                    if (st.hasMoreTokens()) {
                        fromLabel = st.nextToken();
                    }
                    return new BlockIOWalletWithClientSideSigning(apikey, pin, priority, fromLabel);

                } else if ("blockionoforward".equalsIgnoreCase(walletType)) {
                    String apikey = st.nextToken();
                    String pin = st.nextToken();
                    String priority = null;
                    if (st.hasMoreTokens()) {
                        priority = st.nextToken();
                    }
                    String fromLabel = null;
                    if (st.hasMoreTokens()) {
                        fromLabel = st.nextToken();
                    }
                    return new BlockIOWalletWithClientSideSigningWithUniqueAddresses(apikey,pin, priority, fromLabel);

                } else if ("dogecoind".equalsIgnoreCase(walletType)) {
                    //"dogecoind:protocol:user:password:ip:port:accountname"

                    String protocol = st.nextToken();
                    String username = st.nextToken();
                    String password = st.nextToken();
                    String hostname = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String accountName ="";
                    if (st.hasMoreTokens()) {
                        accountName = st.nextToken();
                    }

                    InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(hostname, port));
                    hostname = tunnelAddress.getHostString();
                    port = tunnelAddress.getPort();

                    if (protocol != null && username != null && password != null && hostname !=null && accountName != null) {
                        String rpcURL = protocol +"://" + username +":" + password + "@" + hostname +":" + port;
                        return new DogecoindRPCWallet(rpcURL,accountName);
                    }
                }
                if ("dogedemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.DOGE.getCode(), walletAddress);
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
        if (CryptoCurrency.DOGE.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new DogecoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ("dogefix".equalsIgnoreCase(exchangeType)) {
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
        result.add(CryptoCurrency.DOGE.getCode());
        return result;
    }

}
