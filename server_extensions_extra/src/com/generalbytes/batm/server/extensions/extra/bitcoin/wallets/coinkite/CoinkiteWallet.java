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

package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinkite;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import si.mazi.rescu.RestProxyFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class CoinkiteWallet implements IWallet{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final int CALL_PERIOD_MINIMUM = 4100; //Coinkite cannot be called more often than once in 2 seconds

    private String apiKey;
    private String apiSecret;
    private String accountId;

    private ICoinkiteAPI api;
    public static volatile long lastCall = -1;

    static {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(tz);
    }

    public CoinkiteWallet(String apiKey, String apiSecret, String accountId) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        if (accountId == null) {
            accountId = "0";
        }

        accountId = accountId.replace("#","");
        accountId = Integer.parseInt(accountId) +"";
        this.accountId = accountId;
        api =RestProxyFactory.createProxy(ICoinkiteAPI.class, "https://api.coinkite.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        result.add(ICurrencies.LTC);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return ICurrencies.BTC;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!(ICurrencies.LTC.equalsIgnoreCase(cryptoCurrency) || ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        waitForPossibleCall();
        String timestamp = getTimestamp();
        String sign = sign("/v1/account/"+accountId, timestamp, apiSecret);
        AccountResponse accountResponse = api.getAccount(apiKey, sign, timestamp, accountId);
        lastCall = System.currentTimeMillis();
        if (accountResponse != null && accountResponse.getAccount() != null) {
            if ( accountResponse.getAccount().getQuick_deposit() !=null && accountResponse.getAccount().getCoin_type().equalsIgnoreCase(cryptoCurrency)) {
                return accountResponse.getAccount().getQuick_deposit();
            }
        }

        return null;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!(ICurrencies.LTC.equalsIgnoreCase(cryptoCurrency) || ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        waitForPossibleCall();
        String timestamp = getTimestamp();
        String sign = sign("/v1/account/"+accountId, timestamp, apiSecret);
        AccountResponse accountResponse = api.getAccount(apiKey, sign, timestamp, accountId);
        lastCall = System.currentTimeMillis();
        if (accountResponse != null && accountResponse.getAccount() != null) {
            if ( accountResponse.getAccount().getQuick_deposit() !=null && accountResponse.getAccount().getCoin_type().equalsIgnoreCase(cryptoCurrency)) {
                if (accountResponse.getAccount().getBalance() != null && accountResponse.getAccount().getBalance().getCurrency().equalsIgnoreCase(cryptoCurrency)) {
                    return new BigDecimal(accountResponse.getAccount().getBalance().getString());
                }
            }
        }
        return null;
    }



    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!(ICurrencies.LTC.equalsIgnoreCase(cryptoCurrency) || ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }

        waitForPossibleCall();
        String timestamp = getTimestamp();
        String sign = sign("/v1/new/send", timestamp, apiSecret);
        CoinkiteSendResponse response = api.send(apiKey, sign, timestamp, new CoinkiteSendRequest(description, amount.toPlainString(), accountId, destinationAddress, description));
        lastCall = System.currentTimeMillis();
        if (response != null) {
            if (response.getResult() != null) {
                String ck_refnum = response.getResult().getCK_refnum();
                String send_authcode = response.getResult().getSend_authcode();

//                System.out.println("ckref = " + ck_refnum);
//                System.out.println("authcode = " + send_authcode);

                timestamp = getTimestamp();
                sign = sign("/v1/update/"+ ck_refnum +"/auth_send", timestamp, apiSecret);
                waitForPossibleCall();
                response = api.authSend(apiKey, sign, timestamp, ck_refnum, send_authcode);
                lastCall = System.currentTimeMillis();
                if (response != null ) {
                    if (response.getResult() != null) {
//                        System.out.println("ckref = " + ck_refnum);
                        return response.getResult().getCK_refnum();
                    }
                }



            }
        }

        return null;
    }

    private void waitForPossibleCall() {
        long now = System.currentTimeMillis();
        if (lastCall != -1) {
            long diff = now - lastCall;
//            System.out.println("diff = " + diff);
            if (diff < CALL_PERIOD_MINIMUM) {
                try {
                    long sleeping = CALL_PERIOD_MINIMUM - diff;
//                    System.out.println("sleeping = " + sleeping);
                    Thread.sleep(sleeping);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        lastCall = now;
        return;
    }


    private static String sign(String data, String timestamp, String secret) {
        if (timestamp == null) {
            timestamp = getTimestamp();
        }
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] result = sha256_HMAC.doFinal((data + "|" + timestamp).getBytes());
            return bytesToHexString(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getTimestamp() {
        return sdf.format(new Date());
    }

    public static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
