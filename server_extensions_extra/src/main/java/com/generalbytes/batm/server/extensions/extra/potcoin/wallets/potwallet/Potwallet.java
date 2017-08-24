package com.generalbytes.batm.server.extensions.extra.potcoin.wallets.potwallet;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.Random;

public class Potwallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger("batm.master.Potwallet");

    private String nonce;
    private String accessHash;

    private String publicKey;
    private String privateKey;
    private String walletId;

    private PotwalletAPI api;

    public Potwallet(String publicKey, String privateKey, String walletId) {
        this.nonce = generateRandomString(8);

        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.walletId = walletId;

        api = RestProxyFactory.createProxy(PotwalletAPI.class, "https://api.potwallet.com");
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return ICurrencies.POT;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.POT);
        return result;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        if (walletId != null) {
            return walletId;
        }
        try {
            accessHash = generateHash(privateKey, "https://api.potwallet.com/v1/address", nonce);
            PotwalletResponse response = api.getAddress(publicKey, accessHash, nonce);
            if (response != null && response.getMessage() != null && response.getSuccess()) {
                return new String(response.getMessage());
            }
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
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
            accessHash = generateHash(privateKey, "https://api.potwallet.com/v1/balance", nonce);
            PotwalletResponse response = api.getCryptoBalance(publicKey, accessHash, nonce);
            if (response != null && response.getMessage() != null && response.getSuccess()) {
                log.debug("Transaction " + response.getMessage() + " sent.");
                return new BigDecimal(response.getMessage());
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
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        try{
            accessHash = generateHash(privateKey, "https://api.potwallet.com/v1/send", nonce);
            PotwalletResponse response = api.sendPots(publicKey, accessHash, nonce, new PotwalletSendRequest(destinationAddress, amount.stripTrailingZeros()));
            if (response != null && response.getMessage() != null && response.getSuccess()) {
                return new String(response.getMessage());
            }
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    private static String generateRandomString(int length)
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    private static String generateHash(String privateHash, String endPoint, String nonce )
    {
        String digest = null;
        try
        {
            String content = endPoint + nonce;

            SecretKeySpec key = new SecretKeySpec((privateHash).getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            byte[] bytes = mac.doFinal(content.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();

            for (int i=0; i<bytes.length; i++) {
                String hex = Integer.toHexString(0xFF &  bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch(InvalidKeyException e){
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest ;
    }
}
