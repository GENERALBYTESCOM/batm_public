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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind;

import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

public class BATMBitcoindRPCWallet implements IWallet{
    private static final Logger log = LoggerFactory.getLogger(BATMBitcoindRPCWallet.class);
    private final String cryptoCurrency;
    private final BitcoinJSONRPCClient client;

    public BATMBitcoindRPCWallet(String rpcURL, String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
        this.client = createClient(rpcURL);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(cryptoCurrency);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Bitcoind wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            String result;
            log.info("Bitcoind sending coins to: " + destinationAddress + " " + amount);
            result = client.sendToAddress(destinationAddress, amount, description);
            log.debug("result = " + result);
            return result;
        } catch (BitcoinRPCException e) {
            log.error("Error in sendCoins", e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Bitcoind wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            return client.getNewAddress();
        } catch (BitcoinRPCException e) {
            log.error("Error in getCryptoAddress", e);
            return null;
        }
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Bitcoind wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return client.getBalance();
        } catch (BitcoinRPCException e) {
            log.error("Error in getCryptoBalance", e);
            return null;
        }
    }

    private static BitcoinJSONRPCClient createClient(String rpcURL) {
        try {
            final BitcoinJSONRPCClient bitcoinJSONRPCClient = new BitcoinJSONRPCClient(rpcURL);
            bitcoinJSONRPCClient.setHostnameVerifier((s, sslSession) -> true);

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

                @Override
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }}, null);
            bitcoinJSONRPCClient.setSslSocketFactory(sslcontext.getSocketFactory());
            return bitcoinJSONRPCClient;
        } catch (MalformedURLException | NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Error", e);
            return null;
        }
    }
}
