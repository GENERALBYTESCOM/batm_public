/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BATMBitcoindRPCWallet implements IWallet{
    private static final Logger log = LoggerFactory.getLogger(BATMBitcoindRPCWallet.class);
    private String cryptoCurrency = ICurrencies.BTC;

    private String accountName;
    private BitcoinJSONRPCClient client;


    public BATMBitcoindRPCWallet(String rpcURL, String accountName, String cryptoCurrency) {
        this.accountName = accountName;
        this.cryptoCurrency = cryptoCurrency;
        client = createClient(rpcURL);
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

        log.info("Bitcoind sending coins from " + accountName + " to: " + destinationAddress + " " + amount);
        try {
            String result = client.sendFrom(accountName, destinationAddress, amount.doubleValue());
            log.debug("result = " + result);
            return result;
        } catch (BitcoinException e) {
            e.printStackTrace();
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
            List<String> addressesByAccount = client.getAddressesByAccount(accountName);
            if (addressesByAccount == null || addressesByAccount.size() == 0) {
                return null;
            }else{
                return addressesByAccount.get(0);
            }
        } catch (BitcoinException e) {
            e.printStackTrace();
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
            double balance = client.getBalance(accountName);
            return BigDecimal.valueOf(balance);
        } catch (BitcoinException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BitcoinJSONRPCClient createClient(String rpcURL) {
        try {
            final BitcoinJSONRPCClient bitcoinJSONRPCClient = new BitcoinJSONRPCClient(rpcURL);
            bitcoinJSONRPCClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[] {new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

                    @Override
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }}, null);
                bitcoinJSONRPCClient.setSslSocketFactory(sslcontext.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            return bitcoinJSONRPCClient;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
