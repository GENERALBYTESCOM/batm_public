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
package com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet;

import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BinanceWallet implements IWallet {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BinanceWallet");

    private final String address;
    private final String binanceApiKey;
    private final String binanceApiSecret;
    private final String cryptoCurrency;

    private final BinanceWalletAPI apiBinance;

    public BinanceWallet(String address, String binanceApiKey, String binanceApiSecret, String cryptoCurrency) {
        this.address = address;
        this.binanceApiKey = binanceApiKey;
        this.binanceApiSecret = binanceApiSecret;
        this.cryptoCurrency = cryptoCurrency;

        apiBinance = RestProxyFactory.createProxy(BinanceWalletAPI.class, "https://api.binance.com");
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return this.cryptoCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(this.cryptoCurrency);
        return result;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency {} not supported.", cryptoCurrency);
            return null;
        }

        return address;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency {} not supported.", cryptoCurrency);
            return null;
        }
        try {
            String timeStamp = String.valueOf(new Date().getTime());
            String query = String.format("recvWindow=%d&timestamp=%s", 5000, timeStamp);

            String signing = sign(query, binanceApiSecret);

            final BinanceResponse accountInfo = apiBinance.getCryptoBalance(this.binanceApiKey, String.valueOf(5000), timeStamp, signing);

            if (accountInfo != null) {
                List<BinanceAssetData> balances = accountInfo.getBalances();
                if (balances != null && !balances.isEmpty()) {
                    for (BinanceAssetData assetData : balances) {
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

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            String timeStamp = String.valueOf(new Date().getTime());
            String query = String.format("asset=%s&address=%s&amount=%s&name=%s&recvWindow=%d&timestamp=%s",
                cryptoCurrency, destinationAddress, amount, "123", 5000, timeStamp
            );
            String signing = sign(query, binanceApiSecret);
            BinanceSendCoinResponse response = apiBinance.sendCryptoCurrency(
                this.binanceApiKey, cryptoCurrency, destinationAddress, String.valueOf(amount), "123", String.valueOf(5000), timeStamp, signing
            );

            if (response != null && response.getMsg() != null && Boolean.TRUE.equals(response.getSuccess())) {
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
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            byte[] bytes = mac.doFinal(message.getBytes(StandardCharsets.US_ASCII));

            StringBuilder hash = new StringBuilder();

            for (byte b : bytes) {
                String hex = Integer.toHexString(0xFF & b);
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
