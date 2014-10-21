/*************************************************************************************
 * Copyright (C) 2014 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay.BitcoinPayPP;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.BitcoinAverageRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind.BATMBitcoindRPCWallet;

import java.math.BigDecimal;
import java.util.*;

public class BitcoinExtension implements IExtension{

    @Override
    public String getName() {
        return "BATM Bitcoin extra extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        return null; //no BTC exchange available in open source version so far (Bitstamp is in built-in extension)
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        if (paymentProcessorLogin !=null && !paymentProcessorLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(paymentProcessorLogin,":");
            String processorType = st.nextToken();
            if ("bitcoinpay".equalsIgnoreCase(processorType)) { //bitcoinpay:msciu823jes
                if (st.hasMoreTokens()) {
                    String apiKey = st.nextToken();
                    return new BitcoinPayPP(apiKey);
                }
            }
        }
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();
            if ("bitcoind".equalsIgnoreCase(walletType)) {
                //"bitcoind:protocol:user:password:ip:port:accountname"

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
                    return new BATMBitcoindRPCWallet(rpcURL,accountName);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        return null; //no BTC address validator in open source version so far (It is present in built-in extension)
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null; //no BTC paper wallet generator in open source version so far (It is present in built-in extension)
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        //NOTE: (Bitstamp is in built-in extension)
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ("bitcoinaverage".equalsIgnoreCase(exchangeType)) {
                return new BitcoinAverageRateSource();
            }else if ("btcfix".equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                return new FixPriceRateSource(rate);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        return result;
    }

}
