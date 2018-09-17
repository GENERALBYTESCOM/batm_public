/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
 

public class LskWallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger("batm.master.LskWallet");

    private String address;
    private String binanceApiKey;
    private String binanceApiSecret; 
 
    private ILskBinanceAPI apiBinance;

    public LskWallet(String address, String binanceApiKey, String binanceApiSecret) {

        this.address = address;
        this.binanceApiKey = binanceApiKey;
        this.binanceApiSecret = binanceApiSecret; 

        apiBinance = RestProxyFactory.createProxy(ILskBinanceAPI.class, "https://api.binance.com");
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return Currencies.LSK;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.LSK);
        return result;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        if (address != null) {
            return address;
        }

        return null;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        try {

            String query = "";
            String timeStamp = String.valueOf(new Date().getTime());
            query = "recvWindow=" + 5000 + "&timestamp=" + timeStamp; 
 
            String signing = sign(query, binanceApiSecret);

            final LskBinanceResponse accountInfo = apiBinance.getCryptoBalance(this.binanceApiKey, String.valueOf(5000), timeStamp, signing);

            if (accountInfo != null) {
                List<LskBinanceAssetData> balances = (List<LskBinanceAssetData>) accountInfo.getBalance();
                if(balances != null && !balances.isEmpty()) {
                    for (LskBinanceAssetData assetData : balances) {
                        final String asset = (String) assetData.getAsset(); 
                        BigDecimal value = assetData.getFree(); 
                        if (asset.equals(cryptoCurrency)) {
                            return value;
                        }
                    }
                }
            }
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try { 
            String query = "";
            String timeStamp = String.valueOf(new Date().getTime());
            query = "asset=" + cryptoCurrency + "&address=" + destinationAddress + "&amount=" + amount + "&name=" + "123" + "&recvWindow=" + 5000 + "&timestamp=" + timeStamp;

            String signing = sign(query, binanceApiSecret);
            LskSendCoinResponse response = apiBinance.sendLsks(this.binanceApiKey, cryptoCurrency, destinationAddress, String.valueOf(amount), "123", String.valueOf(5000), timeStamp, signing);
 
            if (response != null && response.getMsg() != null && response.getSuccess()) {
                return response.getMsg();
            }
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }
    
    public static String sign(String message, String secret) {

        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            byte[] bytes = mac.doFinal(message.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();

            for (int i=0; i<bytes.length; i++) {
                String hex = Integer.toHexString(0xFF &  bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }   
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to sign message.", e);
        }
    }
}
