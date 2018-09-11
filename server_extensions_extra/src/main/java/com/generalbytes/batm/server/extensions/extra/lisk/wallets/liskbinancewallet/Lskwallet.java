package com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.Random; 

public class Lskwallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger("batm.master.Lskwallet");

    private String nonce;
    private String accessHash;

    private String address;
    private String binanceApiKey;
    private String binanceApiSecret; 
 
    private LskBinanceAPI apiBinance;

    public Lskwallet(String address, String binanceApiKey, String binanceApiSecret) {
        this.nonce = generateRandomString(8);

        this.address = address;
        this.binanceApiKey = binanceApiKey;
        this.binanceApiSecret = binanceApiSecret; 

        apiBinance = RestProxyFactory.createProxy(LskBinanceAPI.class, "https://api.binance.com");
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

            final Map<String, Object> accountInfo = apiBinance.getCryptoBalance(this.binanceApiKey, String.valueOf(5000), timeStamp, signing);
 
            if (accountInfo != null && !accountInfo.isEmpty()) {
                final List<Object> balances = (List<Object>) accountInfo.get("balances");

                if(balances != null && !balances.isEmpty()) {
                    for (Object dataObject : balances) {
                        final Map<String, Object> map = (Map<String, Object>) dataObject;
                        final String asset = (String) map.get("asset"); 
                        BigDecimal value = new BigDecimal((String) map.get("free"));

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
        try{ 
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

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    public static String sign(String message, String secret) {
        try {
          Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
          SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
          sha256_HMAC.init(secretKeySpec);
          return new String(Hex.encodeHex(sha256_HMAC.doFinal(message.getBytes())));
        } catch (Exception e) {
          throw new RuntimeException("Unable to sign message.", e);
        }
    }
}
