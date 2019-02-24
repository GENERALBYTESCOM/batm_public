/*
 *
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
 */
package com.generalbytes.batm.server.extensions.extra.nuls.wallet.binance;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.nuls.NulsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author naveen
 */
public class NulsWallet implements IWallet {

    private static final Logger log = LoggerFactory.getLogger("batm.master.NulsWallet");

    private String address;
    private String binanceApiKey;
    private String binanceApiSecret;

    private INulsBinanceApi apiBinance;

    public NulsWallet(String address, String binanceApiKey, String binanceApiSecret) {
        this.address = address;
        this.binanceApiKey = binanceApiKey;
        this.binanceApiSecret = binanceApiSecret;
        apiBinance = RestProxyFactory.createProxy(INulsBinanceApi.class, NulsConstants.BINANCE_API_BASE_URL);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String query = "asset=" + cryptoCurrency + "&address=" + destinationAddress + "&amount=" + amount + "&name=" + "123" + "&recvWindow=" + 5000 + "&timestamp=" + timeStamp;

            String signing = sign(query, binanceApiSecret);
            NulsSendCoinResponse response = apiBinance.sendNuls(this.binanceApiKey, cryptoCurrency, destinationAddress, String.valueOf(amount), "123", String.valueOf(5000), timeStamp, signing);

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
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.NULS.getCode());
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.NULS.getCode();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        try {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String query = "recvWindow=" + 5000 + "&timestamp=" + timeStamp;

            String signing = sign(query, binanceApiSecret);
            final NulsBinanceResponse accountInfo = apiBinance.getCryptoBalance(this.binanceApiKey, String.valueOf(5000), timeStamp, signing);
            if (accountInfo != null) {
                List<NulsBinanceAssetData> balances =  accountInfo.getBalance();
                if(balances != null && !balances.isEmpty()) {
                    for (NulsBinanceAssetData assetData : balances) {
                        final String asset = assetData.getAsset();
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

    private static String sign(String message, String secret) {
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
