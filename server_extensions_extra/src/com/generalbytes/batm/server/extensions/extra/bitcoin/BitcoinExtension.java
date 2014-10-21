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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IExtension;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IPaymentProcessor;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.BitfinexExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay.BitcoinPayPP;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.BitcoinAverageRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind.BATMBitcoindRPCWallet;

public class BitcoinExtension implements IExtension{

    @Override
    public String getName() {
        return "BATM Bitcoin extra extension";
    }

    @Override
    public IExchange createExchange(String paramString)
    {
      if ((paramString != null) && (!paramString.trim().isEmpty()))
      {
        StringTokenizer paramTokenizer = new StringTokenizer(paramString, ":");
        String prefix = paramTokenizer.nextToken();
        if ("bitfinex".equalsIgnoreCase(prefix)) {
          String keyID = paramTokenizer.nextToken();
          String keySecret = paramTokenizer.nextToken();
          return new BitfinexExchange(keyID, keySecret);
        }
      }
      return null;
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
        //NOTE: (Bitstamp is in built-in extension and Bitfinex is in the Bitfinex Exchange class)
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
